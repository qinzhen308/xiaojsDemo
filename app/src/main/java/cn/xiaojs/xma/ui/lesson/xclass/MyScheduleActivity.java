package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;

/**
 * Created by Paul Z on 2017/5/23.
 *
 * 我的课表（原首页）
 */

public class MyScheduleActivity extends BaseActivity implements IUpdateMethod {
    FragmentManager mFm;
    FragmentTransaction mFt;

    MyScheduleFragment mFragment;


    public static final String EXTRA_ID="extra_id";
    public static final String EXTRA_TITLE="extra_title";
    public static final String EXTRA_TEACHING="extra_teaching";
    public static final int REQUEST_CODE_ADD=23;
    public boolean isTeaching=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isTeaching=getIntent().getBooleanExtra(EXTRA_TEACHING,false);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void addViewContent() {
        ButterKnife.bind(this);
        setMiddleTitle(ScheduleUtil.getDateYM_Ch(new Date()));
        mFragment=new MyScheduleFragment();
        mFragment.setOnMonthChangeListener(new MyScheduleBuz.OnMonthChangeListener() {
            @Override
            public void onMonthChange(String Date) {
                setMiddleTitle(Date);
            }
        });
        mFm=getSupportFragmentManager();
        mFm.beginTransaction().replace(R.id.base_content,mFragment).commitAllowingStateLoss();

    }

    @OnClick({R.id.left_view})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.left_view:
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode) {
                case REQUEST_CODE_ADD:
                    ClassLesson cl=(ClassLesson) data.getSerializableExtra(CreateTimetableActivity.EXTRA_CLASS_LESSON);
                    updateFragment(cl);
                    break;
                case CourseConstant.CODE_CANCEL_LESSON:
                case CourseConstant.CODE_EDIT_LESSON:
                case CourseConstant.CODE_LESSON_AGAIN:
                case CourseConstant.CODE_CREATE_LESSON:
                    updateData(false);
                    break;
            }
        }
    }

    private void updateFragment(ClassLesson obj){

    }

    public static void invoke(Context context){
        Intent intent=new Intent(context,MyScheduleActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void updateData(boolean justNative,Object... others) {
        mFragment.updateData(justNative);
    }

    @Override
    public void updateItem(int position, Object obj, Object... others) {
        //因为两种模式的数据集在代码层不同的，但是在概念上是相同的，所以没法做到精准同步。
        //所以对于当前模式下的操作，可以精准刷新，但是另一个模式只能采取从接口重新拉取数据
        mFragment.updateItem(position,obj,others);
    }


}
