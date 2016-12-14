package com.benyuan.xiaojs.ui.base;
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
 * Date:2016/10/11
 * Desc:
 *
 * ======================================================================================== */

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.progress.ProgressHUD;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseActivity extends FragmentActivity {

    protected View mHeader;
    protected FrameLayout mContent;
    private TextView mLeftText;
    protected TextView mRightText;
    private TextView mMiddleText;
    private ImageView mLeftImage;
    private ImageView mRightImage;
    private ImageView mRightImage2;
    private View mHeaderDivider;
    private View mFailedView;
    private Button mReload;

    private Unbinder mBinder;
    private ProgressHUD progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        mHeader = findViewById(R.id.header);
        mContent = (FrameLayout) findViewById(R.id.base_content);
        mLeftText = (TextView) findViewById(R.id.left_view);
        mRightText = (TextView) findViewById(R.id.right_view);
        mMiddleText = (TextView) findViewById(R.id.middle_view);
        mLeftImage = (ImageView) findViewById(R.id.left_image);
        mRightImage = (ImageView) findViewById(R.id.right_image);
        mRightImage2 = (ImageView) findViewById(R.id.right_image2);
        mHeaderDivider = findViewById(R.id.base_header_divider);
        mFailedView = findViewById(R.id.base_failed);
        mReload = (Button) findViewById(R.id.base_failed_click);
        addViewContent();
    }

    protected void needHeader(boolean need){
        if (!need){
            mHeader.setVisibility(View.GONE);
            mHeaderDivider.setVisibility(View.GONE);
        }else {
            mHeader.setVisibility(View.VISIBLE);
            mHeaderDivider.setVisibility(View.VISIBLE);
        }
    }

    protected void needHeaderDivider(boolean need){
        if (need){
            mHeaderDivider.setVisibility(View.VISIBLE);
        }else {
            mHeaderDivider.setVisibility(View.GONE);
        }
    }

    protected abstract void addViewContent();

    protected View addView(int layoutId){
        if (layoutId > 0){
            mContent.removeAllViews();
            View view = getLayoutInflater().inflate(layoutId, mContent);
            if (!delayBindView()){
                mBinder = ButterKnife.bind(this);
            }
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

    protected View addView(View view){
        if (view != null){
            mContent.removeAllViews();
            mContent.addView(view);
            if (!delayBindView()){
                mBinder = ButterKnife.bind(this);
            }
            return view;
        }else {
            try {
                throw new Exception("view is null!");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    protected final void setLeftText(int resId){
        if (resId > 0 && !TextUtils.isEmpty(getResources().getText(resId))){
            mLeftText.setVisibility(View.VISIBLE);
            mLeftText.setText(resId);
        }else {
            mLeftText.setVisibility(View.GONE);
        }
    }

    protected final void setMiddleTitle(int resId){
        if (resId > 0 && !TextUtils.isEmpty(getResources().getText(resId))){
            mMiddleText.setVisibility(View.VISIBLE);
            mMiddleText.setText(resId);
        }else {
            mMiddleText.setVisibility(View.GONE);
        }
    }

    protected final void setMiddleTitle(String title){
        if (!TextUtils.isEmpty(title)){
            mMiddleText.setVisibility(View.VISIBLE);
            mMiddleText.setText(title);
        }else {
            mMiddleText.setVisibility(View.GONE);
        }
    }

    protected final void setRightText(int resId){
        if (resId > 0 && !TextUtils.isEmpty(getResources().getText(resId))){
            mRightText.setVisibility(View.VISIBLE);
            mRightText.setText(resId);
        }else {
            mRightText.setVisibility(View.GONE);
        }
    }

    protected final void setLeftImage(int resId){
        if (resId > 0){
            mLeftImage.setVisibility(View.VISIBLE);
            mLeftImage.setImageResource(resId);
        }else {
            mLeftImage.setVisibility(View.GONE);
        }
    }

    protected final void setRightImage(int resId){
        if (resId > 0){
            mRightImage.setVisibility(View.VISIBLE);
            mRightImage.setImageResource(resId);
        }else {
            mRightImage.setVisibility(View.GONE);
        }
    }

    protected final void setRightImage2(int resId){
        if (resId > 0){
            mRightImage2.setVisibility(View.VISIBLE);
            mRightImage2.setImageResource(resId);
        }else {
            mRightImage2.setVisibility(View.GONE);
        }
    }

    public void showProgress(boolean cancellable){
        mFailedView.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);
        if (progress == null){
            progress = ProgressHUD.create(this);
        }
        progress.setCancellable(cancellable);
        progress.show();
    }

    public void cancelProgress(){
        if (progress != null && progress.isShowing()){
            progress.dismiss();
        }
    }

    public void showFailedView(View.OnClickListener listener){
        mFailedView.setVisibility(View.VISIBLE);
        mContent.setVisibility(View.GONE);
        setOnFailedClick(listener);
    }

    public void setOnFailedClick(View.OnClickListener listener){
        mReload.setOnClickListener(listener);
    }

    /**
     * 是否延迟调用bindview,如果延迟，则需在子类自行调用ButterKnife.bind对view进行注解
     * @return
     */
    protected boolean delayBindView(){
        return false;
    }

    @Override
    protected void onDestroy() {
        if (mBinder != null){
            mBinder.unbind();
        }
        if (progress != null && progress.isShowing()){
            progress.dismiss();
            progress = null;
        }
        super.onDestroy();
    }
}
