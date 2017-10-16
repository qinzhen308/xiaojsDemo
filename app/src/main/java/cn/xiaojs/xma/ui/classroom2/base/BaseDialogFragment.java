package cn.xiaojs.xma.ui.classroom2.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;

/**
 * Created by maxiaobao on 2017/10/15.
 */

public class BaseDialogFragment extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Window window = getDialog().getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        window.setLayout(-1, WindowManager.LayoutParams.MATCH_PARENT);
    }

    /**
     * 显示loading状态
     */
    public void showProgress(boolean cancelable) {
        ((Classroom2Activity) getActivity()).showProgress(cancelable);
    }

    /**
     * 退出loading状态
     */
    public void cancelProgress() {
        ((Classroom2Activity) getActivity()).cancelProgress();
    }
}
