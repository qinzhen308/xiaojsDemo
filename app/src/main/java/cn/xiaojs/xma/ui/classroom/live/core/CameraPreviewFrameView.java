package cn.xiaojs.xma.ui.classroom.live.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by jerikc on 16/2/5.
 */
public class CameraPreviewFrameView extends GLSurfaceView {
    private static final String TAG = "CameraPreviewFrameView";


    public CameraPreviewFrameView(Context context) {
        super(context);
    }

    public CameraPreviewFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return false;
    }

}
