package cn.xiaojs.xma.ui.classroom2.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.LoadingView;

/**
 * Created by maxiaobao on 2017/10/19.
 */

public abstract class BaseLiveView extends FrameLayout{

    @BindView(R.id.loading_layout)
    protected RelativeLayout loadingLayout;
    @BindView(R.id.loading_progress)
    protected LoadingView loadingView;
    @BindView(R.id.loading_desc)
    protected TextView loadingDescView;
    @BindView(R.id.overlay_mask)
    protected View overlayMaskView;
    @BindView(R.id.close_video)
    protected ImageView closeView;

    private boolean closeEnable;



    public BaseLiveView(Context context) {
        super(context);
        init();
    }

    public BaseLiveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseLiveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setKeepScreenOn(true);
        View view = LayoutInflater.from(getContext()).
                inflate(R.layout.layout_classroom2_base_live_view, this, true);

        ButterKnife.bind(this);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(createLiveView(), 0, params);
    }


    @OnClick({R.id.close_video, R.id.overlay_mask})
    void onViewClick(View view){
        switch(view.getId()) {
            case R.id.close_video:
                onCloseClick(view);
                break;
            case R.id.overlay_mask:
                int vis = closeView.getVisibility() == VISIBLE? GONE : VISIBLE;
                closeView.setVisibility(vis);
                break;
        }
    }


    public abstract View createLiveView();


    protected void onCloseClick(View view) {
        if (!closeEnable) {
            overlayMaskView.setVisibility(GONE);
        }

        closeView.setVisibility(GONE);
        setVisibility(GONE);
    }

    public void setCloseEnabled(boolean enabled) {

        closeEnable = enabled;

        int vis = closeEnable? VISIBLE : GONE;
        overlayMaskView.setVisibility(vis);

    }

    public void showLoading(boolean show) {
        if (show) {
            loadingLayout.setVisibility(VISIBLE);
        } else {
            loadingLayout.setVisibility(GONE);
        }
    }

//    public void showLoading(boolean b, int loadingSize, int txtSize) {
//        if (b) {
//            mLoadingView.setSize(loadingSize);
//            mLadingDesc.getPaint().setTextSize(txtSize);
//            mLoadingLayout.setVisibility(VISIBLE);
//        } else {
//            mLoadingLayout.setVisibility(GONE);
//        }
//    }

    public void setLoadingDesc(@StringRes int str) {
        if (loadingDescView != null) {
            loadingDescView.setText(str);
        }

    }

    public void setLoadingDesc(String str) {
        if (loadingDescView != null) {
            loadingDescView.setText(str);
        }

    }
}
