package cn.xiaojs.xma.ui.classroom2.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.widget.LoadingView;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public abstract class BottomSheetFragment extends BottomSheetDialogFragment {

    @Nullable
    @BindView(R.id.tips_layout)
    RelativeLayout tipsLayout;
    @Nullable
    @BindView(R.id.loading_progress)
    LoadingView loadingView;
    @Nullable
    @BindView(R.id.loading_desc)
    TextView loadingDescView;
    @Nullable
    @BindView(R.id.tips_icon)
    TextView tipsIconView;

    @Nullable
    @BindView(R.id.root_lay)
    public View rootLayout;


    protected ClassroomEngine classroomEngine;


    public abstract View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState);


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        classroomEngine = ClassroomEngine.getEngine();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View view = createView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);

        if (rootLayout !=null) {
            rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        if (getDialog() == null)
                            return;

                        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();

                        FrameLayout bottomSheet = (FrameLayout) dialog.findViewById(
                                android.support.design.R.id.design_bottom_sheet);
                        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                        behavior.setHideable(true);
                        bottomSheet.setBackgroundColor(Color.TRANSPARENT);

                        behavior.setPeekHeight(view.getMeasuredHeight());

                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                });
        return view;
    }

    //    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//
//        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
//        final View view = View.inflate(getContext(), R.layout.fragment_classroom2_bottomsheet, null);
//        dialog.setContentView(view);
//        bgView = (FrameLayout) view.findViewById(R.id.bg);
//        bgView.addView(createView());
//        ButterKnife.bind(this, view);
//
//        behavior = BottomSheetBehavior.from((View) view.getParent());
//        behavior.setHideable(true);
//        ((View) view.getParent()).setBackgroundColor(Color.TRANSPARENT);
//
//        bgView.post(new Runnable() {
//            @Override
//            public void run() {
//                behavior.setPeekHeight(view.getMeasuredHeight());
//            }
//        });
//
//        return dialog;
//    }
//
//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//
//    }

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


    protected void showLoadingStatus() {
        if (tipsLayout != null) {
            tipsLayout.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.VISIBLE);
            loadingView.resume();

            loadingDescView.setVisibility(View.VISIBLE);
            tipsIconView.setVisibility(View.GONE);

        }
    }

    protected void showFinalTips(@DrawableRes int tipsIcon, @StringRes int tipsStr) {
        if (tipsLayout != null) {
            tipsLayout.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            loadingView.pause();
            loadingDescView.setVisibility(View.GONE);
            tipsIconView.setVisibility(View.VISIBLE);
            tipsIconView.setCompoundDrawablesWithIntrinsicBounds(0, tipsIcon, 0, 0);
            tipsIconView.setText(tipsStr);
        }
    }

    protected void showFinalTips() {
        if (tipsLayout != null) {
            tipsLayout.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);
            loadingView.pause();
            loadingDescView.setVisibility(View.GONE);
            tipsIconView.setVisibility(View.VISIBLE);
        }
    }

    protected void hiddenTips() {
        if (tipsLayout != null) {
            tipsLayout.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            loadingView.pause();
            loadingDescView.setVisibility(View.GONE);
            tipsIconView.setVisibility(View.GONE);
        }
    }

}
