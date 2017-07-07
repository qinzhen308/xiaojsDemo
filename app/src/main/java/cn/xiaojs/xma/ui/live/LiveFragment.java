package cn.xiaojs.xma.ui.live;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/10/11
 * Desc:
 *
 * ======================================================================================== */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.LiveClass;
import cn.xiaojs.xma.model.ctl.LiveItem;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.ScanQrcodeActivity;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.EnrollLessonActivity;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.LessonHomeActivity;
import cn.xiaojs.xma.ui.lesson.TeachLessonActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.lesson.xclass.CreateClassActivity;
import cn.xiaojs.xma.ui.search.SearchActivity;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.ui.widget.AdapterListView;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.HorizontalAdaptScrollerView;
import cn.xiaojs.xma.util.TimeUtil;

public class LiveFragment extends BaseFragment {
    @BindView(R.id.swipe_ly)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.home_live_brilliant)
    HorizontalAdaptScrollerView mHorizontalListView;

    @BindView(R.id.home_live_list)
    AdapterListView mTeachingLessonList;
    @BindView(R.id.home_live_list2)
    AdapterListView mEnrollLessonList;

    @BindView(R.id.teacher_wrapper)
    View mTeacherWrapper;
    @BindView(R.id.student_wrapper)
    View mStudentWrapper;

    @BindView(R.id.lesson_all_1)
    View mTeachLessonTitleView;
    @BindView(R.id.lesson_all_2)
    View mEnrollLessonTitleView;
    @BindView(R.id.enroll_lesson_hold_view)
    View mEnrollLessonHoldView;

    @BindView(R.id.teach_lesson_empty)
    LinearLayout mTeachLessonEmpty;
    @BindView(R.id.enroll_lesson_empty)
    TextView mEnrollLessonEmpty;
    @BindView(R.id.teach_divider)
    View teachDivView;
    @BindView(R.id.enroll_divider)
    View enrollDivView;



    @BindView(R.id.teach_lesson_all)
    View mTeachLessonAllView;
    @BindView(R.id.enroll_lesson_all)
    View mEnrollLessonAllView;

    @BindView(R.id.open_lesson)
    ImageView mOpenLesson;

    @BindView(R.id.live_root)
    LinearLayout liveRootView;

//    @BindView(R.id.btn_add)
//    ImageButton addBtnView;

    private int mUserType;
    private Criteria mCriteria;
    protected Pagination mPagination;
    private boolean mDataLoading;

    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_live, null);
        return v;
    }

    @Override
    protected int registerDataChangeListenerType() {
        return SimpleDataChangeListener.LESSON_CREATION_CHANGED | SimpleDataChangeListener.LESSON_ENROLL_CHANGED;
    }

    @Override
    protected void onDataChanged() {
        initData();
    }

    @Override
    protected void init() {

//        mTeachingLessonList.setNeedDivider(true);
//        mTeachingLessonList.setDividerHeight(mContext.getResources().getDimensionPixelSize(R.dimen.px30));
//        mTeachingLessonList.setDividerColor(R.color.main_bg);
//        mEnrollLessonList.setNeedDivider(true);
//        mEnrollLessonList.setDividerHeight(mContext.getResources().getDimensionPixelSize(R.dimen.px30));
//        mEnrollLessonList.setDividerColor(R.color.main_bg);


        Bundle bundle = getArguments();
        if (bundle != null) {
            mUserType = bundle.getInt(LiveConstant.KEY_USER_TYPE, LiveConstant.USER_STUDENT);
        }
        RecyclerView.Adapter adapter = new LiveBrilliantAdapter(mContext);
        mHorizontalListView.setItemVisibleCountType(HorizontalAdaptScrollerView.ItemVisibleTypeCount.TYPE_FREE);
        mHorizontalListView.setItemVisibleCount(1.7f);
        mHorizontalListView.setAdapter(adapter);
        ViewGroup.LayoutParams lp = mHorizontalListView.getLayoutParams();
        lp.height = getResources().getDimensionPixelSize(R.dimen.px370);
        mHorizontalListView.setLayoutParams(lp);

        mCriteria = new Criteria();
        Duration duration = new Duration();
        duration.setStart(TimeUtil.original());
        duration.setEnd(TimeUtil.yearAfter(10));

        mCriteria = new Criteria();
        mCriteria.setSource(Ctl.LessonSource.ALL);
        mCriteria.setDuration(duration);

        mPagination = new Pagination();
        mPagination.setPage(1);
        mPagination.setMaxNumOfObjectsPerPage(3);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });

        initData();
    }

    @OnClick({R.id.btn_add, R.id.lesson_all_1, R.id.lesson_all_2, R.id.s_root, R.id.my_course_search, R.id.open_lesson})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                showMenu(view);
                break;
            case R.id.lesson_all_1:
                Intent intent = new Intent(mContext, TeachLessonActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.lesson_all_2:
                Intent enroll = new Intent(mContext, EnrollLessonActivity.class);
                mContext.startActivity(enroll);
                break;
            case R.id.s_root:
            case R.id.my_course_search:
                Intent search = new Intent(mContext, SearchActivity.class);
                mContext.startActivity(search);
                break;
            case R.id.open_lesson:
                Intent open = new Intent(mContext, LessonCreationActivity.class);
                mContext.startActivity(open);
                break;
        }
    }


    @Override
    protected void reloadOnFailed() {
        initData();
    }

    private void initData() {
        if (mDataLoading) {
            return;
        }

        mDataLoading = true;

        if (!swipeRefreshLayout.isRefreshing()) {
            showProgress(true);
        }

        LessonDataManager.getLiveClasses(mContext, new APIServiceCallback<LiveClass>() {
            @Override
            public void onSuccess(LiveClass object) {
                cancelProgress();
                hideFailView();

                liveRootView.setVisibility(View.VISIBLE);

                fillData(object);
                mDataLoading = false;

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                mDataLoading = false;
                //Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();

                liveRootView.setVisibility(View.GONE);

                showFailView();

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void fillData(final LiveClass liveClasses) {
        if (liveClasses != null) {
            if ((liveClasses.taught != null && !liveClasses.taught.isEmpty())
                    && (liveClasses.enrolled == null || liveClasses.enrolled.isEmpty())) {
                //只有教的课，没有学的课
                mTeachLessonAllView.setVisibility(View.VISIBLE);
                mTeachLessonTitleView.setVisibility(View.VISIBLE);
                LiveTeachingClassAdapter adapter = new LiveTeachingClassAdapter(mContext, liveClasses.taught, false);
                mTeachingLessonList.setAdapter(adapter);
                mEnrollLessonList.setVisibility(View.GONE);
                mTeachingLessonList.setVisibility(View.VISIBLE);

                mTeachingLessonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        enterLessonHome(liveClasses.taught.get(position), true);
                    }
                });

//                mTeachingLessonList.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        enterLessonHome(liveClasses.taught.get(position), true);
//                    }
//                });
                mStudentWrapper.setVisibility(View.GONE);
            } else if ((liveClasses.taught == null || liveClasses.taught.isEmpty())
                    && (liveClasses.enrolled != null && !liveClasses.enrolled.isEmpty())) {
                //只有学的课，没有教的
                mEnrollLessonAllView.setVisibility(View.VISIBLE);
                LiveEnrollLessonAdapter adapter = new LiveEnrollLessonAdapter(mContext, liveClasses.enrolled);
                mEnrollLessonList.setAdapter(adapter);
                mTeachingLessonList.setVisibility(View.GONE);
                mTeachingLessonList.setVisibility(View.VISIBLE);

                mEnrollLessonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        enterLessonHome(liveClasses.enrolled.get(position), false);
                    }
                });
//                mEnrollLessonList.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        enterLessonHome(liveClasses.enrolled.get(position), false);
//                    }
//                });
                mStudentWrapper.setVisibility(View.VISIBLE);
                mEnrollLessonTitleView.setVisibility(View.VISIBLE);
                mTeacherWrapper.setVisibility(View.GONE);
            } else if ((liveClasses.taught != null && !liveClasses.taught.isEmpty())
                    && (liveClasses.enrolled != null && !liveClasses.enrolled.isEmpty())) {
                //有教的课也有学的课
                mEnrollLessonHoldView.setVisibility(View.VISIBLE);
                mStudentWrapper.setVisibility(View.VISIBLE);
                mTeacherWrapper.setVisibility(View.VISIBLE);
                mTeachLessonAllView.setVisibility(View.VISIBLE);
                mEnrollLessonAllView.setVisibility(View.VISIBLE);
                mTeachLessonTitleView.setVisibility(View.VISIBLE);
                mEnrollLessonTitleView.setVisibility(View.VISIBLE);
                mTeachingLessonList.setVisibility(View.VISIBLE);
                mEnrollLessonList.setVisibility(View.VISIBLE);
                LiveTeachingClassAdapter teach = new LiveTeachingClassAdapter(mContext, liveClasses.taught, false);
                mTeachingLessonList.setAdapter(teach);
                LiveEnrollLessonAdapter enroll = new LiveEnrollLessonAdapter(mContext, liveClasses.enrolled);
                mEnrollLessonList.setAdapter(enroll);

                mTeachingLessonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        enterLessonHome(liveClasses.taught.get(position), true);
                    }
                });
                mEnrollLessonList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        enterLessonHome(liveClasses.enrolled.get(position), false);
                    }
                });
//                mTeachingLessonList.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        enterLessonHome(liveClasses.taught.get(position), true);
//                    }
//                });
//                mEnrollLessonList.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        enterLessonHome(liveClasses.enrolled.get(position), false);
//                    }
//                });
            } else if ((liveClasses.taught == null || liveClasses.taught.isEmpty())
                    && (liveClasses.enrolled == null || liveClasses.enrolled.isEmpty())) {
                //没有教的课也没有学的课
                mEnrollLessonTitleView.setVisibility(View.VISIBLE);
                if (AccountDataManager.isTeacher(mContext)) {
                    //当前用户是老师
                    mTeachLessonTitleView.setVisibility(View.VISIBLE);
                    mTeacherWrapper.setVisibility(View.VISIBLE);
                    mStudentWrapper.setVisibility(View.GONE);
                    mTeachLessonEmpty.setVisibility(View.VISIBLE);
                    mEnrollLessonEmpty.setVisibility(View.GONE);
                } else {
                    mTeachLessonEmpty.setVisibility(View.GONE);
                    mEnrollLessonEmpty.setVisibility(View.VISIBLE);
                    mEnrollLessonEmpty.setText(Html.fromHtml(getString(R.string.student_no_lesson_tip)));
                    mTeacherWrapper.setVisibility(View.GONE);
                    mStudentWrapper.setVisibility(View.VISIBLE);
                }
            }
        } else {
            //没有教的课也没有学的课
            mEnrollLessonTitleView.setVisibility(View.VISIBLE);
            if (AccountDataManager.isTeacher(mContext)) {
                //当前用户是老师
                mTeachLessonTitleView.setVisibility(View.VISIBLE);
                mTeacherWrapper.setVisibility(View.VISIBLE);
                mStudentWrapper.setVisibility(View.GONE);
                mTeachLessonEmpty.setVisibility(View.VISIBLE);
                mEnrollLessonEmpty.setVisibility(View.GONE);
            } else {
                mTeachLessonEmpty.setVisibility(View.GONE);
                mEnrollLessonEmpty.setVisibility(View.VISIBLE);
                mEnrollLessonEmpty.setText(Html.fromHtml(getString(R.string.student_no_lesson_tip)));
                mTeacherWrapper.setVisibility(View.GONE);
                mStudentWrapper.setVisibility(View.VISIBLE);
            }
        }
    }

    private void enterLessonHome(LiveItem liveItem, boolean fromTeach) {
        if (TextUtils.isEmpty(liveItem.state)
                || LessonState.ACKNOWLEDGED.equalsIgnoreCase(liveItem.state)
                || LessonState.PENDING_FOR_ACK.equalsIgnoreCase(liveItem.state)
                || LessonState.DRAFT.equalsIgnoreCase(liveItem.state)
                || LessonState.PENDING_FOR_APPROVAL.equalsIgnoreCase(liveItem.state)
                || LessonState.CANCELLED.equals(liveItem.state)
                || LessonState.REJECTED.equalsIgnoreCase(liveItem.state)) {
            return;
        }

        Intent i = new Intent(mContext, LessonHomeActivity.class);
        if (fromTeach) {
            i.putExtra(CourseConstant.KEY_ENTRANCE_TYPE, LessonHomeActivity.ENTRANCE_FROM_TEACH_LESSON);
        }
        i.putExtra(CourseConstant.KEY_LESSON_ID, liveItem.id);
        mContext.startActivity(i);
    }


    private void showMenu(View targetView) {
        CommonPopupMenu menu = new CommonPopupMenu(mContext);
        String[] items = getResources().getStringArray(R.array.add_menu);
        menu.addTextItems(items);
        menu.addImgItems(new Integer[]{R.drawable.ic_scan, R.drawable.ic_menu_create_lesson, R.drawable.ic_menu_create_lesson});
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        if (PermissionUtil.isOverMarshmallow()
                                && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                            requestPermissions(new String[]{Manifest.permission.CAMERA}, MainActivity.PERMISSION_CODE);
                            PermissionGen.needPermission(LiveFragment.this,MainActivity.PERMISSION_CODE,Manifest.permission.CAMERA);

                        } else {
                            startActivity(new Intent(mContext, ScanQrcodeActivity.class));
                        }
                        break;
                    case 1:
                        if (AccountDataManager.isTeacher(mContext)) {
                            //老师可以开课
                            Intent intent = new Intent(mContext, LessonCreationActivity.class);
                            startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
                        } else {
                            //提示申明教学能力
                            final CommonDialog dialog = new CommonDialog(mContext);
                            dialog.setTitle(R.string.declare_teaching_ability);
                            dialog.setDesc(R.string.declare_teaching_ability_tip);
                            dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mContext, TeachingSubjectActivity.class);
                                    startActivity(intent);
                                }
                            });
                            dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                        break;
                    case 2:
                        startActivity(new Intent(mContext, CreateClassActivity.class));
                        break;
                }

            }
        });
        int offset = getResources().getDimensionPixelSize(R.dimen.px68);
        menu.show(targetView, offset);
    }

    @Keep
    @PermissionSuccess(requestCode = MainActivity.PERMISSION_CODE)
    public void accessCameraSuccess() {
        startActivity(new Intent(mContext, ScanQrcodeActivity.class));
    }

    @Keep
    @PermissionRationale(requestCode = MainActivity.PERMISSION_CODE)
    public void accessCameraRationale() {
        PermissionHelper.showRationaleDialog(this);
    }
}
