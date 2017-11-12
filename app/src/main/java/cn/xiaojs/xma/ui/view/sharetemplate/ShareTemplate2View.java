package cn.xiaojs.xma.ui.view.sharetemplate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;

import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/11/11.
 */

public class ShareTemplate2View extends BaseShareTemplateView {
    public static final String tip1 = "长按识别二维码加入教室，观看直播";
    Paint paint;
    Paint measurePaint;
    private int color_font_white;
    private int color_gray;

    Bitmap bmLogo;
    Bitmap bmShareShadow;

    public ShareTemplate2View(Context context) {
        super(context);
        init();
    }

    public ShareTemplate2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShareTemplate2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        measurePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        color_font_white = getResources().getColor(R.color.white);
        color_gray = getResources().getColor(R.color.classroom_gray_1);
        paint.setStrokeWidth(2);
        bmLogo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_share_logo);
        bmShareShadow = BitmapFactory.decodeResource(getResources(), R.drawable.bg_share_shadow);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        if (width == 0 || height == 0) return;

        if (bmShareShadow != null) {
            Rect shadowRect = new Rect();
            int left = 0;
            int right = 0;
            int top = (int) (height * 622f / 1130);
            int bottom = height;
            shadowRect.set(left, top, right, bottom);
            canvas.drawBitmap(bmLogo, new Rect(0, 0, bmLogo.getWidth(), bmLogo.getHeight()), shadowRect, paint);
        }

        if (bmLogo != null) {
            Rect logoRect = new Rect();
            int left = (int) (width * 30f / 750);
            int right = (int) (width * 107f / 750) + left;
            int top = (int) (height * 30f / 1130);
            int bottom = (int) (height * 43f / 1130) + top;
            logoRect.set(left, top, right, bottom);
            canvas.drawBitmap(bmLogo, new Rect(0, 0, bmLogo.getWidth(), bmLogo.getHeight()), logoRect, paint);
        }
        if (bmAvatar != null) {
            RectF avatarRect = new RectF();
            int left = (int) (width * 50f / 750);
            int right = (int) (width * 85f / 750) + left;
            int top = (int) (height * 895f / 1130);
            int bottom = (int) (height * 85f / 1130) + top;
            avatarRect.set(left, top, right, bottom);
            paint.setColor(color_gray);
            canvas.drawOval(new RectF(avatarRect.left-2,avatarRect.top-2,avatarRect.right+2,avatarRect.bottom+2),paint);
            Path pathInner=new Path();
            pathInner.addOval(avatarRect, Path.Direction.CCW);
            canvas.save();
            canvas.clipPath(pathInner);
            canvas.drawBitmap(bmAvatar, new Rect(0, 0, bmAvatar.getWidth(), bmAvatar.getHeight()), avatarRect, paint);
            canvas.restore();
        }

        if (bmQrCode != null) {
            Rect qrcodeRect = new Rect();
            int left = (int) (width * 580f / 750);
            int right = (int) (width * 140f / 750) + left;
            int top = (int) (height * 920f / 1130);
            int bottom = (int) (height * 140f / 1130) + top;
            qrcodeRect.set(left, top, right, bottom);
            paint.setColor(color_gray);
            canvas.drawRect(qrcodeRect.left - 2, qrcodeRect.top - 2, qrcodeRect.right + 2, qrcodeRect.bottom + 2, paint);
            canvas.drawBitmap(bmQrCode, new Rect(0, 0, bmQrCode.getWidth(), bmQrCode.getHeight()), qrcodeRect, paint);
        }

        if (!TextUtils.isEmpty(techerName)) {
            float textSize = 40f * height / 1130;
            measurePaint.setTextSize(textSize);
            float x = 160f * width / 750;
            float y = 940f * height / 1130;
            paint.setTextSize(textSize);
            paint.setColor(color_font_white);
            canvas.drawText(techerName, 0, techerName.length(), x, y, paint);
        }

        if (!TextUtils.isEmpty(techerDescrib)) {
            float textSize = 26f * height / 1130;
            paint.setTextSize(textSize);
            paint.setColor(color_font_white);
            measurePaint.setTextSize(textSize);
            float length = measurePaint.measureText(techerDescrib, 0, techerDescrib.length());
            int maxLineLength = (int) (400f * width / 750);
            int line = ((int) length / maxLineLength) + 1;
            int strStart = 0;
            int strEnd = techerDescrib.length() / line;
            float x = 160f * width / 750;
            float y = 946f * height / 1130;
            float lineSpace = 5f * height / 1130;
            for (int i = 0; i < line; i++) {
                y += lineSpace + textSize;
                canvas.drawText(techerDescrib, strStart, strEnd, x, y, paint);
                strStart = strEnd;
                strEnd += techerDescrib.length() / line;
                if (strEnd > techerDescrib.length()) {
                    strEnd = techerDescrib.length();
                }
            }
        }

        if (!TextUtils.isEmpty(tip1)) {
            float textSize = 22f * height / 1130;
            measurePaint.setTextSize(textSize);
            float x = 50f * width / 750;
            float y = 1060f * height / 1130;
            paint.setTextSize(textSize);
            paint.setColor(color_font_white);
            canvas.drawText(tip1, 0, tip1.length(), x, y, paint);
        }


        if (!TextUtils.isEmpty(className)) {
            float textSize = 56f * height / 1130;
            paint.setTextSize(textSize);
            paint.setColor(color_font_white);
            measurePaint.setTextSize(textSize);
            float length = measurePaint.measureText(className, 0, className.length());
            int maxLineLength = (int) (650f * width / 750);
            int line = ((int) length / maxLineLength) + 1;
            int strStart = 0;
            int strEnd = className.length() / line;
            float lineSpace = 11f * height / 1130;
            float x = 50f * height / 1130;
            float y = 864f * height / 1130 - line * (lineSpace + textSize);
            for (int i = 0; i < line; i++) {
                y += lineSpace + textSize;
                canvas.drawText(className, strStart, strEnd, x, y, paint);
                strStart = strEnd;
                strEnd += className.length() / line;
                if (strEnd > className.length()) {
                    strEnd = className.length();
                }
            }
        }

        paint.setColor(color_font_white);
        canvas.drawLine(56f * width / 750, 1017.5f * height / 1130, 556f * width / 750, 1017.5f * height / 1130, paint);
    }

}
