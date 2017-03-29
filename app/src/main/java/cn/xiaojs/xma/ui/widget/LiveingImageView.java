package cn.xiaojs.xma.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/3/28.
 */

@SuppressLint("AppCompatCustomView")
public class LiveingImageView extends ImageView {

    private Paint paint;
    private boolean showText = false;
    private String tips = "上课中";

    public LiveingImageView(Context context) {
        super(context);
        init();
    }

    public LiveingImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveingImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setShowText(boolean show) {
        showText = show;
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(R.color.white));
        paint.setTextSize(25);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (showText) {

            float textSize = paint.measureText(tips);

            float x = getWidth()/2-textSize/2;
            float y = getHeight()/2 +40;
            canvas.drawText("上课中",x,y,paint);
        }


    }
}
