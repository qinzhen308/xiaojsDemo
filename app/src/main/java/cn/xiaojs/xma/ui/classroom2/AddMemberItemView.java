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
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by Paul Z on 2017/11/6.
 */

public class AddMemberItemView extends LinearLayout implements IViewModel<Void> ,IEventer{



    int mPosition;

    @BindDimen(R.dimen.px70)
    int pageViewHeight;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;

    private CircleTransform circleTransform;

    public AddMemberItemView(Context context) {
        super(context);
        init();
    }

    public AddMemberItemView(Context context, AttributeSet attrs) {
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
    public void bindData(int position, Void data) {
        this.mPosition = position;

        tvName.setText("添加成员");

        ivAvatar.setImageResource(R.drawable.img_add);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEventCallback!=null){
                    mEventCallback.onEvent(EventCallback.EVENT_2,mPosition);
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
