package cn.xiaojs.xma.ui.classroom2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.page.MsgInputFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.chat.MessageComparator;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;

/**
 * Created by maxiaobao on 2017/9/25.
 */

public class ChatFragment extends Fragment {

    @BindView(R.id.chat_list)
    RecyclerView recyclerView;

    private ClassroomEngine classroomEngine;
    private LiveCriteria liveCriteria;
    private Pagination pagination;
    private int currentPage = 1;

    private ArrayList<TalkItem> messageData;
    private ChatAdapter adapter;
    private MessageComparator messageComparator;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classroom2_chat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.bottom_input, R.id.bottom_members, R.id.bottom_database, R.id.bottom_schedule})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.bottom_input:
                popInput();
                break;
            case R.id.bottom_members:               //教室成员
                popMembers();
                break;
            case R.id.bottom_database:              //资料库
                popDatabase();
                break;
            case R.id.bottom_schedule:              //课表
                break;
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        classroomEngine = ClassroomEngine.getEngine();
        messageData = new ArrayList<>();
        messageComparator = new MessageComparator();

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL,true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(getContext(),messageData);
        recyclerView.setAdapter(adapter);


        pagination = new Pagination();
        pagination.setMaxNumOfObjectsPerPage(20);
        pagination.setPage(currentPage);

        liveCriteria = new LiveCriteria();
        liveCriteria.to = String.valueOf(Communications.TalkType.OPEN);

        loadData();
    }

    private void loadData() {
        LiveManager.getTalks(getContext(), classroomEngine.getTicket(),
                liveCriteria, pagination, new APIServiceCallback<CollectionPage<TalkItem>>() {
            @Override
            public void onSuccess(CollectionPage<TalkItem> object) {
                if (object !=null && object.objectsOfPage !=null && object.objectsOfPage.size()>0) {

                    //Collections.sort(object.objectsOfPage, messageComparator);
                    messageData.addAll(0,object.objectsOfPage);
                    pagination.setPage(currentPage++);
                    //更新视图
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                Toast.makeText(getContext(), "获取消息列表失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void popInput() {
        MsgInputFragment inputFragment = new MsgInputFragment();
        Bundle data = new Bundle();
        data.putInt(Constants.KEY_MSG_INPUT_FROM, 1);
        inputFragment.show(getFragmentManager(), "input");
    }


    private void popMembers() {
        MemberListFragment memberListfragment = new MemberListFragment();
        memberListfragment.show(getFragmentManager(), "member");
    }

    private void popDatabase() {
        DatabaseFragment databaseFragment = new DatabaseFragment();
        databaseFragment.show(getFragmentManager(), "database");
    }



}
