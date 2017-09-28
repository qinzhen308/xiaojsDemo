package cn.xiaojs.xma.ui.classroom2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.live.MemberAdapter;

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

        LiveManager.getAttendees(getContext(), classroomEngine.getTicket(),
                new APIServiceCallback<LiveCollection<Attendee>>() {
            @Override
            public void onSuccess(LiveCollection<Attendee> liveCollection) {


                MemberAdapter memberAdapter = new MemberAdapter(getContext(),
                        liveCollection.attendees);

                recyclerView.setAdapter(memberAdapter);


            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });

    }
}
