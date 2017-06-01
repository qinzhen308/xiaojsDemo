package cn.xiaojs.xma.ui.lesson.xclass;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ClassSheduleTabModeFragment extends AbsClassScheduleFragment {

    RecyclerView recyclerview;
    HomeClassAdapter mAdapter;
    RadioGroup tabBar;
    RadioButton tab1;
    RadioButton tab2;
    RadioButton tab3;
    RadioButton tab4;
    String classId="";

    @Override
    protected View getContentView() {
        View v=View.inflate(getActivity(), R.layout.fragment_class_schedule_tab_mode, null);
        recyclerview=(RecyclerView)v.findViewById(R.id.recyclerview);
        tabBar=(RadioGroup) v.findViewById(R.id.tab_bar);
        tab1=(RadioButton) v.findViewById(R.id.tab1);
        tab2=(RadioButton) v.findViewById(R.id.tab2);
        tab3=(RadioButton) v.findViewById(R.id.tab3);
        tab4=(RadioButton) v.findViewById(R.id.tab4);
        return v;
    }

    @Override
    protected void init() {
        classId=getActivity().getIntent().getStringExtra(ClassScheduleActivity.EXTRA_ID);
        mAdapter=new HomeClassAdapter(recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        recyclerview.setAdapter(mAdapter);
        tabBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.tab1:
                        break;
                    case R.id.tab2:
                        break;
                    case R.id.tab3:
                        break;
                    case R.id.tab4:
                        break;
                }
            }
        });

        getCountPerTab();
    }


    private void request(){

    }


    private void getCountPerTab(){
        setTabCount(3333,102,10,1024);
    }

    private void setTabCount(int count1,int count2,int count3,int count4){
        SpannableString ss=new SpannableString("全部 "+formatCount(count1));
        ss.setSpan(new RelativeSizeSpan(0.6f),3,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab1.setText(ss);
        ss=new SpannableString("待上课 "+formatCount(count2));
        ss.setSpan(new RelativeSizeSpan(0.6f),4,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab2.setText(ss);
        ss=new SpannableString("上课中 "+formatCount(count3));
        ss.setSpan(new RelativeSizeSpan(0.6f),4,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab3.setText(ss);
        ss=new SpannableString("已完课 "+formatCount(count4));
        ss.setSpan(new RelativeSizeSpan(0.6f),4,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab4.setText(ss);

    }

    private String formatCount(int count){
        String str="";
        if(count<1000){
            str=""+count;
        }else if(count<10000){
            str=(count/1000)+"k+";
        }else {
            str="上万";
        }
        return str;
    }



}
