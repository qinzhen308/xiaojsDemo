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
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.home.HomeConstant;
import cn.xiaojs.xma.ui.home.MomentDetailActivity;

public class MomentUGC extends RelativeLayout {

    @BindView(R.id.ugc_praise)
    TextView mPraise;
    @BindView(R.id.ugc_comment)
    TextView mComment;
    @BindView(R.id.ugc_share)
    TextView mShare;
    @BindView(R.id.ugc_more)
    ImageView mMore;

    private Dynamic mDynamic;
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
                SocialManager.likeActivity(getContext(), mDynamic.id, new APIServiceCallback<Dynamic.DynStatus>() {
                    @Override
                    public void onSuccess(Dynamic.DynStatus object) {
                        mDynamic.liked = !mDynamic.liked;
                        int newTotal = getTotal(mPraise);
                        if (mDynamic.liked){
                            newTotal++;
                        }else {
                            newTotal--;
                        }
                        praise(mDynamic.liked,newTotal);
                        if (listener != null){
                            listener.onPraise(mDynamic.liked,true);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        if (listener != null){
                            listener.onPraise(mDynamic.liked,false);
                        }
                    }
                });

            }
        });

        mComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MomentDetailActivity.class);
                intent.putExtra(HomeConstant.KEY_MOMENT_ID,mDynamic.id);
                getContext().startActivity(intent);
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

    }

    public void setMoreVisibility(int visibility) {
        mMore.setVisibility(visibility);

    }

    public View getMore(){
        return mMore;
    }

    private void praise(boolean status,int total){
        if (status){
            mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_praise_on,0,0,0);
        }else {
            mPraise.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_praise_off,0,0,0);
        }

        mPraise.setText(String.valueOf(total));
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

    private int getTotal(TextView view){
        String strInt = view.getText().toString();
        int Int = 0;
        try {
            Int = Integer.parseInt(strInt);
        }catch (Exception e){
        }
        return Int;
    }

    public void setStatus(Dynamic status){
        mDynamic = status;
        if (mDynamic == null)
            return;
        mComment.setText(String.valueOf(mDynamic.stats.comments));
        mShare.setText(String.valueOf(mDynamic.stats.shared));
        praise(mDynamic.liked,mDynamic.stats.liked);
        //FIXME 等动态分享H5页面出来后，需要打开以下注释代码
//        if (mDynamic.scope == Social.ShareScope.PUBLIC){//公开动态可分享
//            mShare.setVisibility(VISIBLE);
//        }else {
//            mShare.setVisibility(GONE);
//        }
    }



    public interface OnItemClickListener{
        void onPraise(boolean liked,boolean success);
        void onComment();
        void onShare();
        void onMore();
    }
}
