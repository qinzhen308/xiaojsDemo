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
 * Author:Administrator
 * Date:2016/11/2
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.mine.SettingsActivity;
import com.benyuan.xiaojs.ui.widget.EditTextDel;
import com.benyuan.xiaojs.util.DataPicker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class LiveCourseCreationFragment extends BaseFragment {
    private final int ENROLL_MAX_NUM = 50;
    private final int REQUEST_CODE_OPTIONAL_INFO = 1;

    @BindView(R.id.live_course_name)
    EditTextDel mLiveCourseNameEdt;
    @BindView(R.id.enroll_people_limit)
    EditTextDel mEnrollLimitEdt;
    @BindView(R.id.teach_form)
    TextView mTeachFormTv;
    @BindView(R.id.enroll_way_switcher)
    ToggleButton mEnrollWaySwitcher;
    @BindView(R.id.enroll_way)
    View mEnrollWayLayout;
    @BindView(R.id.charge_way_switcher)
    ToggleButton mChargeWaySwitcher;
    @BindView(R.id.charge_way)
    View mChargeWayLayout;
    @BindView(R.id.by_live_duration)
    EditTextDel mByLiveDurationEdt;
    @BindView(R.id.by_live_total_price)
    EditTextDel mByLiveTotalPriceEdt;
    @BindView(R.id.course_duration)
    EditTextDel mCourseDurationEdt;
    @BindView(R.id.on_shelves)
    ToggleButton mOnShelves;

    private boolean mEnrollWayOpen = false;


    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_live_course_creation, null);
    }

    @Override
    protected void init() {
        mEnrollWaySwitcher.setChecked(false);
        mEnrollWayLayout.setVisibility(View.GONE);

        mChargeWaySwitcher.setSelected(false);
        mChargeWayLayout.setVisibility(View.GONE);
    }

    @OnClick({R.id.subject_category, R.id.teach_form, R.id.enroll_way, R.id.online_enroll,
            R.id.offline_enroll, R.id.enroll_way_switcher, R.id.charge_way_switcher,
            R.id.by_live_total_price_title, R.id.by_live_duration_title, R.id.course_start_time,
            R.id.optional_info, R.id.on_shelves})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subject_category:
                break;
            case R.id.teach_form:
                selectTeachForm();
                break;
            case R.id.enroll_way_switcher:
                openOrCloseEnrollWay(v);
                break;
            case R.id.online_enroll:
                v.setSelected(!v.isSelected() ? true : false);
                break;
            case R.id.offline_enroll:
                v.setSelected(!v.isSelected() ? true : false);
                break;
            case R.id.charge_way_switcher:
                openOrCloseChargeWay(v);
                break;
            case R.id.by_live_total_price_title:
                v.setSelected(!v.isSelected() ? true : false);
                mByLiveDurationEdt.setEnabled(v.isSelected());
                break;
            case R.id.by_live_duration_title:
                v.setSelected(!v.isSelected() ? true : false);
                mByLiveTotalPriceEdt.setEnabled(v.isSelected());
                break;
            case R.id.course_start_time:
                selectCourseStartTime();
                break;
            case R.id.optional_info:
                startActivityForResult(new Intent(mContext, CourseCreationOptionalInfoActivity.class),
                        REQUEST_CODE_OPTIONAL_INFO);
                break;
            case R.id.on_shelves:
                break;
            case R.id.sub_btn:
                createLiveCourse();
                break;
            default:
                break;
        }
    }

    private void selectTeachForm() {
        List<String> sexList = new ArrayList<String>();
        sexList.add(getString(R.string.teach_form_open));
        sexList.add(getString(R.string.teach_form_one2one));
        sexList.add(getString(R.string.teach_form_more2one));
        DataPicker.pickData(mContext, sexList, new DataPicker.OnDataPickListener() {
            @Override
            public void onDataPicked(Object data) {
                if (data instanceof String) {
                    mTeachFormTv.setText((String) data);
                }
            }
        });
    }

    private void openOrCloseEnrollWay(View view) {
        if (view instanceof ToggleButton) {
            boolean isChecked = ((ToggleButton) view).isChecked();
            mEnrollWayLayout.setVisibility(!isChecked ? View.GONE : View.VISIBLE);
        }
    }

    private void openOrCloseChargeWay(View view) {
        if (view instanceof ToggleButton) {
            boolean isChecked = ((ToggleButton) view).isChecked();
            mChargeWayLayout.setVisibility(!isChecked ? View.GONE : View.VISIBLE);
        }
    }

    private void selectCourseStartTime() {
        List<String> sexList = new ArrayList<String>();
        sexList.add(getString(R.string.teach_form_open));
        sexList.add(getString(R.string.teach_form_one2one));
        sexList.add(getString(R.string.teach_form_more2one));
        DataPicker.pickData(mContext, sexList, new DataPicker.OnDataPickListener() {
            @Override
            public void onDataPicked(Object data) {
                if (data instanceof String) {
                    mTeachFormTv.setText((String) data);
                }
            }
        });
    }

    private boolean checkSubmitInfo() {
        String limitPeople = mEnrollLimitEdt.getText().toString();
        try {
            int limit = Integer.parseInt(limitPeople);
            if (limit > ENROLL_MAX_NUM) {
                Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (Exception e) {
                return false;
        }

        return true;
    }

    private void createLiveCourse() {
        if (!checkSubmitInfo()) {
            return;
        }

        //submit info
    }

}
