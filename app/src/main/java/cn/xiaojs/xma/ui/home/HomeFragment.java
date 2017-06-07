package cn.xiaojs.xma.ui.home;

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
 * Date:2016/10/11
 * Desc:
 *
 * ======================================================================================== */

import android.animation.Animator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaola.qrcodescanner.qrcode.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.DynamicStatus;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.lesson.xclass.LessonScheduleActivity;
import cn.xiaojs.xma.ui.lesson.xclass.animlib.Xiaojs120Anim;
import cn.xiaojs.xma.ui.message.PostDynamicActivity;
import cn.xiaojs.xma.ui.search.SearchActivity;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.ui.widget.banner.BannerAdapter;
import cn.xiaojs.xma.ui.widget.banner.BannerBean;
import cn.xiaojs.xma.ui.widget.banner.BannerView;
import cn.xiaojs.xma.util.DeviceUtil;

public class HomeFragment extends BaseFragment {

    public static final String ACTION_UPDATE_FOWLLED = "au_followed";
    public static final String EXTRA_FOWLLED_ID = "account_id";

    public static final String ACTION_UPDATE_UN_FOWLLED = "au_unfollowed";


    @BindView(R.id.home_banner)
    BannerView mBanner;

    @BindView(R.id.title)
    View mTitle;
    @BindView(R.id.home_moment_mark_wrapper)
    RelativeLayout mMark;
    @BindView(R.id.home_moment_tip)
    TextView mMarkTip;
    @BindView(R.id.home_moment_mark_right_wrapper)
    LinearLayout mRightMark;
    @BindView(R.id.portrait)
    RoundedImageView mPortraitImg;
    @BindView(R.id.home_right_moment_tip)
    TextView mRightMarkTip;

    @BindView(R.id.empty_desc)
    TextView mEmptyDesc;
    @BindView(R.id.empty_image)
    ImageView mEmptyImage;

    PullToRefreshSwipeListView mList;
    private boolean mScrolled;

    private HomeMomentAdapter mAdapter;

    private UpdateFollowReceiver updateFollowReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        updateFollowReceiver = new UpdateFollowReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_UPDATE_FOWLLED);

        getActivity().registerReceiver(updateFollowReceiver,intentFilter);

    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(updateFollowReceiver);
        super.onDetach();
    }

    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_home, null);
        mList = (PullToRefreshSwipeListView) v.findViewById(R.id.home_list);
        View header = mContext.getLayoutInflater().inflate(R.layout.layout_home_list_header,null);
        mList.getRefreshableView().addHeaderView(header);
        return v;
    }

    @Override
    protected void init() {
        mAdapter = new HomeMomentAdapter(mContext,mList);
        mAdapter.setDesc(mContent.getResources().getString(R.string.moment_data_empty));
        mAdapter.setFragment(this);
        mList.setAdapter(mAdapter);

        BannerBean b1 = new BannerBean();
        //BannerBean b2 = new BannerBean();
        //BannerBean b3 = new BannerBean();
        //BannerBean b4 = new BannerBean();
        b1.resId = R.drawable.ic_ad_help;
        //b2.resId = R.drawable.ic_ad_ii;
        //b3.resId = R.drawable.ic_ad_iii;
//        b4.resId = R.drawable.ic_ad;

        List<BannerBean> beanList = new ArrayList<>();
        beanList.add(b1);
        //beanList.add(b2);
//        beanList.add(b3);
        //beanList.add(b4);

        ViewGroup.LayoutParams lp=mBanner.getLayoutParams();
        lp.height= (int)(ScreenUtils.getScreenWidth(getActivity())*0.16f);
        mBanner.setLayoutParams(lp);
        BannerAdapter adapter = new BannerAdapter(mContext,beanList);
        mBanner.setAdapter(adapter);

        mList.getRefreshableView().setOnScrollListener(new OnScrollYListener(mList.getRefreshableView().getWrappedList()) {
            @Override
            public void onScrollY(int y) {
//                handleScrollChanged(y);
                mark();
            }
        });




    }

    @Override
    protected int registerDataChangeListenerType() {
        return SimpleDataChangeListener.DYNAMIC_CHANGED;
    }

    @Override
    protected void onDataChanged() {
        //do something
        toRefresh();
    }


    private void handleScrollChanged(int offsetY) {
        if (offsetY >= 0) {
            int bannerHeight = mBanner.getMeasuredHeight();
            if (offsetY >= 0 && offsetY <= bannerHeight) {
                mTitle.setBackgroundResource(R.drawable.ic_home_title_bg);
            }else if (offsetY > bannerHeight){
                int bgColor = Color.argb(255,0xf5,0xf5,0xf5);
                mTitle.setBackgroundColor(bgColor);
            }
        }
    }

    @OnClick({R.id.home_moment_mark_wrapper,R.id.home_moment_mark_right_wrapper,R.id.iv_write_dynamic})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_moment_mark_wrapper:
            case R.id.home_moment_mark_right_wrapper:
                Intent intent = new Intent(mContext,MomentUpdateActivity.class);
                ((BaseActivity)mContext).startActivityForResult(intent,HomeConstant.REQUEST_CODE_UPDATE);
                break;
            case R.id.iv_write_dynamic:
                startActivityForResult(new Intent(getActivity(), PostDynamicActivity.class), BaseConstant.REQUEST_CODE_SEND_MOMENT);
                break;
        }
    }

    //动态更新的提示转换动画
    private void mark(){
        if (!mScrolled){
            mMark.animate().translationX(getMarkScrollDistance()).start();
            mMark.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mMark.setVisibility(View.INVISIBLE);
                    mRightMark.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mScrolled = true;
        }
    }

    private int getMarkScrollDistance(){
        int l[] = new int[2];
        mMark.getLocationOnScreen(l);
        int endX = DeviceUtil.getScreenWidth(mContext) - mMark.getWidth() / 2;
        return endX - l[0];
    }

    public void notifyUpdates(CollectionPage<Dynamic> dynamic){
        int updates = dynamic != null ? dynamic.totalUpdates : 0;
        if (updates > 0){
            if (mScrolled){
                mRightMark.setVisibility(View.VISIBLE);
                mMark.setVisibility(View.INVISIBLE);
                mRightMarkTip.setText(getString(R.string.dynamic_update,updates));
                mMarkTip.setText(getString(R.string.dynamic_about_me,updates));
            }else {
                mRightMark.setVisibility(View.INVISIBLE);
                mMark.setVisibility(View.VISIBLE);
                mRightMarkTip.setText(getString(R.string.dynamic_update,updates));
                mMarkTip.setText(getString(R.string.dynamic_about_me,updates));
            }
        }else {
            mScrolled = true;
            mRightMark.setVisibility(View.INVISIBLE);
            mMark.setVisibility(View.INVISIBLE);
        }
    }

    public void showOrHiddenEmptyViiew(boolean hidden,String error) {
        if (hidden){
            mEmptyDesc.setVisibility(View.GONE);
            mEmptyImage.setVisibility(View.GONE);
        }else{
            mEmptyDesc.setVisibility(View.VISIBLE);
            if(!TextUtils.isEmpty(error)){
                mEmptyDesc.setText(error);
            }

            mEmptyImage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mBanner.start();
//        if(guide==null){
//            guide=new Xiaojs120Anim(getActivity());
//            mContent.post(new Runnable() {
//                @Override
//                public void run() {
//                    guide.attachCircleEditView(getActivity(),mContent.findViewById(R.id.iv_write_dynamic));
//                }
//            });
//        }
    }

//    Xiaojs120Anim guide;

    @Override
    public void onPause() {
        super.onPause();
        mBanner.stop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case HomeConstant.REQUEST_CODE_UPDATE:
                if (resultCode == Activity.RESULT_OK){
                    //进入过动态更新界面，并成功获取过数据，回来就不显示动态更新的提示
                    mRightMark.setVisibility(View.INVISIBLE);
                    mMark.setVisibility(View.INVISIBLE);
                    mScrolled = true;
                }
                break;
            case HomeConstant.REQUEST_CODE_MOMENT_DETAIL:
                if (resultCode == Activity.RESULT_OK){
                    if (mAdapter != null){
                        DynamicStatus status = (DynamicStatus) data.getSerializableExtra(HomeConstant.KEY_DATA_MOMENT_DETAIL);
                        mAdapter.update(status);
                    }
                }else if (resultCode == HomeConstant.RESULT_MOMENT_DETAIL_OPERATED){

                    String action = data.getAction();

                    if (!TextUtils.isEmpty(action) && action.equals(HomeConstant.ACTION_RESULT_DEL)) {

                        int removeId = data.getIntExtra(HomeConstant.KEY_ITEM_POSITION,-1);
                        if (removeId >=0){
                            if (mAdapter != null){
                                mAdapter.getList().remove(removeId);
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                    }else{
                        if (mAdapter != null){
                            mAdapter.doRefresh();
                        }
                    }

                }
                break;
            case BaseConstant.REQUEST_CODE_SEND_MOMENT:
                if (resultCode == Activity.RESULT_OK){
                    if (mAdapter != null){
                        mAdapter.doRefresh();
                    }
                }
                break;
        }
    }

    public void toRefresh() {
        if (mAdapter != null){
            mAdapter.doRefresh();
        }
    }

    private class UpdateFollowReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(ACTION_UPDATE_FOWLLED)) {
                String account = intent.getStringExtra(EXTRA_FOWLLED_ID);
                mAdapter.updateFollow(account);
            }else if (action.equals(ACTION_UPDATE_UN_FOWLLED)) {
                String account = intent.getStringExtra(EXTRA_FOWLLED_ID);
                mAdapter.updateUnFollow(account);
            }
        }

    }

}
