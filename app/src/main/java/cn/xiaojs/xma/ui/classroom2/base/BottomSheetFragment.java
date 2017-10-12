package cn.xiaojs.xma.ui.classroom2.base;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import butterknife.ButterKnife;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public abstract class BottomSheetFragment extends BottomSheetDialogFragment {

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

        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

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


}
