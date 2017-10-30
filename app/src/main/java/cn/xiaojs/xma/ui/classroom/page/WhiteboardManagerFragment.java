package cn.xiaojs.xma.ui.classroom.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.live.BoardCriteria;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.ui.base.CommonRVAdapter;
import cn.xiaojs.xma.ui.classroom2.base.BaseDialogFragment;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;

/**
 * Created by Paul Z on 2017/10/18.
 */

public class WhiteboardManagerFragment extends BaseDialogFragment {
    @BindView(R.id.rv_whiteboard)
    RecyclerView rvWhiteboard;
    @BindView(R.id.middle_view)
    TextView middleView;
    @BindView(R.id.right_view)
    TextView rightView;

    CommonRVAdapter mAdapter;

    public static final String EXTRA_SELECTED_BOARD_ID="extra_selected_board_id";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = getContentView();
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    protected View getContentView() {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_classroom_board_manager, null);
    }

    protected void init() {
        middleView.setText("白板");
        rightView.setText("新增");
        rvWhiteboard.setLayoutManager(new GridLayoutManager(getActivity(),3,GridLayoutManager.VERTICAL,false));
        mAdapter=new CommonRVAdapter(rvWhiteboard);
        rvWhiteboard.setAdapter(mAdapter);
        //test
        ArrayList<WhiteboardModel> datas=new ArrayList<>();
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        datas.add(new WhiteboardModel());
        datas.get(3).isSelected=true;
        mAdapter.setList(datas);
        mAdapter.notifyDataSetChanged();
    }




    @OnClick({R.id.left_image, R.id.right_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                dismiss();
                break;
            case R.id.right_view:
                break;
        }
    }

    public static WhiteboardManagerFragment createInstance(String selectedBoardId){
        WhiteboardManagerFragment fragment=new WhiteboardManagerFragment();
        if(!TextUtils.isEmpty(selectedBoardId)){
            Bundle data=new Bundle();
            data.putString(EXTRA_SELECTED_BOARD_ID,selectedBoardId);
            fragment.setArguments(data);
        }
        return fragment;
    }

    public void getBoards(){
        BoardCriteria criteria=new BoardCriteria();
        Pagination pagination=new Pagination();
        ClassroomEngine.getEngine().getBoards(criteria, pagination, new APIServiceCallback<CollectionPage<BoardItem>>() {
            @Override
            public void onSuccess(CollectionPage<BoardItem> object) {


            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }
}
