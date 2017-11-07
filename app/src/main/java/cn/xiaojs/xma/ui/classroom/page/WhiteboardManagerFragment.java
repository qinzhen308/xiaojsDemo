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
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.stateview.LoadStateListener;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.ScheduleLesson;
import cn.xiaojs.xma.model.live.BoardCriteria;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.ui.base.CommonRVAdapter;
import cn.xiaojs.xma.ui.classroom2.base.BaseDialogFragment;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.util.ArrayUtil;

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

    DataPageLoader<WhiteboardModel, CollectionPage<BoardItem>> dataPageLoader;
    Pagination mPagination;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initPageLoad();
        getBoards();
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
        criteria.state= Live.BoardState.OPEN;
        ClassroomEngine.getEngine().getBoards(criteria, mPagination, dataPageLoader);
    }

    private void initPageLoad() {
        mPagination = new Pagination();
        mPagination.setPage(1);
        mPagination.setMaxNumOfObjectsPerPage(10);
        dataPageLoader = new DataPageLoader<WhiteboardModel, CollectionPage<BoardItem>>() {
            PageChangeInRecyclerView pageChangeInRecyclerView;

            @Override
            public void onRequst(int page) {
                mPagination.setPage(page);
                getBoards();
            }

            @Override
            public List<WhiteboardModel> adaptData(CollectionPage<BoardItem> object) {
                List<WhiteboardModel> dest=new ArrayList<>();
                if (object != null&&!ArrayUtil.isEmpty(object.objectsOfPage)){
                    for(int i=0;i<object.objectsOfPage.size();i++){
                        dest.add(new WhiteboardModel(object.objectsOfPage.get(i)));
                    }
                }
                return dest;
            }

            @Override
            public void onSuccess(List<WhiteboardModel> curPage, List<WhiteboardModel> all) {
                pageChangeInRecyclerView.completeLoading();
                if (!ArrayUtil.isEmpty(all)) {
                    mAdapter.setList(all);
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                pageChangeInRecyclerView.completeLoading();
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView = new PageChangeInRecyclerView(rvWhiteboard, this);
            }

        };
    }
}
