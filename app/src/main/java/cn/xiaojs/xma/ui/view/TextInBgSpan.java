package cn.xiaojs.xma.ui.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

/**
 * Created by Paul Z on 2017/11/1.
 */


public class TextInBgSpan extends ReplacementSpan {


    int color;
    private int mWidth;
    Drawable mDrawable;

    /**
     * @param d 接收图片
     */
    public TextInBgSpan(Drawable d, int color) {
        this.color = color;
        mDrawable=d;
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        mWidth = (int) paint.measureText(text, start, end);
        return mWidth;
    }


    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                     int bottom, Paint paint) {
        Rect bounds = new Rect((int)x, top,(int) x + mWidth, bottom);
        //设置背景绘制宽高 根据字符串大小扩大一定比例 否则会紧贴字符
        if(mDrawable!=null){
            mDrawable.setBounds(bounds);
            mDrawable.draw(canvas);
        }
        paint.setColor(color);
        paint.setTypeface(Typeface.create("normal", Typeface.NORMAL));
        canvas.drawText(text, start, end, x, y, paint);
    }
}
