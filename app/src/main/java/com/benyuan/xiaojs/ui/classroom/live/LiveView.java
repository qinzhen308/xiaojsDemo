package com.benyuan.xiaojs.ui.classroom.live;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/11/29
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.live.core.Config;
import com.benyuan.xiaojs.ui.classroom.live.view.LiveRecordView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveView extends FrameLayout{

    @BindView(R.id.live_record)
    LiveRecordView mRecorder;

    public LiveView(Context context) {
        super(context);
        init();
    }

    public LiveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public LiveView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_clazz_room_live,this,true);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void create(){
        mRecorder.setPath(Config.pathPush);
    }

    public void resume(){
        mRecorder.resume();
    }

    public void pause(){
        mRecorder.pause();
    }

    public void stop(){

    }

    public void destroy(){
        mRecorder.destroy();
    }
}
