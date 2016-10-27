package com.myhandmark.pulltorefresh.library;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * Created by chris on 16/9/19.
 */

public class RotateUtil {
    public static void initRotate(ImageView loadingView, Context context){
        LinearInterpolator lin = new LinearInterpolator();
        Animation ro = AnimationUtils.loadAnimation(context,R.anim.rotate);
        ro.setInterpolator(lin);
        loadingView.startAnimation(ro);
        loadingView.setVisibility(View.GONE);
    }
}
