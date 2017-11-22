package cn.xiaojs.xma.ui.classroom2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.IEventer;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by Paul Z on 2017/11/6.
 */

public class MemberItemView extends LinearLayout implements IViewModel<Attendee> ,IEventer {


    Attendee mData;

    int position;

    @BindDimen(R.dimen.px70)
    int pageViewHeight;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;

    private CircleTransform circleTransform;

    public MemberItemView(Context context) {
        super(context);
        init();
    }

    public MemberItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_classroom2_member, this);
        setOrientation(VERTICAL);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width + pageViewHeight;
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        setMeasuredDimension(width, height);
    }

    @Override
    public void bindData(int position, Attendee data) {
        mData = data;
        this.position = position;

        tvName.setText(mData.name);

        Glide.with(getContext())
                .load(cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(mData.accountId, 300))
                .bitmapTransform(circleTransform)
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(ivAvatar);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEventCallback!=null){
                    mEventCallback.onEvent(EventCallback.EVENT_1,MemberItemView.this.position);
                }
            }
        });
    }


    private EventCallback mEventCallback;

    @Override
    public void setEventCallback(EventCallback eventCallback) {
        mEventCallback = eventCallback;
    }
}
