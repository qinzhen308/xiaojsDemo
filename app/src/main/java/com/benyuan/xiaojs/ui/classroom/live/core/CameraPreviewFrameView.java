package com.benyuan.xiaojs.ui.classroom.live.core;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by jerikc on 16/2/5.
 */
public class CameraPreviewFrameView extends GLSurfaceView {
    private static final String TAG = "CameraPreviewFrameView";

    public interface Listener {
        boolean onSingleTapUp(MotionEvent e);
    }

    private Listener mListener;
    private GestureDetector mGestureDetector;

    public CameraPreviewFrameView(Context context) {
        super(context);
        initialize(context);
    }

    public CameraPreviewFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return false;
    }

    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (mListener != null) {
                mListener.onSingleTapUp(e);
            }
            return false;
        }
    };

    private void initialize(Context context) {
        Log.i(TAG, "initialize");
        mGestureDetector = new GestureDetector(context, mGestureListener);
    }
}
