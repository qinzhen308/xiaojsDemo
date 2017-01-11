package cn.xiaojs.xma.ui.base;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/12/18
 * Desc:
 *
 * ======================================================================================== */

import android.view.View;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.CheckSoftInputLayout;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseCheckSoftInputActivity extends BaseActivity{

    CheckSoftInputLayout mLayout;
    private Unbinder mBinder;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_check_soft_input);
        mLayout = (CheckSoftInputLayout) findViewById(R.id.check_soft_input);
        mLayout.setOnResizeListener(new CheckSoftInputLayout.OnResizeListener() {
            @Override
            public void onResize(int w, int h, int oldw, int oldh) {
                if (oldh == 0){
                    return;
                }
                if (w != oldw){
                    return;
                }
                if (h < oldh){
                    //输入法弹出
                    onImShow(oldh - h);
                }else if (h > oldh){
                    //输入法关闭
                    onImHide(h - oldh);
                }
            }
        });
        load();
    }

    protected abstract void onImShow(int imHeight);

    protected abstract void onImHide(int imHeight);
    /**
     * 调用loadView加载布局
     */
    protected abstract void load();

    protected View loadView(int layoutId){
        if (layoutId > 0){
            mLayout.removeAllViews();
            View view = getLayoutInflater().inflate(layoutId, mLayout);
            mBinder = ButterKnife.bind(this);
            return view;
        }else {
            try {
                throw new Exception("layoutId error!");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected boolean delayBindView() {
        return true;
    }

    @Override
    protected void onDestroy() {
        if (mBinder != null){
            mBinder.unbind();
        }
        super.onDestroy();
    }
}
