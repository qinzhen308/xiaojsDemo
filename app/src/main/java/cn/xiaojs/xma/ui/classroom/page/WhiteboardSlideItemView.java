package cn.xiaojs.xma.ui.classroom.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class WhiteboardSlideItemView extends RelativeLayout implements IViewModel<SlideImgModel> {

    SlideImgModel mData;
    @BindView(R.id.iv_slide_img)
    ImageView ivSlideImg;
    @BindView(R.id.tv_page)
    TextView tvPage;

    @BindView(R.id.selector)
    View selector;


    @BindDimen(R.dimen.px70)
    int pageViewHeight;

    public WhiteboardSlideItemView(Context context) {
        super(context);
        init();
    }

    public WhiteboardSlideItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=(int)(9*width/16.0f)+pageViewHeight;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
        setMeasuredDimension(width,height);
    }

    private void init() {
        inflate(getContext(), R.layout.layout_whiteboard_slide_item, this);
        ButterKnife.bind(this);
    }


    @Override
    public void bindData(int position, SlideImgModel data) {
        mData = data;
        if(data.isSelected){
            selector.setVisibility(VISIBLE);
        }else {
            selector.setVisibility(GONE);
        }

        Glide.with(getContext()).load(data.url).into(ivSlideImg);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



}
