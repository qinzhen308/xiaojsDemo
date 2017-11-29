package cn.xiaojs.xma.ui.classroom.page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kaola.qrcodescanner.qrcode.utils.ScreenUtils;

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
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ToastUtil;
import okhttp3.ResponseBody;

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

    String selectedId;


    public static final String EXTRA_SELECTED_BOARD_ID = "extra_selected_board_id";
    public static final String EXTRA_SELECTED_BOARD = "extra_selected_board";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            selectedId=getArguments().getString(EXTRA_SELECTED_BOARD_ID);
        }
    }

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
        rvWhiteboard.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
        mAdapter = new CommonRVAdapter(rvWhiteboard);
        rvWhiteboard.setAdapter(mAdapter);
        //test
        ArrayList<WhiteboardModel> datas = new ArrayList<>();
        mAdapter.setList(datas);
        mAdapter.notifyDataSetChanged();
        mAdapter.setCallback(new EventCallback() {
            @Override
            public void onEvent(int what, Object... object) {
                switch (what) {
                    case EVENT_1://打开本白板
                        openBoard((int) object[1], (WhiteboardModel) object[0]);
                        break;
                    case EVENT_2://更多
                        showMoreDialog((int) object[1], (WhiteboardModel) object[0]);
                        break;
                }
            }
        });
    }

    private void openBoard(final int itemPosition, final WhiteboardModel data) {
        if (getTargetFragment() != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_SELECTED_BOARD, data.boardItem);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
        dismiss();
    }

    private void showMoreDialog(final int itemPosition, final WhiteboardModel data) {
        ListBottomDialog dialog = new ListBottomDialog(getActivity());

        dialog.setItems(getResources().getStringArray(R.array.board_manager_operate));
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                if (position == 0) {
                    showRenameDialog(itemPosition, data);
                } else if (position == 1) {
                    showDeleteBoardDialog(itemPosition, data);
                }
            }
        });
        dialog.show();
    }


    private void deleteBoard(final int position, final WhiteboardModel data) {

        ClassroomEngine.getEngine().closeBoard(data.boardItem.id, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                ClassroomEngine.getEngine().deleteBoard(data.boardItem.id, new APIServiceCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody object) {
                        ToastUtil.showToast(getActivity(), "删除成功");
                        rvWhiteboard.scrollToPosition(0);
                        dataPageLoader.refresh();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        ToastUtil.showToast(getActivity(), errorMessage);
                    }
                });
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getActivity(), errorMessage);
            }
        });

    }

    private void showDeleteBoardDialog(final int position, final WhiteboardModel data){
        if(data.isSelected){
            ToastUtil.showToast(getActivity(),R.string.whiteboard_cant_remove);
            return;
        }
        final CommonDialog dialog = new CommonDialog(getActivity());
        dialog.setDesc("是否删除白板“" + data.boardItem.title + "”？");
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.setRightBtnText(R.string.delete);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                deleteBoard(position, data);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showRenameDialog(final int position, final WhiteboardModel data) {
        final CommonDialog dialog = new CommonDialog(getActivity());
        dialog.setTitle(R.string.rename);
        final EditTextDel editText = new EditTextDel(getActivity());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.px20);
        lp.topMargin = getResources().getDimensionPixelSize(R.dimen.px20);
        editText.setLayoutParams(lp);
        editText.setHint("请输入白板名称（50字内）");
        editText.setLines(1);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        editText.setTextColor(getResources().getColor(R.color.font_black));
        editText.setBackgroundResource(R.drawable.common_search_bg);
        editText.setGravity(Gravity.LEFT | Gravity.TOP);
        int padding = getResources().getDimensionPixelSize(R.dimen.px10);
        editText.setPadding(padding, padding, padding, padding);
        editText.setHintTextColor(getResources().getColor(R.color.font_gray));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.font_28px));
        editText.setText(data.boardItem.title);
        dialog.setCustomView(editText);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                String newTitle = editText.getText().toString();
                if (TextUtils.isEmpty(newTitle)) {
                    ToastUtil.showToast(getActivity(), "请输入白板名");
                    return;
                }
                if (newTitle.equals(data.boardItem.title)) {
                    dialog.dismiss();
                    return;
                }
                rename(position, data, newTitle);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void rename(int position, final WhiteboardModel data, final String newTitle) {


        ClassroomEngine.getEngine().renameBoard(data.boardItem.id, newTitle, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                data.boardItem.title = newTitle;
                mAdapter.notifyDataSetChanged();
                ToastUtil.showToast(getActivity(), "修改成功");
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getActivity(), errorMessage);
            }
        });

    }


    public void registBoard() {
        final Board board = new Board();
        board.title = "新的白板";
        board.drawing = new Board.DrawDimension();
        board.drawing.width = ScreenUtils.getScreenWidth(getActivity());
        board.drawing.height = board.drawing.width * 9 / 16;
        ClassroomEngine.getEngine().registerBoard(ClassroomEngine.getEngine().getTicket(), board, new APIServiceCallback<BoardItem>() {
            @Override
            public void onSuccess(BoardItem object) {
                dataPageLoader.refresh();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getActivity(), errorMessage + ",后续操作无法保存");
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

    public static WhiteboardManagerFragment createInstance(String selectedBoardId) {
        WhiteboardManagerFragment fragment = new WhiteboardManagerFragment();
        if (!TextUtils.isEmpty(selectedBoardId)) {
            Bundle data = new Bundle();
            data.putString(EXTRA_SELECTED_BOARD_ID, selectedBoardId);
            fragment.setArguments(data);
        }
        return fragment;
    }

    public void getBoards() {
        BoardCriteria criteria = new BoardCriteria();
        criteria.state = Live.BoardState.OPEN;
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
                List<WhiteboardModel> dest = new ArrayList<>();
                if (object != null && !ArrayUtil.isEmpty(object.objectsOfPage)) {
                    WhiteboardModel model=null;
                    for (int i = 0; i < object.objectsOfPage.size(); i++) {
                        model=new WhiteboardModel(object.objectsOfPage.get(i));
                        if(model.boardItem.id.equals(selectedId)){
                            model.isSelected=true;
                        }
                        dest.add(model);
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
