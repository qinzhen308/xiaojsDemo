package cn.xiaojs.xma.ui.classroom2.schedule;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.page.ClassroomScheduleFragment;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.material.AddNewFragment;
import cn.xiaojs.xma.ui.classroom2.material.DatabaseListFragment;
import cn.xiaojs.xma.ui.classroom2.material.DownloadListFragment;
import cn.xiaojs.xma.util.APPUtils;

import static cn.xiaojs.xma.ui.classroom.main.Constants.KEY_CLASS_ID;

/**
 * Created by Paul Z on 2017/10/25.
 */

public class ScheduleFragment extends BottomSheetFragment
        implements DialogInterface.OnKeyListener{

    @BindView(R.id.cl_root)
    ConstraintLayout rootLay;

    ClassroomScheduleFragment fragment;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_schedule, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getDialog()==null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rootLay.getLayoutParams();
            params.setMargins(0,0,0,0);
            rootLay.setBackgroundColor(getResources().getColor(R.color.white));
        }else {
            getDialog().setOnKeyListener(this);
        }

        fragment=new ClassroomScheduleFragment();
        Bundle data=new Bundle();
        data.putString(KEY_CLASS_ID,getArguments().getString(KEY_CLASS_ID));
        fragment.setArguments(data);
        getChildFragmentManager().beginTransaction().add(R.id.layout_container,fragment).commitAllowingStateLoss();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {

            switch (requestCode) {
                case CTLConstant.REQUEST_MATERIAL_ADD_NEW:
                    break;
            }
        }
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }


    public static ScheduleFragment createInstance(String classId){
        ScheduleFragment fragment=new ScheduleFragment();
        Bundle data=new Bundle();
        if(!TextUtils.isEmpty(classId)){
            data.putString(KEY_CLASS_ID,classId);
        }
        fragment.setArguments(data);
        return fragment;
    }
}
