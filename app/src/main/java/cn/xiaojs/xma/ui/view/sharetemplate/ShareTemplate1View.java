package cn.xiaojs.xma.ui.view.sharetemplate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/11/11.
 */

public class ShareTemplate1View extends BaseShareTemplateView{
    public static final String tip1="邀 请 您 加 入";
    public static final String tip2="长按识别二维码加入教室";
    public static final String tip3="观看直播";
    Paint paint;
    Paint measurePaint;
    private int color_font_black;
    private int color_font_shit;
    private int color_gray;

    Bitmap bmLogo;

    public ShareTemplate1View(Context context) {
        super(context);
        init();
    }

    public ShareTemplate1View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShareTemplate1View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        measurePaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        color_font_black=getResources().getColor(R.color.font_black);
        color_font_shit=getResources().getColor(R.color.yellow_shit);
        color_gray=getResources().getColor(R.color.classroom_gray_1);
        paint.setStrokeWidth(2);
        bmLogo= BitmapFactory.decodeResource(getResources(),R.drawable.ic_share_logo);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width=getWidth();
        int height=getHeight();
        if(width==0||height==0)return;

        if(bmLogo!=null){
            Rect logoRect=new Rect();
            int left=(int)(width*30f/750);
            int right=(int)(width*107f/750)+left;
            int top=(int)(height*30f/1130);
            int bottom=(int)(height*43f/1130)+top;
            logoRect.set(left,top,right,bottom);
            canvas.drawBitmap(bmLogo,new Rect(0,0,bmLogo.getWidth(),bmLogo.getHeight()),logoRect,paint);
        }
        if(bmAvatar!=null){
            RectF avatarRect=new RectF();
            int left=(int)(width*308.5f/750);
            int right=(int)(width*133f/750)+left;
            int top=(int)(height*150.5/1130);
            int bottom=(int)(height*133f/1130)+top;
            avatarRect.set(left,top,right,bottom);
            Path pathInner=new Path();
            pathInner.addOval(avatarRect, Path.Direction.CCW);
            canvas.save();
            canvas.clipPath(pathInner);
            canvas.drawBitmap(bmAvatar,new Rect(0,0,bmAvatar.getWidth(),bmAvatar.getHeight()),avatarRect,paint);
            canvas.restore();
        }

        if(bmQrCode!=null){
            Rect qrcodeRect=new Rect();
            int left=(int)(width*275f/750);
            int right=(int)(width*200f/750)+left;
            int bottom=(int)(height*930f/1130);
            int top=bottom-(int)(height*200f/1130);
            qrcodeRect.set(left,top,right,bottom);
            paint.setColor(color_gray);
            canvas.drawRect(qrcodeRect.left-2,qrcodeRect.top-2,qrcodeRect.right+2,qrcodeRect.bottom+2,paint);
            canvas.drawBitmap(bmQrCode,new Rect(0,0,bmQrCode.getWidth(),bmQrCode.getHeight()),qrcodeRect,paint);
        }

        if(!TextUtils.isEmpty(techerName)){
            float textSize=34f*height/1130;
            measurePaint.setTextSize(textSize);
            float lenght=measurePaint.measureText(techerName,0,techerName.length());
            float x=(width-lenght)/2;
            float y=370f*height/1130;
            paint.setTextSize(textSize);
            paint.setColor(color_font_shit);
            canvas.drawText(techerName,0,techerName.length(),x,y,paint);
        }

        if(!TextUtils.isEmpty(tip1)){
            float textSize=34f*height/1130;
            measurePaint.setTextSize(textSize);
            float lenght=measurePaint.measureText(tip1,0,tip1.length());
            float x=(width-lenght)/2;
            float y=430f*height/1130;
            paint.setTextSize(textSize);
            paint.setColor(color_font_black);
            canvas.drawText(tip1,0,tip1.length(),x,y,paint);
        }

        if(!TextUtils.isEmpty(tip2)){
            float textSize=28f*height/1130;
            measurePaint.setTextSize(textSize);
            float lenght=measurePaint.measureText(tip2,0,tip2.length());
            float x=(width-lenght)/2;
            float y=1000f*height/1130;
            paint.setTextSize(textSize);
            paint.setColor(color_font_black);
            canvas.drawText(tip2,0,tip2.length(),x,y,paint);
        }

        if(!TextUtils.isEmpty(tip3)){
            float textSize=28f*height/1130;
            measurePaint.setTextSize(textSize);
            float lenght=measurePaint.measureText(tip3,0,tip3.length());
            float x=(width-lenght)/2;
            float y=1050f*height/1130;
            paint.setTextSize(textSize);
            paint.setColor(color_font_black);
            canvas.drawText(tip3,0,tip3.length(),x,y,paint);
        }

        if(!TextUtils.isEmpty(className)){
            float textSize=46f*height/1130;
            paint.setTextSize(textSize);
            paint.setColor(color_font_shit);
            measurePaint.setTextSize(textSize);
            float length=measurePaint.measureText(className,0,className.length());
            int maxLineLength=(int)(450f*width/750);
            int line=((int)length/maxLineLength)+1;
            int strStart=0;
            int strEnd=className.length()/line;
            float x=0;
            float y=500f*height/1130;
            float line1Y=y;
            float lineSpace=8*height/1130;
            for(int i=0;i<line;i++){
                length=measurePaint.measureText(className,strStart,strEnd);
                x=(width-length)/2;
                y+=lineSpace+textSize;
                canvas.drawText(className,strStart,strEnd,x,y,paint);
                strStart=strEnd;
                strEnd+=className.length()/line;
                if(strEnd>className.length()){
                    strEnd=className.length();
                }
            }
            float lineTagLength=50f*width/750;
            float lineTagSpace=20f*width/750;
            float lineTagX=line>1?maxLineLength:length;
            canvas.drawLine((width-lineTagX)/2-lineTagSpace,(line1Y+y+lineSpace)/2,(width-lineTagX)/2-lineTagSpace-lineTagLength,(line1Y+y+lineSpace)/2,paint);
            canvas.drawLine(width-((width-lineTagX)/2-lineTagSpace),(line1Y+y+lineSpace)/2,width-((width-lineTagX)/2-lineTagSpace-lineTagLength),(line1Y+y+lineSpace)/2,paint);
        }
    }

}
