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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/5/25.
 */
public class LessonOperateBoard extends Dialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    @BindView(R.id.recyclerview1)
    RecyclerView recyclerview1;
    @BindView(R.id.recyclerview2)
    RecyclerView recyclerview2;
    @BindView(R.id.cancel)
    Button cancel;
    private View mContentView;// dialog content view

    private List<LOpModel> opGroup1 =new ArrayList<>();

    private List<LOpModel> opGroup2 =new ArrayList<>();

    ItemAdapter mAdapter1;
    ItemAdapter mAdapter2;


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
            R.drawable.ic_op_share2,R.drawable.ic_op_database1
    };


    private final static int[] names={
            R.string.lesson_op_look_apply,R.string.lesson_op_cancel_lesson,
            R.string.lesson_op_cancel_submit,R.string.lesson_op_class_info,
            R.string.lesson_op_database,R.string.lesson_op_delete,
            R.string.lesson_op_edit,R.string.lesson_op_enter,
            R.string.lesson_op_enter,R.string.lesson_op_look,
            R.string.lesson_op_modify_time,R.string.lesson_op_private,
            R.string.lesson_op_public,R.string.lesson_op_publish,
            R.string.lesson_op_recreate_lesson,R.string.lesson_op_schedule,
            R.string.lesson_op_share,R.string.lesson_op_signup,
            R.string.lesson_op_submit,R.string.lesson_op_cancel_check,
            R.string.lesson_op_share,R.string.lesson_op_database
    };

    private OnDialogCloseListener mOnDismissListener;

    public final static List<LOpModel> commonOps=new ArrayList<>(4);
    static {
        commonOps.add(new LOpModel(LOpModel.OP_APPLY));
        commonOps.add(new LOpModel(LOpModel.OP_DATABASE2));
        commonOps.add(new LOpModel(LOpModel.OP_ENTER_2));
        commonOps.add(new LOpModel(LOpModel.OP_SHARE2));
    }

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



    public static List<LOpModel> getCommonOps(){
        return commonOps;
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

    public LessonOperateBoard setOpGroup1(List<LOpModel> ops) {
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

    public LessonOperateBoard setOpGroup2(List<LOpModel> ops) {
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
        private List<LOpModel> list;

        private void setList(List<LOpModel> list){
            this.list=list;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemHolder holder=new ItemHolder(View.inflate(parent.getContext(), R.layout.item_lesson_operate, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position) {
            holder.name.setText(names[list.get(position).getId()]);
            holder.icon.setImageResource(icons[list.get(position).getId()]);
            holder.btnItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.get(position).onClick(activity,data);
                    dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return list ==null?0: list.size();
        }
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

    public void show(List<LOpModel> ops){
        this.opGroup2 =ops;
        mAdapter2.setList(ops);
        mAdapter2.notifyDataSetChanged();
        if(isShowing()){

        }else {
            show();
        }
    }

    Object data;
    public LessonOperateBoard setData(Object data){
        this.data=data;
        return this;
    }
    Activity activity;
    public LessonOperateBoard setActivity(Activity activity){
        this.activity=activity;
        return this;
    }

    public LessonOperateBoard maybe(Activity activity,Object data){
        return setActivity(activity).setData(data);
    }

    @Override
    public void dismiss() {
        this.activity=null;
        this.data=null;
        super.dismiss();
    }

}
