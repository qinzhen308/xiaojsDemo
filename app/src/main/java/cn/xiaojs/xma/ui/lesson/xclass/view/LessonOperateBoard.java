package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaola.qrcodescanner.qrcode.utils.ScreenUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.AbsOpModel;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/25.
 */
public class LessonOperateBoard<T> extends Dialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    @BindView(R.id.recyclerview1)
    RecyclerView recyclerview1;
    @BindView(R.id.recyclerview2)
    RecyclerView recyclerview2;
    @BindView(R.id.cancel)
    Button cancel;
    private View mContentView;// dialog content view

    private List<AbsOpModel<T>> opGroup1 =new ArrayList<>();

    private List<AbsOpModel<T>> opGroup2 =new ArrayList<>();

    ItemAdapter mAdapter1;
    ItemAdapter mAdapter2;


    //请一定按顺序，并且和LOpModel里面的OP_..顺序保持一致
    private final static int[] icons={
            R.drawable.ic_op_apply,R.drawable.ic_op_cancel_lesson,
            R.drawable.ic_op_cancel_submit,R.drawable.ic_op_class_info,
            R.drawable.ic_op_database2,R.drawable.ic_op_delete,
            R.drawable.ic_op_edit,R.drawable.ic_op_enter,
            R.drawable.ic_op_enter_2,R.drawable.ic_op_look,
            R.drawable.ic_op_modify_time,R.drawable.ic_op_private,
            R.drawable.ic_op_public,R.drawable.ic_op_publish,
            R.drawable.ic_op_recreate_lesson,R.drawable.ic_op_schedule,
            R.drawable.ic_op_share,R.drawable.ic_op_signup,
            R.drawable.ic_op_submit,R.drawable.ic_op_cancel_check,
            R.drawable.ic_op_share2,R.drawable.ic_op_database1,
            R.drawable.ic_op_agree_invite,R.drawable.ic_op_disagree_invite,
            R.drawable.ic_op_reject_reason,R.drawable.ic_op_recreate_lesson,
            R.drawable.ic_op_apply_students_list,R.drawable.ic_op_abort_recorded_lesson
    };

    //请一定按顺序，并且和LOpModel里面的OP_..顺序保持一致
    private final static int[] names={
            R.string.lesson_op_look_apply,R.string.lesson_op_cancel_lesson,
            R.string.lesson_op_cancel_submit,R.string.lesson_op_class_info,
            R.string.lesson_op_database,R.string.lesson_op_delete,
            R.string.lesson_op_edit,R.string.lesson_op_enter,
            R.string.lesson_op_enter,R.string.lesson_op_look,
            R.string.lesson_op_modify_time,R.string.lesson_op_private,
            R.string.lesson_op_public,R.string.lesson_op_publish,
            R.string.lesson_op_create_lesson_again,R.string.lesson_op_schedule,
            R.string.lesson_op_share,R.string.lesson_op_signup,
            R.string.lesson_op_submit,R.string.lesson_op_cancel_check,
            R.string.lesson_op_share,R.string.lesson_op_database,
            R.string.lesson_op_agree_invite,R.string.lesson_op_disagree_invite,
            R.string.lesson_op_reject_reason,R.string.lesson_op_recreate_lesson,
            R.string.lesson_op_apply_students_list,R.string.lesson_op_abort_recorded_lesson
    };

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
        initItemWidth();
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
        mAdapter1=new ItemAdapter();
        mAdapter2=new ItemAdapter();
        recyclerview1.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));
        recyclerview2.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));


        mAdapter1.setList(opGroup1);
        mAdapter2.setList(opGroup2);
        recyclerview1.setAdapter(mAdapter1);
        recyclerview2.setAdapter(mAdapter2);
        recyclerview1.setVisibility(View.GONE);
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

    public LessonOperateBoard setOpGroup1(List<AbsOpModel<T>> ops) {
        this.opGroup1 = ops;
        mAdapter1.setList(ops);
        mAdapter1.notifyDataSetChanged();
        if(ArrayUtil.isEmpty(ops)){
            recyclerview1.setVisibility(View.GONE);
        }else {
            recyclerview1.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public LessonOperateBoard setOpGroup2(List<AbsOpModel<T>> ops) {
        this.opGroup2 = ops;
        mAdapter2.setList(ops);
        mAdapter2.notifyDataSetChanged();
        if(ArrayUtil.isEmpty(ops)){
            recyclerview2.setVisibility(View.GONE);
        }else {
            recyclerview2.setVisibility(View.VISIBLE);
        }
        return this;
    }


    public void setOnDismissListener(OnDialogCloseListener listener) {
        mOnDismissListener = listener;
    }

    @OnClick({R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                if(isShowing())dismiss();
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

    class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {
        private List<AbsOpModel<T>> list;

        private void setList(List<AbsOpModel<T>> list){
            this.list=list;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemHolder holder=new ItemHolder(View.inflate(parent.getContext(), R.layout.item_lesson_operate, null));
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(itemWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            return holder;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position) {
            holder.name.setText(names[list.get(position).getId()]);
            holder.icon.setImageResource(icons[list.get(position).getId()]);
            holder.btnItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.get(position).onClick(activity,data,LessonOperateBoard.this.position);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list ==null?0: list.size();
        }
    }

    int itemWidth=0;
    private void initItemWidth(){
        itemWidth=(ScreenUtils.getScreenWidth(getContext())-getContext().getResources().getDimensionPixelSize(R.dimen.px100))/4;
        Logger.d("----qz----initItemWidth="+itemWidth);
    }

    public static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.btn_item)
        View btnItem;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(ItemHolder.this,itemView);
        }
    }

    public void show(List<AbsOpModel<T>> ops){
        this.opGroup2 =ops;
        mAdapter2.setList(ops);
        mAdapter2.notifyDataSetChanged();
        if(isShowing()){

        }else {
            show();
        }
    }

    T data;
    public LessonOperateBoard setData(T data){
        this.data=data;
        return this;
    }
    Activity activity;
    public LessonOperateBoard setActivity(Activity activity){
        this.activity=activity;
        return this;
    }

    int position=-1;
    public LessonOperateBoard setPosition(int position){
        this.position=position;
        return this;
    }

    public LessonOperateBoard maybe(Activity activity,T data,int position){
        return setActivity(activity).setData(data).setPosition(position);
    }

    @Override
    public void dismiss() {
        this.activity=null;
        this.data=null;
        super.dismiss();
    }

}
