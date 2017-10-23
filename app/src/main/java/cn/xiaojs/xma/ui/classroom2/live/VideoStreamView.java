package cn.xiaojs.xma.ui.classroom2.live;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import cn.xiaojs.xma.ui.classroom2.base.BaseLiveView;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;

/**
 * Created by maxiaobao on 2017/10/19.
 */

public class VideoStreamView extends BaseLiveView{


    public interface ControlListener{
        void onPlayClosed();
    }


    private CameraPreviewFrameView cameraPreviewView;

    private ControlListener controlListener;

    public VideoStreamView(Context context) {
        super(context);
    }

    public VideoStreamView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoStreamView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View createLiveView() {
        cameraPreviewView = new CameraPreviewFrameView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        cameraPreviewView.setLayoutParams(layoutParams);
        return cameraPreviewView;
    }

    @Override
    protected void onCloseClick(View view) {
        super.onCloseClick(view);
        if (controlListener != null) {
            controlListener.onPlayClosed();
        }
    }

    public CameraPreviewFrameView getCameraPreviewView() {
        return cameraPreviewView;
    }

    public void setControlListener(ControlListener listener) {
        this.controlListener = listener;
    }
}
