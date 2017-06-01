package cn.xiaojs.xma.common.pageload.stateview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;

/**
 * Created by Paul Z on 2017/5/15.
 */

public class AppLoadState implements LoadStateListener{
    private View mEmptyView;
    private View mEmptyLayout;

    private ImageView mEmptyImage;
    private TextView mEmptyDesc;
    private TextView mEmptyDesc1;
    private Button mEmptyButton;

    private PullToRefreshSwipeListView mListView;

    private int mCurrentState=STATE_NORMAL;

    private String emptyDesc;
    private String emptyDesc1;
    private String emptyBtnDesc;

    private String failedDesc;
    private String failedDesc1;
    private String failedBtnDesc;


    public AppLoadState(Context context, PullToRefreshSwipeListView listView){
        mListView=listView;
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

    @Override
    public void onSuccess(String msg) {
        detach();
    }

    @Override
    public void onFailed(String msg) {
        mListView.onRefreshComplete();
        setTexts(failedDesc,failedDesc1,failedBtnDesc);
        mEmptyImage.setImageResource(R.drawable.ic_data_failed);
        mEmptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setRefreshing();
            }
        });
        attach();
    }

    @Override
    public void onNoData(String msg) {
        mListView.onRefreshComplete();
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        setTexts(emptyDesc,emptyDesc1,emptyBtnDesc);
        mCurrentState = STATE_NORMAL;
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
        if (PullToRefreshBase.Mode.PULL_FROM_START == mListView.getMode() || PullToRefreshBase.Mode.BOTH == mListView.getMode()) {
            mListView.setFirstLoading(true);
            mListView.setRefreshing();
        } else {
            mListView.setFirstLoading(true);
            mListView.setRefreshing(true);
        }
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
        mListView.setEmptyView(mEmptyView);
    }
    private void detach(){
        mListView.removeEmptyView(mEmptyView);
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
