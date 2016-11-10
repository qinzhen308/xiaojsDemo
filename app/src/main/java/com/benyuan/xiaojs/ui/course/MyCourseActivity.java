package com.benyuan.xiaojs.ui.course;
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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.ui.base.BaseTopTabActivity;
import com.benyuan.xiaojs.ui.view.CommonPopupMenu;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

public class MyCourseActivity extends BaseTopTabActivity {

    View mHover;
    TextView mFilter;
    TextView mInput;

    private CourseFilterDialog mDialog;
    private int mTimePosition;
    private int mStatePosition;

    @Override
    protected void initView() {
        setMiddleTitle(R.string.my_course);
        setRightImage(R.drawable.add_selector);
        setLeftImage(R.drawable.back_arrow);

        List<Fragment> fs = new ArrayList<>();
        Bundle b1 = new Bundle();
        b1.putBoolean("key",false);
        Bundle b2 = new Bundle();
        b2.putBoolean("key",true);
        Fragment f1 = new MyCourseFragment();
        f1.setArguments(b1);
        fs.add(f1);
        Fragment f2 = new MyCourseFragment();
        f2.setArguments(b2);
        fs.add(f2);
        List<String> ss = new ArrayList<>();
        ss.add("我学的课");
        ss.add("我授的课");
        mHover = getLayoutInflater().inflate(R.layout.layout_my_course_search,null);
        mFilter = (TextView) mHover.findViewById(R.id.course_filter);
        mInput = (TextView) mHover.findViewById(R.id.my_course_search);
        mHover.setVisibility(View.GONE);
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter();
            }
        });
        mInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyCourseActivity.this,MyCourseSearchActivity.class);
                startActivity(intent);
            }
        });
        addHover(ss,mHover,fs);
    }

    @OnClick({R.id.left_image,R.id.right_image})
    public void onLocalClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image://右上角menu
                CommonPopupMenu menu = new CommonPopupMenu(this);
                String[] items = getResources().getStringArray(R.array.my_course_list);
                menu.addTextItems(items);
                menu.addImgItems(new Integer[]{R.drawable.open_course_selector,R.drawable.add_private_course_selector});
                menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        handleRightClick(i);
                    }
                });
                menu.showAsDropDown(mRightText);
                break;
            default:
                break;
        }
    }

    private void filter(){
        if (mDialog == null){
            mDialog = new CourseFilterDialog(this);
            mDialog.setTimeSelection(mTimePosition);
            mDialog.setStateSelection(mStatePosition);
            mDialog.showAsDropDown(mHover);
            mDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mDialog = null;
                }
            });
            mDialog.setOnOkListener(new CourseFilterDialog.OnOkListener() {
                @Override
                public void onOk(int timePosition, int statePosition) {
                    mTimePosition = timePosition;
                    mStatePosition = statePosition;
                    Criteria criteria  = MyCourseBusiness.getFilter(timePosition,statePosition);
                    Fragment f = ((FragmentPagerAdapter)mViewPager.getAdapter()).getItem(getPosition());
                    ((MyCourseFragment)f).request(criteria);
                }
            });
        }else {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void handleRightClick(int position){
        switch (position){
            case 0://我要开课
                Intent intent = new Intent(this,CourseCreationActivity.class);
                startActivity(intent);
                break;
            case 1://加入私密课
                break;
            default:
                break;
        }
    }

    public void hideTop(){
        mHover.setVisibility(View.GONE);
    }

    public void showTop(){
        mHover.setVisibility(View.VISIBLE);
    }
}
