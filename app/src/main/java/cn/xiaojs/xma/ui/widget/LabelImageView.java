package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
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

    public static final int LABEL_POSITION_LEFT_TOP=0;
    public static final int LABEL_POSITION_RIGHT_TOP=1;
    public static final int LABEL_POSITION_RIGHT_BOTTOM=2;
    public static final int LABEL_POSITION_LEFT_BOTTOM=3;
    int labelPosition=LABEL_POSITION_LEFT_BOTTOM;

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
        a.recycle();
        init();
    }

    private void init(){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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

    public void setLabelResource(@DrawableRes int res){
        labelDrawable=getResources().getDrawable(res);
        invalidate();
    }


}
