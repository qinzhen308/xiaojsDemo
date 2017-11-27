package cn.xiaojs.xma.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/11/27.
 */

public class IndicatorView extends View{

    private float point_radius=10;
    private float point_magin=15;
    private float selected_point_length=40;
    private int selected_point_color;
    private int point_color;

    private float mOffset;
    private int mCurrentPage;
    private int mState=ViewPager.SCROLL_STATE_IDLE;

    Paint paint=new Paint();

    ViewPager mViewPager;

    public IndicatorView(Context context) {
        super(context);
        init();

    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
        init();

    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.LabelImageView, defStyleAttr, 0);
        point_color=a.getColor(R.styleable.IndicatorView_point_color,getResources().getColor(R.color.gray));
        selected_point_color=a.getColor(R.styleable.IndicatorView_selected_point_color,getResources().getColor(R.color.main_orange));
        selected_point_length=a.getColor(R.styleable.IndicatorView_selected_point_length,40);
        point_magin=a.getDimension(R.styleable.IndicatorView_point_magin,15);
        point_radius=a.getDimension(R.styleable.IndicatorView_point_radius,10);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mViewPager==null){
            int width=(int)(2*point_radius);
            setMeasuredDimension(width,(int)(2*point_radius));
        }else {
            int width=(int)((mViewPager.getAdapter().getCount()-1)*(2*point_radius+point_magin)+selected_point_length);
            setMeasuredDimension(width,(int)(2*point_radius));
        }
    }

    public void bindViewPager(ViewPager viewPager) {
        if(viewPager.getAdapter()==null){
            throw new RuntimeException("please set Adapter for ViewPager first");
        }
        mViewPager=viewPager;
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int tempPosition;
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                /*if(Math.abs(mOffset)-positionOffset>0){
                    mOffset=1-positionOffset;
                }else {
                    mOffset=positionOffset;
                }*/
                mCurrentPage=position;
                mOffset=positionOffset;
                invalidate();
                Logger.d("----qz----indicator---onPageScrolled--offset="+positionOffset+"---------position="+position);
            }

            @Override
            public void onPageSelected(int position) {
                tempPosition=position;
                Logger.d("----qz----indicator---onPageSelected--position="+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Logger.d("----qz----indicator---onPageScrollStateChanged--state="+state);
                mState=state;
                if(state==ViewPager.SCROLL_STATE_IDLE){
//                    mCurrentPage=tempPosition;
                    invalidate();
                }
            }
        });
        requestLayout();
    }

    private void init(){

        paint.setStyle(Paint.Style.FILL);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setStrokeWidth(2*point_radius);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mViewPager==null&&mViewPager.getAdapter()==null||mViewPager.getAdapter().getCount()==0){
            return;
        }
        int position=mCurrentPage;
        int state=mState;
        float offset=mOffset;
        int size=mViewPager.getAdapter().getCount();
        float x=point_radius;
        int y=getHeight()/2;
        float selected_startX=0;
        float selected_endX=0;
        paint.setColor(point_color);
        for(int i=0;i<size;i++){
            if(i==position){
                selected_startX=x;
                selected_endX=selected_startX+selected_point_length-2*point_radius;
                canvas.drawPoint(x,y,paint);
                x+=selected_point_length+point_magin;
            }else{
                canvas.drawPoint(x,y,paint);
                x+=2*point_radius+point_magin;
            }
        }
        paint.setColor(selected_point_color);
//        float offsetX=mOffset*selected_point_length/2;
//        canvas.drawLine(selected_startX+offsetX,y,selected_endX+offsetX,y,paint);

        if(state== ViewPager.SCROLL_STATE_SETTLING){
            float offsetX=offset*point_magin;
            canvas.drawLine(selected_startX+offsetX,y,selected_endX+offsetX,y,paint);
            invalidate();
        }else if(state== ViewPager.SCROLL_STATE_IDLE){
            canvas.drawLine(selected_startX,y,selected_endX,y,paint);
        }else if(state== ViewPager.SCROLL_STATE_DRAGGING){
            float offsetX=offset*point_magin;
            canvas.drawLine(selected_startX+offsetX,y,selected_endX+offsetX,y,paint);
        }
    }


}
