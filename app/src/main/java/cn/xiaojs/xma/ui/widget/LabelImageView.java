package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class LabelImageView extends android.support.v7.widget.AppCompatImageView {
    float labelWidth;
    float labelHeight;
    Drawable labelDrawable;
    String mMaskText;
    int mMaskColor;
    int mMaskTextColor;
    float mMaskTextSize;
    Paint mMaskPaint;

    public static final int LABEL_POSITION_LEFT_TOP=0;
    public static final int LABEL_POSITION_RIGHT_TOP=1;
    public static final int LABEL_POSITION_RIGHT_BOTTOM=2;
    public static final int LABEL_POSITION_LEFT_BOTTOM=3;
    int labelPosition=LABEL_POSITION_LEFT_BOTTOM;
    public final static int DEFAULT_TEXT_COLOR=0xFFFFFF;
    public final static int DEFAULT_MASK_COLOR=0x00000000;

    public LabelImageView(Context context) {
        super(context);
        init();
    }

    public LabelImageView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LabelImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LabelImageView, defStyleAttr, 0);
        labelPosition=a.getInt(R.styleable.LabelImageView_label_position,LABEL_POSITION_LEFT_BOTTOM);
        labelWidth=a.getDimension(R.styleable.LabelImageView_label_width,20);
        labelHeight=a.getDimension(R.styleable.LabelImageView_label_height,20);
        labelDrawable=a.getDrawable(R.styleable.LabelImageView_labelSrc);
        mMaskText=a.getString(R.styleable.LabelImageView_my_mask_text);
        mMaskTextColor=a.getColor(R.styleable.LabelImageView_my_mask_textcolor,DEFAULT_TEXT_COLOR);
        mMaskColor=a.getColor(R.styleable.LabelImageView_my_mask_color,DEFAULT_MASK_COLOR);
        mMaskTextSize=a.getDimension(R.styleable.LabelImageView_my_mask_textsize,20);
        a.recycle();
        init();
    }

    private void init(){
        mMaskPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        mMaskPaint.setStrokeWidth(1);
        mMaskPaint.setTextSize(mMaskTextSize);
        mMaskPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawMask(canvas);
        drawLabel(canvas);
    }


    private void drawLabel(Canvas canvas){
        if(labelDrawable==null){
            return;
        }
        final int saveCount = canvas.getSaveCount();
        canvas.save();
        labelDrawable.setBounds(0,0,(int)labelWidth,(int)labelHeight);
        if(labelPosition==LABEL_POSITION_RIGHT_BOTTOM){
//            labelDrawable.setBounds((int)(getWidth()-labelWidth),(int)(getHeight()-labelHeight),getWidth(),getHeight());
            canvas.translate(getWidth()-labelWidth,getHeight()-labelHeight);
        }else if(labelPosition==LABEL_POSITION_RIGHT_TOP){
//            labelDrawable.setBounds((int)(getWidth()-labelWidth),0,getWidth(),(int)labelHeight);
            canvas.translate(getWidth()-labelWidth,0);
        }else if(labelPosition==LABEL_POSITION_LEFT_TOP){
//            labelDrawable.setBounds(0,0,(int)labelWidth,(int)labelHeight);
            canvas.translate(0,0);
        }else if(labelPosition==LABEL_POSITION_RIGHT_TOP){
//            labelDrawable.setBounds((int)(getWidth()-labelWidth ),0,getWidth(),(int)labelHeight);
            canvas.translate(getWidth()-labelWidth,0);
        }
        labelDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private void drawMask(Canvas canvas){
        if(!TextUtils.isEmpty(mMaskText)){
            RectF rectF=new RectF(getPaddingLeft(),getPaddingTop(),getWidth()-getPaddingRight(),getHeight()-getPaddingBottom());
            mMaskPaint.setColor(mMaskColor);
            canvas.drawOval(rectF,mMaskPaint);
            mMaskPaint.setColor(mMaskTextColor);
            mMaskPaint.setTextSize(mMaskTextSize);
            float textWidth=mMaskPaint.measureText(mMaskText,0,mMaskText.length());
            canvas.drawText(mMaskText,0,mMaskText.length(),rectF.centerX()-textWidth/2,rectF.centerY()+mMaskTextSize/3,mMaskPaint);
        }
    }

    public void setLabelResource(@DrawableRes int res){
        labelDrawable=getResources().getDrawable(res);
        invalidate();
    }


    public void setMask(@ColorInt int color){
        mMaskColor=color;
        invalidate();
    }

    public void setMaskTextColor(@ColorInt int color){
        mMaskTextColor=color;
        invalidate();
    }

    public void setMaskText(String text){
        mMaskText=text;
        invalidate();
    }
    public void setMaskText(@StringRes int text){
        mMaskText=getResources().getString(text);
        invalidate();
    }

}
