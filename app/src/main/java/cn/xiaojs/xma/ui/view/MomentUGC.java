package cn.xiaojs.xma.ui.view;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/12/11
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.DeviceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MomentUGC extends RelativeLayout {

    @BindView(R.id.ugc_praise)
    TextView mPraise;
    @BindView(R.id.ugc_comment)
    TextView mComment;
    @BindView(R.id.ugc_share)
    TextView mShare;
    @BindView(R.id.ugc_more)
    ImageView mMore;

    private OnItemClickListener listener;

    public MomentUGC(Context context) {
        super(context);
        init();
    }

    public MomentUGC(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MomentUGC(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_moment_ugc, this);
        ButterKnife.bind(this);
        mPraise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onPraise();
                }
            }
        });

        mComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onComment();
                }
            }
        });

        mShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onShare();
                }
            }
        });

        mMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onMore();
                }
            }
        });

        DeviceUtil.expandViewTouch(mMore,getResources().getDimensionPixelSize(R.dimen.px100));
    }

    public void setPraise(int count){
        mPraise.setText(String.valueOf(count));
    }

    public void setComment(int count){
        mComment.setText(String.valueOf(count));
    }

    public void setShare(int count){
        mShare.setText(String.valueOf(count));
    }

    public void setCommentDrawable(int resId){
        mComment.setCompoundDrawablesWithIntrinsicBounds(resId,0,0,0);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onPraise();
        void onComment();
        void onShare();
        void onMore();
    }
}
