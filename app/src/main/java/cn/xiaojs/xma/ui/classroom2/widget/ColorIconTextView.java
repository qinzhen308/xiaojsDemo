package cn.xiaojs.xma.ui.classroom2.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.Random;

/**
 * Created by maxiaobao on 2017/10/12.
 */

public class ColorIconTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint paint;

    private String[] colorStrings = {"#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
            "#2196F3", "#03A9F4", "#00BCD4", "#009688","#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107",
    "#FF9800","#FF5722","#795548", "#9E9E9E", "#607D8B"};

    public ColorIconTextView(Context context) {
        super(context);
        init();
    }

    public ColorIconTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorIconTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        Random random = new Random();
        int colorIndex = random.nextInt(colorStrings.length-1);

        int color = Color.parseColor(colorStrings[colorIndex]);


        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, getMeasuredWidth()/2,paint);
        super.onDraw(canvas);


    }


}
