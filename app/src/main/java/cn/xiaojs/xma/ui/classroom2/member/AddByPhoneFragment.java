package cn.xiaojs.xma.ui.classroom2.member;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.preference.ClassroomPref;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class AddByPhoneFragment extends BottomSheetFragment {


    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_member_add_by_phone, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tipsContentView.setText("添加成员后，手机号将作为ta登录小教室的唯一账号");

    }

    @OnClick({R.id.back_btn,R.id.ok_btn, R.id.lesson_creation_tips_close})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
            case R.id.ok_btn:
                dismiss();
                break;
            case R.id.lesson_creation_tips_close://关闭提醒
                closeCourCreateTips();
                break;
        }
    }

    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }



}
