package cn.xiaojs.xma.ui.classroom2.member;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class MemberListFragment extends BottomSheetFragment {


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


//        LiveManager.getAttendees(getContext(), classroomEngine.getTicket(),
//                new APIServiceCallback<LiveCollection<Attendee>>() {
//            @Override
//            public void onSuccess(LiveCollection<Attendee> liveCollection) {
//
//
//                MemberAdapter memberAdapter = new MemberAdapter(MemberListFragment.this
//                        ,getContext(), liveCollection.attendees);
//
//                recyclerView.setAdapter(memberAdapter);
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        });

    }

    @OnClick({R.id.add_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                addStudents();
                break;
        }
    }

    public void enterChatSession() {
        SingleSessionFragment.invoke(getFragmentManager());
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
