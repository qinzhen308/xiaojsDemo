package cn.xiaojs.xma.ui.classroom2.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.ClosableAdapterSlidingLayout;
import cn.xiaojs.xma.ui.widget.ClosableSlidingLayout;

/**
 * Created by maxiaobao on 2017/10/15.
 */

public abstract class RightSheetFragment extends DialogFragment
        implements ClosableSlidingLayout.SlideListener{

    private int viewWidth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext(), R.style.CommonDialog);
        dialog.setCanceledOnTouchOutside(true);

        View view = getContentView();
        //forbid closeable by sliding
        //view.setEnabled(false);
        Window dialogWindow = dialog.getWindow();
        //hideSystemUI(dialog);

        //set attributes
        dialogWindow.setContentView(view);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setWindowAnimations(R.style.RightSheetAnim);
        dialogWindow.setGravity(Gravity.RIGHT);
        dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        //set params
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0;
        dialogWindow.setAttributes(params);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                //onFragmentShow(dialogInterface);
            }
        });


        ButterKnife.bind(this, dialog);
        onViewCreated(view);

        return dialog;
    }

    @Override
    public void onClosed() {
        dismiss();
    }

    @Override
    public void onOpened() {

    }

    private View getContentView() {
        View view = onCreateView();
        if (view instanceof ClosableSlidingLayout) {
            View contactView = ((ClosableSlidingLayout) view).getChildAt(0);

            ViewGroup.LayoutParams params = contactView.getLayoutParams();
            int paramsW = viewWidth > 0 ? viewWidth : ViewGroup.LayoutParams.WRAP_CONTENT;
            if (params == null) {
                params = new ViewGroup.LayoutParams(paramsW, ViewGroup.LayoutParams.MATCH_PARENT);
                contactView.setLayoutParams(params);
            } else {
                params.width = paramsW;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            ((ClosableSlidingLayout) view).setSlideOrientation(ClosableSlidingLayout.SLIDE_FROM_LEFT_TO_RIGHT);
            if (view instanceof ClosableAdapterSlidingLayout) {
                setSlideConflictView((ClosableAdapterSlidingLayout) view);
            }


            ((ClosableSlidingLayout) view).setSlideListener(this);
            View targetView = getTargetView(view);
            if (targetView != null) {
                ((ClosableSlidingLayout) view).setTarget(targetView);
            }
        }
        return view;
    }

    protected abstract View onCreateView();
    protected abstract View getTargetView(View root);
    protected void setSlideConflictView(ClosableAdapterSlidingLayout horizontalSlidingLayout) {

    }

    protected void onViewCreated(View view) {

    }

    protected abstract void hidden();

}
