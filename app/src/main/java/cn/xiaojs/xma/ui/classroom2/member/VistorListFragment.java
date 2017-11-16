package cn.xiaojs.xma.ui.classroom2.member;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.chat.SingleSessionFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class VistorListFragment extends BottomSheetFragment implements DialogInterface.OnKeyListener {

    @BindView(R.id.cl_root)
    ConstraintLayout rootLay;
    @BindView(R.id.vistor_list)
    RecyclerView recyclerView;
    @BindView(R.id.title)
    TextView titleView;

    private VistorAdapter vistorAdapter;
    private EventListener.ELMember eventListener;

    private ArrayList<Attendee> vistors;


    public static void invokeShow(FragmentManager manager, ArrayList<Attendee> attendees) {

        VistorListFragment vistorListFragment = new VistorListFragment();
        Bundle b = new Bundle();
        b.putSerializable(CTLConstant.EXTRA_MEMBER, attendees);
        vistorListFragment.setArguments(b);
        vistorListFragment.show(manager, "vistor");

    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_vistorlist, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getDialog() == null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rootLay.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            rootLay.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            getDialog().setOnKeyListener(this);
        }

        vistors = (ArrayList<Attendee>) getArguments().getSerializable(CTLConstant.EXTRA_MEMBER);

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        vistorAdapter = new VistorAdapter(VistorListFragment.this, getContext(), vistors);

        recyclerView.setAdapter(vistorAdapter);

        if (vistorAdapter.getItemCount() <= 0) {
            showFinalTips();
        }

        eventListener = classroomEngine.observerMember(memberConsumer);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (eventListener != null) {
            eventListener.dispose();
            eventListener = null;
        }
    }

    @OnClick({R.id.back_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            dismiss();
            return true;
        }

        return false;
    }

    public void enterChatSession(Attendee attendee) {

        if (classroomEngine.isVistor()) {
            Toast.makeText(getContext(), R.string.no_permision_tips, Toast.LENGTH_SHORT).show();
            return;
        }


        DataProvider dataProvider = DataProvider.getProvider(getContext());
        int followType = Social.FllowType.NA;
        if (dataProvider.existInContact(attendee.accountId)) {
            followType = Social.FllowType.FOLLOW_SHIP;
        }

        SingleSessionFragment.invoke(getFragmentManager(),
                attendee.accountId, attendee.name, followType);
    }

    private void join(Talk talk) {
        if (vistors == null) {
            vistors = new ArrayList<>();
        }

        Attendee attendee = new Attendee();
        attendee.name = talk.name;
        attendee.xa = talk.xa;
        attendee.xav = talk.xav;
        attendee.accountId = talk.accountId;
        attendee.sort = talk.sort;

        vistors.add(attendee);
        vistorAdapter.notifyDataSetChanged();

        if (vistorAdapter.getItemCount() >0) {
            hiddenTips();
        }else {
            showFinalTips();
        }
    }

    private void leave(String accountId) {
        if (vistors == null)
            return;

        for (int i = 0; i < vistors.size(); i++) {

            Attendee attendee = vistors.get(i);
            if (attendee.accountId.equals(accountId)) {
                vistors.remove(i);
                vistorAdapter.notifyDataSetChanged();
                if (vistorAdapter.getItemCount() >0) {
                    hiddenTips();
                }else {
                    showFinalTips();
                }
                break;
            }
        }
    }


    private Consumer<EventReceived> memberConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {

            switch (eventReceived.eventType) {
                case Su.EventType.JOIN:
                    Talk talk = (Talk) eventReceived.t;
                    if (talk != null) {

                        if (classroomEngine.getUserIdentity(talk.psType) == CTLConstant.UserIdentity.VISITOR) {
                            join(talk);
                        }


                    }
                    break;
                case Su.EventType.LEAVE:
                    Talk rat = (Talk) eventReceived.t;
                    if (rat != null) {
                        leave(rat.accountId);
                    }
                    break;
            }
        }
    };
}
