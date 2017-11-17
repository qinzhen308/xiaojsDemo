package cn.xiaojs.xma.ui.classroom2.base;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.widget.LoadingView;

/**
 * Created by maxiaobao on 2017/10/19.
 */

public abstract class BaseLiveView extends FrameLayout{

    public interface ControlListener{
        void onLiveViewClosed(BaseLiveView liveView);
        void onLiveViewScaled(BaseLiveView liveView);
    }


    @BindView(R.id.loading_layout)
    protected RelativeLayout loadingLayout;
    @BindView(R.id.loading_progress)
    protected LoadingView loadingView;
    @BindView(R.id.loading_desc)
    protected TextView loadingDescView;
    @BindView(R.id.overlay_mask)
    protected View overlayMaskView;
    @BindView(R.id.overlay_root)
    protected LinearLayout overlayLayout;
    @BindView(R.id.close_video)
    protected ImageView closeView;

    private boolean controleEnable;
    private ControlListener controlListener;



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

    public void setControlListener(ControlListener listener) {
        this.controlListener = listener;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (canMove()) {
//
//            switch (event.getAction()) {
////                case MotionEvent.ACTION_DOWN:
////                    break;
//                case MotionEvent.ACTION_MOVE:
//                    if (XiaojsConfig.DEBUG) {
//                        Logger.d("ACTION_MOVE...............");
//                    }
//                    moveView(this, event.getRawX(), event.getRawY());
//                    return true;
////                case MotionEvent.ACTION_UP:
////                    break;
//            }
//
//
//        }
//        return super.onTouchEvent(event);
//    }

    @OnClick({R.id.close_video, R.id.scale_video, R.id.overlay_mask})
    void onViewClick(View view){
        switch(view.getId()) {
            case R.id.close_video:
                onCloseClick(view);
                break;
            case R.id.scale_video:
                onScaleClick(view);
                break;
            case R.id.overlay_mask:
                int vis = overlayLayout.getVisibility() == VISIBLE? GONE : VISIBLE;
                overlayLayout.setVisibility(vis);
                break;
        }
    }


    public abstract View createLiveView();
    public abstract boolean canMove();


    protected void onCloseClick(View view) {
        if (!controleEnable) {
            overlayMaskView.setVisibility(GONE);
        }

        overlayLayout.setVisibility(GONE);
        setVisibility(GONE);

        if (controlListener != null) {
            controlListener.onLiveViewClosed(this);
        }
    }

    protected void onScaleClick(View view) {
        if (!controleEnable) {
            overlayMaskView.setVisibility(GONE);
        }

        overlayLayout.setVisibility(GONE);

        if (controlListener != null) {
            controlListener.onLiveViewScaled(this);
        }
    }


    public void setControlEnabled(boolean enabled) {

        controleEnable = enabled;

        int vis = controleEnable? VISIBLE : GONE;
        overlayMaskView.setVisibility(vis);

    }

    public void setCloseViewVisibility(int visibility) {
        closeView.setVisibility(visibility);
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

    private void moveView(View view, float rawX, float rawY) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view
                .getLayoutParams();
        params.leftMargin = (int) rawX - getWidth() / 2;
        params.topMargin = (int) rawY - getHeight() / 2;
        view.setLayoutParams(params);
    }
}
