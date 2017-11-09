package cn.xiaojs.xma.ui.classroom.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.BitmapUtils;

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
 * Date:2016/12/5
 * Desc:
 *
 * ======================================================================================== */

public class ClassroomPopupWindowLayout extends LinearLayout {
    public final static int DARK_GRAY = 1;
    public final static int LIGHT_GRAY = 2;
    public final static int TRANS_60 = 3;
    private int mGravity;

    private ImageView mIndicator;

    public ClassroomPopupWindowLayout(Context context) {
        super(context);
    }

    public ClassroomPopupWindowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClassroomPopupWindowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ClassroomPopupWindowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void addContent(View content, int gravity) {
        addContent(content, gravity, TRANS_60);
    }

    public void addContent(View content, int gravity, int style) {
        if (style == DARK_GRAY){
            content.setBackgroundResource(R.drawable.wb_black_conner_bg);
        }else if (style == LIGHT_GRAY){
            content.setBackgroundResource(R.drawable.wb_conner_bg);
        }else if(style == TRANS_60){
            content.setBackgroundResource(R.drawable.bg_wb_tool_panel);
        }
        //content.setBackgroundResource(R.drawable.wb_conner_bg);
        //content.setBackgroundColor(Color.RED);
        mIndicator = buildIndicator(gravity, style);
        mGravity = gravity;
        switch (gravity) {
            case Gravity.TOP:
                setOrientation(LinearLayout.VERTICAL);
                addView(mIndicator);
                addView(content);
                break;
            case Gravity.BOTTOM:
                setOrientation(LinearLayout.VERTICAL);
                addView(content);
                addView(mIndicator);
                break;
            case Gravity.LEFT:
                setOrientation(LinearLayout.HORIZONTAL);
                addView(content);
                addView(mIndicator);
                break;
            case Gravity.RIGHT:
                setOrientation(LinearLayout.HORIZONTAL);
                addView(mIndicator);
                addView(content);
                break;
        }

        setIndicatorOffsetX(0);
    }

    public ImageView buildIndicator(int gravity, int style) {
        int arrawRes=0;
        Bitmap bmp=null;
        if (style == DARK_GRAY){
            arrawRes=R.drawable.ic_cr_pop_indicator_dark_gray;
            bmp = BitmapFactory.decodeResource(getResources(), arrawRes);
        }else if (style == LIGHT_GRAY){
            arrawRes=R.drawable.ic_cr_pop_indicator_gray;
            bmp = BitmapFactory.decodeResource(getResources(), arrawRes);
        }else if(style == TRANS_60){
            bmp= Bitmap.createBitmap(30, 20, Bitmap.Config.ARGB_4444);
            Canvas canvas=new Canvas(bmp);
            Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setStyle(Paint.Style.FILL);
            p.setColor(getResources().getColor(R.color.black_opacity_60));
            Path path=new Path();
            path.moveTo(0,0);
            path.lineTo(15,20);
            path.lineTo(30,0);
            path.lineTo(0,0);
            canvas.drawPath(path,p);
        }
        ImageView img = new ImageView(getContext());

        if (bmp != null) {
            switch (gravity) {
                case Gravity.TOP:
                    bmp = BitmapUtils.rotateBitmap(bmp, 180, true);
                    break;
                case Gravity.BOTTOM:
                    //default
                    break;
                case Gravity.LEFT:
                    bmp = BitmapUtils.rotateBitmap(bmp, -90, true);
                    break;
                case Gravity.RIGHT:
                    bmp = BitmapUtils.rotateBitmap(bmp, 90, true);
                    break;
            }
        }

        img.setImageBitmap(bmp);
        return img;
    }

    public void setIndicatorOffsetX(int offsetX) {
        if (mIndicator != null) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)mIndicator.getLayoutParams();
            if (mGravity == Gravity.TOP || mGravity == Gravity.BOTTOM){
                params.leftMargin = offsetX;
            }else {
                params.topMargin = offsetX;
            }
            mIndicator.setLayoutParams(params);
        }
    }

}
