package cn.xiaojs.xma.common.pageload.stateview;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshRecyclerView;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;

/**
 * Created by Paul Z on 2017/5/15.
 */

public class AppLoadState2 implements LoadStateListener{
    private View mEmptyView;
    private View mEmptyLayout;

    private ImageView mEmptyImage;
    private TextView mEmptyDesc;
    private TextView mEmptyDesc1;
    private Button mEmptyButton;


    private int mCurrentState=STATE_NORMAL;

    private String emptyDesc;
    private String emptyDesc1;
    private String emptyBtnDesc;

    private String failedDesc;
    private String failedDesc1;
    private String failedBtnDesc;

    private int emptyImageRes;
    private int failedImageRes;

    ViewGroup parent;

    public AppLoadState2(Context context, ViewGroup parent){
        this.parent=parent;
        mEmptyView = LayoutInflater.from(context).inflate(R.layout.layout_list_empty, null);
        mEmptyLayout = mEmptyView.findViewById(R.id.empty_layout);
        mEmptyDesc = (TextView) mEmptyView.findViewById(R.id.empty_desc);
        mEmptyDesc1 = (TextView) mEmptyView.findViewById(R.id.empty_desc1);
        mEmptyButton = (Button) mEmptyView.findViewById(R.id.empty_click);
        mEmptyImage = (ImageView) mEmptyView.findViewById(R.id.empty_image);
    }

    public void setTips(String... tips){
        if(tips.length!=6){
            throw new RuntimeException(
                    "only 6 tips size,there are emptyDesc,emptyDesc1,emptyBtnDesc,failedDesc,failedDesc1 and failedBtnDesc");
        }
        emptyDesc=tips[0];
        emptyDesc1=tips[1];
        emptyBtnDesc=tips[2];
        failedDesc=tips[3];
        failedDesc1=tips[4];
        failedBtnDesc=tips[5];
    }

    public void setEmptyImageRes(@DrawableRes int emptyImageRes){
        this.emptyImageRes=emptyImageRes;
    }

    public void setEmptyImageAndTip(@DrawableRes int emptyImageRes,String text){
        setEmptyImageRes(emptyImageRes);
        emptyDesc=text;
    }

    public void setFailedImageRes(@DrawableRes int failedImageRes){
        this.failedImageRes=failedImageRes;
    }

    public void setFailedImageAndTip(@DrawableRes int failedImageRes,String text){
        setFailedImageRes(failedImageRes);
        failedDesc=text;
    }


    @Override
    public void onSuccess(String msg) {
        detach();
    }

    @Override
    public void onFailed(String msg) {
        setTexts(failedDesc,failedDesc1,failedBtnDesc);
        if(failedImageRes>0){
            mEmptyImage.setImageResource(failedImageRes);
        }else {
            mEmptyImage.setImageResource(R.drawable.ic_data_failed);
        }
        mEmptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        attach();
    }

    @Override
    public void onNoData(String msg) {
        setTexts(emptyDesc,emptyDesc1,emptyBtnDesc);
        mCurrentState = STATE_NORMAL;
        if(emptyImageRes>0){
            mEmptyImage.setImageResource(emptyImageRes);
        }else {
            mEmptyImage.setImageResource(R.drawable.ic_data_empty);
        }
        mEmptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCallback!=null){
                    mCallback.onEvent(EventCallback.EVENT_1);
                }
            }
        });
        attach();
    }

    @Override
    public void onNoNetwork(String msg) {
        //暂未实现
    }

    @Override
    public void onLoading(String msg) {
        detach();

    }

    //text=null,不显示tagview
    private void setText(TextView tagView , String text){
        if(text!=null){
            tagView.setVisibility(View.VISIBLE);
            tagView.setText(text);
        }else {
            tagView.setVisibility(View.GONE);
        }
    }

    //text=null,不显示
    private void setTexts(String... texts){
        int i=0;
        if(texts.length>0)
            setText(mEmptyDesc,texts[i]);
        if(texts.length>++i)
            setText(mEmptyDesc1,texts[i]);
        if(texts.length>++i)
            setText(mEmptyButton,texts[i]);
    }

    private void attach(){
        if(mEmptyView.getParent()==null) {
            parent.addView(mEmptyView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }
    private void detach(){
        if(mEmptyView.getParent()!=null){
            parent.removeView(mEmptyView);
        }
    }

    @Override
    public View getView() {
        return mEmptyView;
    }

    private EventCallback mCallback;

    public void setEventCallback(EventCallback callback){
        mCallback=callback;
    }
}
