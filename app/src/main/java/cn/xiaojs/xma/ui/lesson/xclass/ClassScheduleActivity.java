package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by Paul Z on 2017/5/23.
 *
 * 班级课表
 * 两种查看模式分别用fragment管理
 */

public class ClassScheduleActivity extends BaseActivity{
    FragmentManager mFm;
    FragmentTransaction mFt;

    ClassSheduleCalenerFragment calenerFragment;
    ClassSheduleTabModeFragment tabFragment;

    boolean isTabMode=false;

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
        String title=getIntent().getStringExtra(EXTRA_TITLE);
        if(TextUtils.isEmpty(title)){
            setMiddleTitle(R.string.schedule);
        }else {
            if(title.length()>8){
                title=title.substring(0,8)+"...";
            }
            setMiddleTitle(title);

        }
        //因为title_layout是公用的，按钮顺序无法改变，所以只能根据定义按钮的不同意义，来实现对应功能
        if(isTeaching){
            setRightImage(R.drawable.add_selector);
            setRightImage2(R.drawable.selector_mode_tab);
        }else {
            setRightImage(R.drawable.selector_mode_tab);
        }

        ButterKnife.bind(this);
        calenerFragment=new ClassSheduleCalenerFragment();
        tabFragment=new ClassSheduleTabModeFragment();
        mFm=getSupportFragmentManager();
        mFm.beginTransaction().replace(R.id.base_content,calenerFragment).commitAllowingStateLoss();

    }

    @OnClick({R.id.right_image,R.id.right_image2,R.id.left_view})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.right_image:
                if(isTeaching){
                    addSchedule();
                }else {
                    checkMode();
                }
                break;
            case R.id.right_image2:
                //学生身份就没有这个按钮
                checkMode();
                break;
            case R.id.left_view:
                finish();
                break;
        }
    }

    private void addSchedule(){
        Intent intent=new Intent(ClassScheduleActivity.this,CreateTimetableActivity.class);
        intent.putExtra(ClassInfoActivity.EXTRA_CLASSID,getIntent().getStringExtra(EXTRA_ID));
        if(!isTabMode){
            intent.putExtra(CreateTimetableActivity.EXTRA_TARGET_DATE,calenerFragment.getSelectedDate());
        }
        startActivityForResult(intent,REQUEST_CODE_ADD);
    }

    private void checkMode(){
        handler.sendEmptyMessageDelayed(1,PROTECT_TIME);
        if(isProtected)return;
        isTabMode=!isTabMode;
        if(isTabMode){
            setRightImage(R.drawable.selector_mode_calendar);
            mFm.beginTransaction().replace(R.id.base_content,tabFragment).commitAllowingStateLoss();
        }else {
            setRightImage(R.drawable.selector_mode_tab);
            mFm.beginTransaction().replace(R.id.base_content,calenerFragment).commitAllowingStateLoss();
        }
    }


    private final static int PROTECT_TIME=500;
    private boolean isProtected=false;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //保护结束
            isProtected=false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==REQUEST_CODE_ADD){
                ClassLesson cl=(ClassLesson) data.getSerializableExtra(CreateTimetableActivity.EXTRA_CLASS_LESSON);
                updateFragment(cl);
            }
        }
    }

    private void updateFragment(ClassLesson obj){
        tabFragment.refresh();
        calenerFragment.refresh(obj);
    }

    public static void invoke(Context context, String id, String title){
        Intent intent=new Intent(context,ClassScheduleActivity.class);
        intent.putExtra(EXTRA_ID,id);
        intent.putExtra(EXTRA_TITLE,title);
        context.startActivity(intent);
    }

    public static void invoke(Context context, String id, String title,boolean teaching){
        Intent intent=new Intent(context,ClassScheduleActivity.class);
        intent.putExtra(EXTRA_ID,id);
        intent.putExtra(EXTRA_TITLE,title);
        intent.putExtra(EXTRA_TEACHING,teaching);
        context.startActivity(intent);
    }

}
