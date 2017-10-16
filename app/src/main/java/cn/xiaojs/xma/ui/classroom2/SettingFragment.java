package cn.xiaojs.xma.ui.classroom2;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.RadioGroup;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.preference.ClassroomPref;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class SettingFragment extends BottomSheetFragment {


    @BindView(R.id.check_4g)
    CheckedTextView checked4GView;
    @BindView(R.id.level_group)
    RadioGroup levelGroupView;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_setting, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        checked4GView.setChecked(ClassroomPref.allowLive4G(getContext()));
        levelGroupView.check(getCheckId(ClassroomPref.getLivingLevel(getContext())));

        levelGroupView.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                switch (checkedId){
                    case R.id.level_smooth:
                        ClassroomPref.setLivingLevel(getContext(), 0);
                        break;
                    case R.id.level_sd:
                        ClassroomPref.setLivingLevel(getContext(), 1);
                        break;
                    case R.id.level_hd:
                        ClassroomPref.setLivingLevel(getContext(), 2);
                        break;

                }


            }
        });

    }

    @OnClick({R.id.check_4g})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_4g:
                boolean allow = ClassroomPref.allowLive4G(getContext());
                checked4GView.setChecked(!allow);
                ClassroomPref.setAllowLive4g(getContext(), !allow);
                break;
        }
    }

    private int getCheckId(int index) {
        switch (index){
            case 0:
                return R.id.level_smooth;
            case 1:
                return R.id.level_sd;
            case 2:
                return R.id.level_hd;
        }

        return R.id.level_smooth;
    }


}
