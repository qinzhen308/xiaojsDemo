package cn.xiaojs.xma.ui.lesson;
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
 * Date:2017/1/15
 * Desc:我授的课
 *
 * ======================================================================================== */

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;

public class TeachLessonActivity extends BaseActivity {

    @BindView(R.id.teach_lesson_list)
    PullToRefreshSwipeListView mList;

    @BindView(R.id.my_course_search)
    TextView mSearch;

    @BindView(R.id.filter_line)
    View mFilterLine;
    @BindView(R.id.course_filter)
    TextView mFilter;

    protected int timePosition;
    protected int statePosition;
    protected int sourcePosition;

    private TeachLessonAdapter mAdapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_teach_lesson);
        setRightImage(R.drawable.add_selector);
        needHeaderDivider(false);
        setMiddleTitle(R.string.course_of_teach);
        mAdapter = new TeachLessonAdapter(this, mList);
        mAdapter.setDesc(getString(R.string.teach_data_empty));
        mAdapter.setIcon(R.drawable.ic_teach_empty);
        mAdapter.setButtonDesc(getString(R.string.lesson_creation));
        mList.setAdapter(mAdapter);
        mSearch.setHint("课程名称");
    }

    @OnClick({R.id.left_image, R.id.course_filter,R.id.right_image, R.id.my_course_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.course_filter:
                //TODO FILTER
                filter();
                break;
            case R.id.right_image:
                handleRightClick(0);
                //CommonPopupMenu menu = new CommonPopupMenu(this);
                //String[] items = getResources().getStringArray(R.array.my_course_list);
                //menu.addTextItems(items);
                //menu.addImgItems(new Integer[]{R.drawable.open_course_selector});
                //menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //    @Override
                //    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //        handleRightClick(i);
                //    }
                //});
                //int offset = getResources().getDimensionPixelSize(R.dimen.px68);
                //menu.show(mBaseHeader,offset);
                break;
            case R.id.my_course_search:
                Intent intent = new Intent(TeachLessonActivity.this, LessonSearchActivity.class);
                intent.putExtra(CourseConstant.KEY_IS_TEACHER, true);
                startActivity(intent);
                break;
        }
    }

    private void handleRightClick(int position){
        switch (position){
            case 0://我要开课

                if (AccountDataManager.isTeacher(this)){
                    //老师可以开课
                    Intent intent = new Intent(this,LessonCreationActivity.class);
                    startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
                }else {
                    //提示申明教学能力
                    final CommonDialog dialog = new CommonDialog(this);
                    dialog.setTitle(R.string.declare_teaching_ability);
                    dialog.setDesc(R.string.declare_teaching_ability_tip);
                    dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            Intent intent = new Intent(TeachLessonActivity.this, TeachingSubjectActivity.class);
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
            case 1://加入私密课
                break;
            default:
                break;
        }
    }

    private void filter() {
        CourseFilterDialog dialog = new CourseFilterDialog(this, false);
        dialog.setTimeSelection(timePosition);
        dialog.setStateSelection(statePosition);
        dialog.showAsDropDown(mFilterLine);
        mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_up, 0);
        dialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_down, 0);
            }
        });
        dialog.setOnOkListener(new CourseFilterDialog.OnOkListener() {
            @Override
            public void onOk(int timePosition, int statePosition, int sourcePosition) {
                TeachLessonActivity.this.timePosition = timePosition;
                TeachLessonActivity.this.statePosition = statePosition;
                TeachLessonActivity.this.sourcePosition = sourcePosition;
                Criteria criteria = LessonBusiness.getFilter(timePosition, statePosition, sourcePosition, false);
                if (mAdapter != null) {
                    mAdapter.request(criteria);
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //do something
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case CourseConstant.CODE_CREATE_LESSON:
                if (resultCode == Activity.RESULT_OK){
                    if (mAdapter != null){
                        mAdapter.refresh();
                    }
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
