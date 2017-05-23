package cn.xiaojs.xma.ui.widget.banner;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by maxiaobao on 2017/5/23.
 */

public class PageNumView extends TextView implements ViewPager.OnPageChangeListener{

    private int totalPage = 0;
    private int currentPage = 1;
    private boolean mIsLoop = false;
    private ViewPager mPager = null;

    public PageNumView(Context context) {
        super(context);
    }

    public PageNumView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private void setTotalPage(int num) {
        totalPage = num;
    }

    private void showPage() {
        setText(currentPage + "/" + totalPage);
    }

    private void setCurrentPoint(int indicate) {
        currentPage = indicate;
        showPage();
    }

    public void setViewPager(ViewPager pager, int totalNum, int currentNum) {
        if (pager == null) {
            return;
        }
        if (totalNum == 0 || totalNum == 1) {
            mIsLoop = false;
        } else {
            mIsLoop = false;
        }

        setTotalPage(totalNum);

        setCurrentPoint(currentNum + 1);
        mPager = pager;
        mPager.setOnPageChangeListener(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        setCurrentPoint(position + 1);

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
//        if (!mIsLoop) {
//            setCurrentPoint(state);
//        } else {
//            setCurrentPoint(state % totalPage);
//        }
    }
}
