package cn.xiaojs.xma.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

/**
 * Created by Paul Z on 2017/5/11.
 *
 * 文字超过maxlines，末尾重新设置成...且留出部分空白
 * 监听是否有省略字符
 */

public class MaxLineTextView extends android.support.v7.widget.AppCompatTextView {
    protected boolean isOverSize;
    private OnOverSizeChangedListener changedListener;

    private boolean isInitFinsh=false;


    public MaxLineTextView(Context context) {
        super(context);
        init();
    }

    public MaxLineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaxLineTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!refuseListener&&changedListener != null) {
            changedListener.onChanged(checkOverLine());
        }
    }

    private void init() {
        // invalidate when layout end
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (!isInitFinsh&&changedListener != null) {
                    isInitFinsh=true;
                    changedListener.onChanged(checkOverLine());
                }
            }
        });

    }

    private boolean refuseListener=false;
    public void resetTextWithoutListener(){
        refuseListener=true;
        finallyString();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void finallyString(){
        int index=getLayout().getLineVisibleEnd(getMaxLines()-1);
        CharSequence str=getText().subSequence(0,index-5);
        setText(str.toString()+"...");
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public boolean checkOverLine() {
        int maxLine = getMaxLines();
        Layout layout = getLayout();
        if(layout==null){
            return false;
        }
        isOverSize = layout.getEllipsisCount(maxLine - 1) > 0 ? true : false;
        return isOverSize;
    }

    public boolean isOverSize() {
        return isOverSize;
    }

    public void displayAll() {
        setMaxLines(Integer.MAX_VALUE);
        setEllipsize(null);
    }

    public void hide(int maxLine) {
        setMaxLines(maxLine);
        setEllipsize(TextUtils.TruncateAt.END);
    }



    // set a listener for callback
    public OnOverSizeChangedListener getChangedListener() {
        return changedListener;
    }

    public void setOnOverLineChangedListener(OnOverSizeChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    public interface OnOverSizeChangedListener {

        public void onChanged(boolean isOverSize);
    }


}
