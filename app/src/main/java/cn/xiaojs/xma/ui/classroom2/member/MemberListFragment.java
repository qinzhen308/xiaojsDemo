package cn.xiaojs.xma.ui.classroom2.member;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatSessionFragment;
import cn.xiaojs.xma.ui.classroom2.chat.SingleSessionFragment;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class MemberListFragment extends BottomSheetFragment implements DialogInterface.OnKeyListener{

    @BindView(R.id.cl_root)
    ConstraintLayout rootLay;
    @BindView(R.id.member_list)
    RecyclerView recyclerView;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_memberlist, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getDialog()==null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rootLay.getLayoutParams();
            params.setMargins(0,0,0,0);
            rootLay.setBackgroundColor(getResources().getColor(R.color.white));
        }else {
            getDialog().setOnKeyListener(this);
        }


        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Attendee> attendees = new ArrayList<>();
        Map<String, Attendee> attendeeMap = classroomEngine.getMembers();
        if (attendeeMap != null && attendeeMap.size()>0) {
            attendees.addAll(attendeeMap.values());
        }

        MemberAdapter memberAdapter = new MemberAdapter(MemberListFragment.this
                ,getContext(), attendees);

        recyclerView.setAdapter(memberAdapter);

        if (memberAdapter.getItemCount() <= 0) {
            showFinalTips();
        }

    }

    @OnClick({R.id.add_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                addStudents();
                break;
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
            dismiss();
            return true;
        }

        return false;
    }

    public void enterChatSession(Attendee attendee) {
        SingleSessionFragment.invoke(getFragmentManager(),attendee.accountId, attendee.name);
    }


    private void addStudents() {

        ListBottomDialog dialog = new ListBottomDialog(getContext());

        String[] items = getResources().getStringArray(R.array.classroom2_add_student);
        dialog.setItems(items);
        dialog.setTitleVisibility(View.GONE);
        dialog.setRightBtnVisibility(View.GONE);
        dialog.setLeftBtnVisibility(View.GONE);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://手机号添加
                        AddByPhoneFragment addByPhoneFragment = new AddByPhoneFragment();
                        addByPhoneFragment.show(getFragmentManager(), "addphone");
                        break;
                    case 1://从通讯录中添加
                        ChooseContactFragment chooseContactFragment = new ChooseContactFragment();
                        chooseContactFragment.show(getFragmentManager(), "choose_contact");
                        break;
                }

            }

        });
        dialog.show();
    }
}
