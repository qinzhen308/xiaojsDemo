package cn.xiaojs.xma.ui.classroom2.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Random;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.contact2.model.ContactGroupStrategy;
import cn.xiaojs.xma.ui.contact2.query.TextComparator;

/**
 * Created by maxiaobao on 2017/10/12.
 */

public class ColorIconTextView extends android.support.v7.widget.AppCompatTextView {

    //private Paint paint;

//    private String[] colorStrings = {"#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
//            "#2196F3", "#03A9F4", "#00BCD4", "#009688","#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107",
//    "#FF9800","#FF5722","#795548", "#9E9E9E", "#607D8B"};

    //private String[] colorStrings = {"#6ED0AA", "#47BDF6", "#FF7841", "#FF567E", "#89CC4F", "#7C68DF"};


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

        setBackgroundResource(R.drawable.ic_classdefaultavatar);

//        Random random = new Random();
//        int colorIndex = random.nextInt(colorStrings.length-1);
//
//        int color = Color.parseColor(colorStrings[colorIndex]);


        //paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //paint.setColor(color);

    }

//    public void setIconWithText(String text) {
//
//        String group = TextComparator.getLeadingUp(text);
//        String flag = !TextUtils.isEmpty(group) ? group : ContactGroupStrategy.GROUP_SHARP;
//
//        String colorstr;
//
//        if ("A".equalsIgnoreCase(flag)
//                || "B".equalsIgnoreCase(flag)
//                || "C".equalsIgnoreCase(flag)
//                || "D".equalsIgnoreCase(flag)
//                || "E".equalsIgnoreCase(flag)) {
//            colorstr = colorStrings[0];
//
//        }else if ("F".equalsIgnoreCase(flag)
//                || "G".equalsIgnoreCase(flag)
//                || "H".equalsIgnoreCase(flag)
//                || "I".equalsIgnoreCase(flag)
//                || "J".equalsIgnoreCase(flag)) {
//            colorstr = colorStrings[1];
//        }else if ("K".equalsIgnoreCase(flag)
//                || "L".equalsIgnoreCase(flag)
//                || "M".equalsIgnoreCase(flag)
//                || "N".equalsIgnoreCase(flag)
//                || "O".equalsIgnoreCase(flag)) {
//            colorstr = colorStrings[2];
//        }else if ("P".equalsIgnoreCase(flag)
//                || "Q".equalsIgnoreCase(flag)
//                || "R".equalsIgnoreCase(flag)
//                || "S".equalsIgnoreCase(flag)
//                || "T".equalsIgnoreCase(flag)
//                || "U".equalsIgnoreCase(flag)) {
//            colorstr = colorStrings[3];
//        }else if ("V".equalsIgnoreCase(flag)
//                || "W".equalsIgnoreCase(flag)
//                || "X".equalsIgnoreCase(flag)
//                || "Y".equalsIgnoreCase(flag)
//                || "Z".equalsIgnoreCase(flag)) {
//            colorstr = colorStrings[4];
//        }else {
//            colorstr = colorStrings[5];
//        }
//
//        int color = Color.parseColor(colorstr);
//        paint.setColor(color);
//
//        setText(text);
//    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, getMeasuredWidth()/2,paint);
//        super.onDraw(canvas);
//
//
//    }


}
