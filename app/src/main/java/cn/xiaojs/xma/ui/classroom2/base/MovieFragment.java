package cn.xiaojs.xma.ui.classroom2.base;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaResponse;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom.page.BoardCollaborateFragment;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import cn.xiaojs.xma.ui.classroom.page.MsgInputFragment;
import cn.xiaojs.xma.ui.classroom2.ClassDetailFragment;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.SettingFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.chat.ChatLandAdapter;
import cn.xiaojs.xma.ui.classroom2.chat.MessageComparator;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.material.DatabaseFragment;
import cn.xiaojs.xma.ui.classroom2.util.NetworkUtil;
import cn.xiaojs.xma.ui.lesson.xclass.util.RecyclerViewScrollHelper;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.ui.widget.ClosableAdapterSlidingLayout;
import cn.xiaojs.xma.ui.widget.ClosableSlidingLayout;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public abstract class MovieFragment extends BaseRoomFragment
        implements ClosableSlidingLayout.SlideListener {

    @BindView(R.id.control_port)
    public ConstraintLayout controlPort;


    @BindView(R.id.l_top_start_or_stop_living)
    public TextView startOrStopLiveView;
    @BindView(R.id.l_top_photo)
    public ImageView lTopPhotoView;
    @BindView(R.id.l_top_roominfo)
    public TextView lTopRoominfoView;

    @BindView(R.id.l_bottom_session)
    public ImageView lBottomSessionView;

    @BindView(R.id.l_right_screenshot)
    public ImageView lRightScreenshortView;
    @BindView(R.id.l_right_switchcamera)
    public ImageView lRightSwitchcameraView;


    @BindView(R.id.center_panel)
    View centerPanelView;
    @BindView(R.id.center_one2one)
    TextView centerOne2oneView;
    @BindView(R.id.center_board_opera)
    TextView centerBoardOperaView;
    @BindView(R.id.center_board_mgr)
    TextView centerBoardMgrView;
    @BindView(R.id.center_new_board)
    TextView centerNewBoardView;
    @BindView(R.id.center_member)
    TextView centerMedmberView;
    @BindView(R.id.center_database)
    TextView centerDatabaseView;
    @BindView(R.id.center_canlender)
    TextView centerCanlenderView;


    @BindView(R.id.control_land)
    public ConstraintLayout controlLand;


    @BindView(R.id.p_bottom_avator)
    public ImageView pBottomAvatorView;
    @BindView(R.id.p_bottom_class_name)
    public TextView pBottomClassnameView;
    @BindView(R.id.p_top_live)
    public TextView pTopLiveView;


    /////////////
    @BindView(R.id.right_slide_layout)
    public FrameLayout rightSlideLayout;
    @BindView(R.id.slide_layout)
    public ClosableAdapterSlidingLayout slideLayout;


    @BindView(R.id.chat_list)
    public RecyclerView recyclerView;
    private LiveCriteria liveCriteria;
    private Pagination pagination;
    private int currentPage = 1;
    private ArrayList<TalkItem> messageData;
    private ChatLandAdapter adapter;
    private MessageComparator messageComparator;
    private boolean loading = false;
    private long lastTimeline = 0;


    public final static int REQUEST_PERMISSION = 3;

    protected ClassroomEngine classroomEngine;
    protected Fragment slideFragment;
    protected Attendee o2oAttendee;

    //这个白板fragment对象的留存时间依赖于Activity
    //但生命周期取决于使用他的fragment
    protected BoardCollaborateFragment whiteboardFragment;

    private ObjectAnimator hiddeControlAnim;
    private ObjectAnimator showControlAnim;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classroomEngine = ClassroomEngine.getEngine();

        if (getActivity() instanceof Classroom2Activity) {
            whiteboardFragment = ((Classroom2Activity) getActivity()).getCollaBorateFragment();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        slideLayout.setSlideListener(this);
        if (!ClassroomController.getInstance(getActivity()).isPortrait()) {
            onRotateToInitBoard(Configuration.ORIENTATION_LANDSCAPE);
        }

        initControlAnim();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        //
        whiteboardFragment = null;
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CTLConstant.REQUEST_INPUT_MESSAGE:

                    final Talk talkBean = new Talk();
                    talkBean.from = AccountDataManager.getAccountID(getContext());
                    talkBean.body = new Talk.TalkContent();
                    talkBean.body.text = data.getStringExtra(CTLConstant.EXTRA_INPUT_MESSAGE);
                    talkBean.body.contentType = Communications.ContentType.TEXT;
                    talkBean.time = System.currentTimeMillis();
                    talkBean.to = String.valueOf(Communications.TalkType.OPEN);


                    classroomEngine.sendTalk(talkBean, new EventCallback<TalkResponse>() {
                        @Override
                        public void onSuccess(TalkResponse talkResponse) {
                            if (talkResponse != null) {
                                talkBean.time = talkResponse.time;
                            }
                            handleReceivedMsg(talkBean);
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {

                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
        }

    }


    @OnClick({R.id.l_top_back, R.id.l_top_start_or_stop_living,
            R.id.l_bottom_chat, R.id.l_bottom_session, R.id.l_bottom_more,
            R.id.l_right_switchcamera, R.id.l_right_screenshot, R.id.l_right_switch_vb})
    void onLandControlItemClick(View view) {
        switch (view.getId()) {
            case R.id.l_top_back:                                             //返回：横屏
                onTopbackClick(view, true);
                break;
            case R.id.l_top_start_or_stop_living:                             //结束／开始直播
                onStartOrStopLiveClick(view);
                break;
            case R.id.l_right_switchcamera:                                   //切换摄像头
                onSwitchCamera(view);
                break;
            case R.id.l_bottom_more:
                showOrHiddenCenterPanel();
                break;
            case R.id.l_bottom_session:
                onTalkVisibilityClick(view);
                break;
            case R.id.l_bottom_chat:
                onInputMessageClick(view);
                break;

        }
    }


    @OnClick({R.id.center_one2one, R.id.center_board_opera,
            R.id.center_board_mgr, R.id.center_new_board, R.id.center_member,
            R.id.center_database, R.id.center_canlender})
    void onCenterPanelItemClick(View view) {
        switch (view.getId()) {
            case R.id.center_one2one:                                             //一对一音视频
                onOne2OneClick(view);
                break;
            case R.id.center_board_opera:                                         //百般协作
                break;
            case R.id.center_board_mgr:                                           //白板管理
                onBoardMgrClick(view);
                break;
            case R.id.center_new_board:                                           //新增白板
                onNewboardClick(view);
                break;
            case R.id.center_member:                                              //教室成员
                break;
            case R.id.center_database:                                            //资料库
                onMaterialClick(view);
                break;
            case R.id.center_canlender:                                           //课表
                break;
        }

        showOrHiddenCenterPanel();
    }


    @OnClick({R.id.p_top_back, R.id.p_top_more, R.id.p_bottom_orient, R.id.p_top_live})
    void onPortControlItemClick(View view) {
        switch (view.getId()) {
            case R.id.p_top_back:                                             //返回：竖屏
                onTopbackClick(view, false);
                break;
            case R.id.p_top_more:                                             //更多
                showMoreMenu(view);
                break;
            case R.id.p_bottom_orient:                                        //切换为横屏
                changeOrientation();
                break;
            case R.id.p_top_live:                                             //开始直播
                onStartLiveClick(view);
                break;
        }
    }


    @OnClick({R.id.right_slide_layout})
    void onRightSlideLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.right_slide_layout:
                exitSlidePanel();
                break;
        }
    }


    /**
     * 点击了返回
     *
     * @param land 是否横屏的返回
     */
    public abstract void onTopbackClick(View view, boolean land);

    /**
     * 关闭当前的fragment
     */
    public abstract void closeMovie();

    /**
     * 响应屏幕横竖屏方向改变
     */
    public abstract void onRotate(int orientation);


    public void enterIdle() {
        ((Classroom2Activity) getActivity()).enterIdle();
    }

    public void enterPlay() {
        ((Classroom2Activity) getActivity()).enterPlay();
    }

    public void enterLiving() {
        ((Classroom2Activity) getActivity()).enterLiving();
    }

    public void enterPlayback() {
        ((Classroom2Activity) getActivity()).enterPlayback();
    }


    public void showMoreMenu(View targetView) {
        CommonPopupMenu menu = new CommonPopupMenu(getContext());
        String[] items = this.getResources().getStringArray(R.array.classroom2_more_item);
        menu.setBg(R.drawable.popup_menu_bg);
        menu.setWidth(this.getResources().getDimensionPixelSize(R.dimen.px280));
        menu.addTextItems(items);
        menu.addImgItems(new Integer[]{R.drawable.ic_setting,
                R.drawable.ic_class_database_share_1,
                R.drawable.ic_add_class1});
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 2:
                        ClassDetailFragment classDetailFragment = new ClassDetailFragment();
                        classDetailFragment.show(getFragmentManager(), "detail");
                        break;
                    case 1:
                        break;
                    case 0:
                        SettingFragment settingFragment = new SettingFragment();
                        settingFragment.show(getFragmentManager(), "setting");
                        break;
                }

            }
        });
        int offset = getResources().getDimensionPixelSize(R.dimen.px68);
        menu.show(targetView, offset);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // control
    //

    private void initControlAnim() {
        hiddeControlAnim = (ObjectAnimator) AnimatorInflater.loadAnimator(getContext(),
                R.animator.classroom2_control_alpha_hide);
        hiddeControlAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                View view = (View) hiddeControlAnim.getTarget();
                if (view != null) {
                    view.setAlpha(1.0f);
                    view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        showControlAnim = (ObjectAnimator) AnimatorInflater.loadAnimator(getContext(),
                R.animator.classroom2_control_alpha_show);
        showControlAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                View view = (View) showControlAnim.getTarget();
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void showControlAnim(final View target) {
        showControlAnim.setTarget(target);
        showControlAnim.start();
    }

    private void hiddeControlAnim(View target) {
        hiddeControlAnim.setTarget(target);
        hiddeControlAnim.start();
    }

    private void startAnim(final View view, final boolean hidden) {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (hidden) {
                            hiddeControlAnim(view);
                        }else {
                            showControlAnim(view);
                        }

                    }
                });
    }


    private void showLandControl() {
        if (controlLand != null) {
            controlLand.setVisibility(View.VISIBLE);
            startAnim(controlLand, true);
        }
    }

    private void showPortControl() {
        if (controlPort != null) {
            controlPort.setVisibility(View.VISIBLE);
            startAnim(controlPort, true);
        }
    }

    private void hiddeLandControl() {
        if (controlLand != null) {
            controlLand.setVisibility(View.GONE);
            startAnim(controlLand, true);
        }
    }

    private void hiddePortControl() {
        if (controlPort != null) {
            controlPort.setVisibility(View.GONE);
            startAnim(controlPort, true);
        }
    }

    public void hiddeOrshowControl() {

        switch (controlLand.getVisibility()) {
            case View.VISIBLE:
                break;

        }
    }


    /**
     * 切换前后摄像头
     */
    public void onSwitchCamera(View view) {

    }

    /**
     * 竖屏模式下点击了开始直播
     */
    public void onStartLiveClick(View view) {

    }

    /**
     * 横屏模式下点击了开始／结束直播
     */
    public void onStartOrStopLiveClick(View view) {

    }

    /**
     * 点击了新增白板
     */
    public void onNewboardClick(View view) {
        // TODO: 2017/10/18 新增白板
    }

    /**
     * 点击了白板管理
     */
    public void onBoardMgrClick(View view) {

        if (whiteboardFragment != null && !whiteboardFragment.isDetached()) {
            whiteboardFragment.showWhiteboardManager();
        }
    }

    /**
     * 点击了显示或者隐藏聊天列表
     */
    public void onTalkVisibilityClick(View view) {
        int vis = recyclerView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
        recyclerView.setVisibility(vis);
    }


    public void back() {
        getActivity().onBackPressed();
    }

    /**
     * 点击了输入聊天消息
     */
    public void onInputMessageClick(View view) {
        MsgInputFragment inputFragment = new MsgInputFragment();
        Bundle data = new Bundle();
        data.putInt(CTLConstant.EXTRA_INPUT_FROM, 1);
        inputFragment.setArguments(data);
        inputFragment.setTargetFragment(this, CTLConstant.REQUEST_INPUT_MESSAGE);
        inputFragment.show(getFragmentManager(), "input");

    }

    /**
     * 点击了一对一
     */
    public void onOne2OneClick(View view) {

    }

    /**
     * 点击资料库
     */
    public void onMaterialClick(View view) {
        DatabaseFragment databaseFragment = new DatabaseFragment();
        databaseFragment.setTargetFragment(this, CTLConstant.REQUEST_OPEN_MATERIAL);
        showSlidePanel(databaseFragment, "database");
    }


    private void showOrHiddenCenterPanel() {

        int visibility = centerPanelView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;

        centerPanelView.setVisibility(visibility);
        centerOne2oneView.setVisibility(visibility);
        centerBoardOperaView.setVisibility(visibility);
        centerBoardMgrView.setVisibility(visibility);
        centerNewBoardView.setVisibility(visibility);
        centerMedmberView.setVisibility(visibility);
        centerDatabaseView.setVisibility(visibility);
        centerCanlenderView.setVisibility(visibility);


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 右边含有侧滑面板
    //

    public void showSlidePanel(Fragment fragment, String tag) {
        rightSlideLayout.setVisibility(View.VISIBLE);

        slideFragment = fragment;

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.slide_layout, slideFragment)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void exitSlidePanel() {
        rightSlideLayout.setVisibility(View.GONE);

        if (slideFragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(slideFragment)
                    .commitAllowingStateLoss();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 横竖屏切换
    //


    /**
     * 切换横竖屏
     */
    public void changeOrientation() {
        int changeRequest = getActivity().getRequestedOrientation() ==
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ?
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        getActivity().setRequestedOrientation(changeRequest);

    }

    /**
     * 切换横屏
     */
    public void changeOrientationToLand() {

        int changeRequest = getActivity().getRequestedOrientation();

        if (changeRequest != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    protected void controlHandleOnRotate(int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:

                showLandControl();

                if (recyclerView != null) {
                    recyclerView.setVisibility(View.VISIBLE);
                }

                if (controlPort != null) {
                    controlPort.setVisibility(View.GONE);
                }

                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if (controlLand != null) {
                    controlLand.setVisibility(View.GONE);
                }

                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                }

                showPortControl();
                break;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 交流
    //

    public void initTalkData(ChatAdapter.FetchMoreListener listener) {

        messageData = new ArrayList<>();
        messageComparator = new MessageComparator();
        int maxNumOfObjectPerPage = 50;

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatLandAdapter(getContext(), messageData);
        adapter.setAutoFetchMoreSize(8);
        adapter.setPerpageMaxCount(maxNumOfObjectPerPage);
        adapter.setFetchMoreListener(listener);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.px20);
            }
        });


        pagination = new Pagination();
        pagination.setMaxNumOfObjectsPerPage(maxNumOfObjectPerPage);
        pagination.setPage(currentPage);

        liveCriteria = new LiveCriteria();
        liveCriteria.to = String.valueOf(Communications.TalkType.OPEN);

        loadTalk();
    }


    public void loadTalk() {

        if (loading) return;

        loading = true;
        LiveManager.getTalks(getContext(), classroomEngine.getTicket(),
                liveCriteria, pagination, new APIServiceCallback<CollectionPage<TalkItem>>() {
                    @Override
                    public void onSuccess(CollectionPage<TalkItem> object) {
                        if (object != null && object.objectsOfPage != null
                                && object.objectsOfPage.size() > 0) {
                            handleNewData(object);
                        }

                        loading = false;
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                        Toast.makeText(getContext(), "获取消息列表失败", Toast.LENGTH_SHORT).show();

                        loading = false;
                    }
                });
    }

    public void handleNewData(CollectionPage<TalkItem> object) {
        Observable.just(object.objectsOfPage)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doAfterNext(new Consumer<ArrayList<TalkItem>>() {
                    @Override
                    public void accept(ArrayList<TalkItem> talkItems) throws Exception {
                        Collections.sort(talkItems, messageComparator);

                        for (TalkItem item : talkItems) {
                            timeline(item);
                        }
                        messageData.addAll(0, talkItems);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<TalkItem>>() {
                    @Override
                    public void accept(ArrayList<TalkItem> talkItems) throws Exception {


                        if (getActivity() == null)
                            return;

                        if (currentPage == 1) {
                            adapter.notifyDataSetChanged();
                            RecyclerViewScrollHelper rvHelper = new RecyclerViewScrollHelper();
                            rvHelper.smoothMoveToPosition(recyclerView, messageData.size() - 1);
                        } else {
                            adapter.notifyItemRangeInserted(0, talkItems.size());
                            recyclerView.scrollToPosition(talkItems.size());
                        }
                        pagination.setPage(++currentPage);

                        adapter.setFirstLoad(false);

                    }
                });
    }


    public void handleReceivedMsg(Talk talk) {

        TalkItem talkItem = new TalkItem();
        talkItem.time = talk.time;
        talkItem.body = new cn.xiaojs.xma.model.live.TalkItem.TalkContent();
        talkItem.from = new cn.xiaojs.xma.model.live.TalkItem.TalkPerson();
        talkItem.body.text = talk.body.text;
        talkItem.body.contentType = talk.body.contentType;
        talkItem.from.accountId = talk.from;
        //获取名字
        Attendee attendee = classroomEngine.getMember(talk.from);
        talkItem.from.name = attendee == null ? "nil" : attendee.name;

        timeline(talkItem);

        messageData.add(talkItem);
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageData.size() - 1);

    }

    private void timeline(TalkItem item) {
        long time = item.time;
        if (lastTimeline == 0
                || time - lastTimeline >= (long) (5 * 60 * 1000)) {
            item.showTime = true;
            lastTimeline = time;
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 直播
    //

    /**
     * 开始请求直播
     */
    public void requestLive() {

        if (NetworkUtil.isWIFI(getContext())) {
            requestLivePermission();
        } else {
            showNetworkTips();
        }
    }

    private void showNetworkTips() {
        final CommonDialog tipsDialog = new CommonDialog(getContext());
        tipsDialog.setDesc("您正在使用非WI-FI网络，直播将产生流量费用");
        tipsDialog.setLefBtnText(R.string.cancel);
        tipsDialog.setRightBtnText(R.string.mobile_network_allow);
        tipsDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                requestLivePermission();
                tipsDialog.dismiss();

            }
        });
        tipsDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                tipsDialog.dismiss();
            }
        });
        tipsDialog.show();
    }

    private void requestLivePermission() {
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        PermissionGen.needPermission(this, REQUEST_PERMISSION, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Keep
    @PermissionRationale(requestCode = REQUEST_PERMISSION)
    public void requestCameraRationale() {
        PermissionHelper.showRationaleDialog(this,
                getString(R.string.permission_rationale_camera_audio_tip));
    }

    @Keep
    @PermissionSuccess(requestCode = REQUEST_PERMISSION)
    public void toLive() {
        if (classroomEngine.canIndividualByState()) {
            //个人推流
            personPublishStream();
        } else {
            if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING) {
                //开始上课
                requestBeginClass();
            }
        }


    }


    /**
     * 开始上课
     */
    protected void requestBeginClass() {
        showProgress(true);
        classroomEngine.beginClass(classroomEngine.getTicket(), new APIServiceCallback<ClassResponse>() {
            @Override
            public void onSuccess(ClassResponse object) {
                cancelProgress();
                goonLive();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 个人推流
     */
    protected void personPublishStream() {
        showProgress(true);
        classroomEngine.claimStream(Live.StreamMode.AV,
                new EventCallback<ClaimReponse>() {

                    @Override
                    public void onSuccess(ClaimReponse claimReponse) {
                        cancelProgress();
                        goonLive();

                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        cancelProgress();
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void goonLive() {
        changeOrientationToLand();
        enterLiving();
    }


    public void sendStartStreaming() {

        classroomEngine.startStreaming(new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

            }
        });
    }


    public void sendStopStreaming() {

        classroomEngine.stopStreaming(Live.StreamType.INDIVIDUAL,
                classroomEngine.getCsOfCurrent(), new EventCallback<StreamStoppedResponse>() {
                    @Override
                    public void onSuccess(StreamStoppedResponse streamStoppedResponse) {

                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {

                    }
                });
    }

    //====================================全局白板====================================
    protected void addWhiteboardFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.layout_idle_container, whiteboardFragment)
                .commitAllowingStateLoss();
    }

    protected void showWhiteboardFragment() {
        if (whiteboardFragment.isAdded()) {
            getChildFragmentManager().beginTransaction().attach(whiteboardFragment).commitAllowingStateLoss();
        } else {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_idle_container, whiteboardFragment)
                    .commitAllowingStateLoss();
        }
    }

    protected void hideWhiteboardFragment() {
        if (whiteboardFragment.isAdded() && whiteboardFragment.isInLayout()) {
            getChildFragmentManager().beginTransaction().detach(whiteboardFragment).commitAllowingStateLoss();
        }
    }

    protected void removeWhiteboardFragment() {
        getChildFragmentManager().beginTransaction().remove(whiteboardFragment).commitAllowingStateLoss();
    }

    protected void onRotateToInitBoard(int orientation) {
        if (whiteboardFragment == null) return;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (whiteboardFragment.isAdded()) {
                getChildFragmentManager().beginTransaction().attach(whiteboardFragment).commitAllowingStateLoss();
            } else {
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.layout_idle_container, whiteboardFragment)
                        .commitAllowingStateLoss();
            }
        } else {
            if (whiteboardFragment.isAdded() && whiteboardFragment.isInLayout()) {
                getChildFragmentManager().beginTransaction().detach(whiteboardFragment).commitAllowingStateLoss();
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 一对一
    //


    protected void receivedO2o(Attendee attendee) {
        o2oAttendee = attendee;
        ((Classroom2Activity) getActivity()).showO2oPanel(attendee);
    }

    public void argeeO2o() {

        classroomEngine.mediaFeedback(Live.MediaStatus.READY, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {

                changeOrientationToLand();

                handleArgeedO2o(o2oAttendee);

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void refuseO2o() {
        classroomEngine.mediaFeedback(Live.MediaStatus.FAILED_DUE_TO_DENIED,
                new EventCallback<EventResponse>() {
                    @Override
                    public void onSuccess(EventResponse response) {
                        if (XiaojsConfig.DEBUG) {
                            Toast.makeText(getContext(), "你已拒绝", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        if (XiaojsConfig.DEBUG) {
                            Toast.makeText(getContext(), "拒绝失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void sendCloseMedia(String to) {

        classroomEngine.closeMedia(to, new EventCallback<CloseMediaResponse>() {
            @Override
            public void onSuccess(CloseMediaResponse closeMediaResponse) {
                if (XiaojsConfig.DEBUG) {
                    ToastUtil.showToast(getContext(), "close open peer to peer video success");
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                ToastUtil.showToast(getContext(), errorMessage);
            }
        });
    }


    protected void handleArgeedO2o(Attendee attendee) {

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 成员操作
    //

    protected void requestUpdateMemberCount() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(
                    @io.reactivex.annotations.NonNull ObservableEmitter<Integer> e) throws Exception {

                int count = classroomEngine.getOnlineMemberCount();
                e.onNext(count);
                e.onComplete();

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        if (getActivity() == null)
                            return;

                        //排除直播人
                        int realcount = integer.intValue() <= 0 ? 0 : integer.intValue() - 1;

                        onUpdateMembersCount(realcount);

                    }
                });
    }

    public void onUpdateMembersCount(int count) {

    }


}
