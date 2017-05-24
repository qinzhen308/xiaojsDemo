package cn.xiaojs.xma.ui.lesson.xclass;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override
    protected View getContentView() {
        View v=View.inflate(getActivity(), R.layout.fragment_class_schedule_tab_mode, null);
        recyclerview=(RecyclerView)v.findViewById(R.id.recyclerview);
        tabBar=(RadioGroup) v.findViewById(R.id.tab_bar);
        return v;
    }

    @Override
    protected void init() {
        mAdapter=new HomeClassAdapter();
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
    }


    private void request(){

    }



}
