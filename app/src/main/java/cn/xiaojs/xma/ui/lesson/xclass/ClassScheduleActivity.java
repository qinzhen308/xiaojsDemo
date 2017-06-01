package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by Administrator on 2017/5/23.
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
    public static final String EXTRA_TITLE="extra_id";

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.schedule);
        setRightImage(R.drawable.selector_mode_tab);
        calenerFragment=new ClassSheduleCalenerFragment();
        tabFragment=new ClassSheduleTabModeFragment();
        mFm=getSupportFragmentManager();
        mFm.beginTransaction().replace(R.id.base_content,calenerFragment).commitAllowingStateLoss();
        findViewById(R.id.right_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMode();
            }
        });
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


    public static void invoke(Context context,String id,String title){
        Intent intent=new Intent(context,ClassScheduleActivity.class);
        intent.putExtra(EXTRA_ID,id);
        intent.putExtra(EXTRA_TITLE,title);
        context.startActivity(intent);
    }

}
