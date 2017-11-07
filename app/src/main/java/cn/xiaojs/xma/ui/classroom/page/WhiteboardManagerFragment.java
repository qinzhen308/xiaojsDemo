package cn.xiaojs.xma.ui.classroom.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
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
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.stateview.LoadStateListener;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.ScheduleLesson;
import cn.xiaojs.xma.model.live.Board;
import cn.xiaojs.xma.model.live.BoardCriteria;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.ui.base.CommonRVAdapter;
import cn.xiaojs.xma.ui.classroom2.base.BaseDialogFragment;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.schedule.SLOpModel;
import cn.xiaojs.xma.ui.lesson.xclass.view.LessonOperateBoard;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ToastUtil;

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
    public static final String EXTRA_SELECTED_BOARD="extra_selected_board";


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
        mAdapter.setCallback(new EventCallback() {
            @Override
            public void onEvent(int what, Object... object) {
                switch (what){
                    case EVENT_1://打开本白板
                        openBoard((int)object[1],(WhiteboardModel) object[0]);
                        break;
                    case EVENT_2://更多
                        showMoreDialog((int)object[1],(WhiteboardModel) object[0]);
                        break;
                }
            }
        });
    }

    private void openBoard(final int itemPosition, final WhiteboardModel data){
        if(getTargetFragment() !=null){
            Intent intent=new Intent();
            intent.putExtra(EXTRA_SELECTED_BOARD,data.boardItem);
            getTargetFragment().onActivityResult(getTargetRequestCode(),Activity.RESULT_OK,intent);
        }
        dismiss();
    }

    private void showMoreDialog(final int itemPosition, final WhiteboardModel data){
        ListBottomDialog dialog=new ListBottomDialog(getActivity());

        dialog.setItems(getResources().getStringArray(R.array.board_manager_operate));
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                if(position==0){
                    rename(itemPosition,data);
                }else if(position==1){
                    deleteBoard(itemPosition,data);
                }
            }
        });
        dialog.show();
    }


    private void deleteBoard(int position,WhiteboardModel data){

    }

    private void rename(int position,WhiteboardModel data){

    }


    public void registBoard(){
        final Board board=new Board();
        board.title="新的白板";
        board.drawing=new Board.DrawDimension();
        board.drawing.height=810;
        board.drawing.width=1440;
        ClassroomEngine.getEngine().registerBoard(ClassroomEngine.getEngine().getTicket(), board, new APIServiceCallback<BoardItem>() {
            @Override
            public void onSuccess(BoardItem object) {
                dataPageLoader.refresh();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getActivity(),errorMessage+",后续操作无法保存");
            }
        });
    }



    @OnClick({R.id.left_image, R.id.right_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                dismiss();
                break;
            case R.id.right_view:
                registBoard();
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
