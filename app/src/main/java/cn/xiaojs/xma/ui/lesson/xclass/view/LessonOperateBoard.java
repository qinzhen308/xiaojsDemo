package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/25.
 */
public class LessonOperateBoard extends Dialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    @BindView(R.id.icon1)
    ImageView icon1;
    @BindView(R.id.name1)
    TextView name1;
    @BindView(R.id.group1_item1)
    LinearLayout group1Item1;
    @BindView(R.id.icon2)
    ImageView icon2;
    @BindView(R.id.name2)
    TextView name2;
    @BindView(R.id.group1_item2)
    LinearLayout group1Item2;
    @BindView(R.id.icon3)
    ImageView icon3;
    @BindView(R.id.name3)
    TextView name3;
    @BindView(R.id.group1_item3)
    LinearLayout group1Item3;
    @BindView(R.id.group1)
    LinearLayout group1;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.cancel)
    Button cancel;
    private View mContentView;// dialog content view

    private OnDialogCloseListener mOnDismissListener;

    public LessonOperateBoard(Context context) {
        super(context, R.style.CommonDialog);
        init();
    }

    public LessonOperateBoard(Context context, int themeResId) {
        super(context, R.style.CommonDialog);
        init();
    }

    protected LessonOperateBoard(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        setContentView(R.layout.dialog_lesson_operate);
        ButterKnife.bind(this);
        dialogWindow.setWindowAnimations(R.style.BottomSheetAnim);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);
        setOnCancelListener(this);
        setOnDismissListener(this);
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        if (mOnDismissListener != null) {
            mOnDismissListener.onCancel();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mOnDismissListener != null) {
            mOnDismissListener.onDismiss();
        }
    }

    public void setList(){

    }


    public void setOnDismissListener(OnDialogCloseListener listener) {
        mOnDismissListener = listener;
    }

    @OnClick({R.id.group1_item1, R.id.group1_item2, R.id.group1_item3,R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.group1_item1:
                break;
            case R.id.group1_item2:
                break;
            case R.id.group1_item3:
                break;
            case R.id.cancel:
                break;
        }
    }

    public interface OnDialogCloseListener {
        void onCancel();

        void onDismiss();
    }

    public interface OnItemClick {
        void onItemClick(Object item);
    }
}
