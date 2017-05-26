package cn.xiaojs.xma.ui.lesson.xclass.view;

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
import android.widget.LinearLayout;
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

    private List<LOpModel> opGroup1 =new ArrayList<>();

    private List<LOpModel> opGroup2 =new ArrayList<>();

    ItemAdapter mAdapter;


    private final static int[] icons={
            R.drawable.ic_op_apply,R.drawable.ic_op_cancel_lesson,
            R.drawable.ic_op_cancel_submit,R.drawable.ic_op_class_info,
            R.drawable.ic_op_database,R.drawable.ic_op_delete,
            R.drawable.ic_op_edit,R.drawable.ic_op_enter,
            R.drawable.ic_op_enter_2,R.drawable.ic_op_look,
            R.drawable.ic_op_modify_time,R.drawable.ic_op_private,
            R.drawable.ic_op_public,R.drawable.ic_op_publish,
            R.drawable.ic_op_recreate_lesson,R.drawable.ic_op_schedule,
            R.drawable.ic_op_share,R.drawable.ic_op_signup,
            R.drawable.ic_op_submit
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
            R.string.lesson_op_submit
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
        mAdapter=new ItemAdapter();
        recyclerview.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));

        for(int i=0;i<icons.length;i++){
            if(i==0||i==4||i==8){
                continue;
            }
            opGroup2.add(new LOpModel(i));
        }
        recyclerview.setAdapter(mAdapter);

        opGroup1.add(new LOpModel(0));
        opGroup1.add(new LOpModel(4));
        opGroup1.add(new LOpModel(8));
        setOpGroup1(opGroup1);
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
        if(ArrayUtil.isEmpty(opGroup2)){
            group1.setVisibility(View.GONE);
        }else {
            group1.setVisibility(View.VISIBLE);
        }
        icon1.setImageResource(icons[opGroup2.get(0).getId()]);
        name1.setText(names[opGroup2.get(0).getId()]);
        icon2.setImageResource(icons[opGroup2.get(1).getId()]);
        name2.setText(names[opGroup2.get(1).getId()]);
        icon3.setImageResource(icons[opGroup2.get(2).getId()]);
        name3.setText(names[opGroup2.get(2).getId()]);
        return this;
    }

    public LessonOperateBoard setOpGroup2(List<LOpModel> ops) {
        this.opGroup2 = ops;
        mAdapter.notifyDataSetChanged();
        return this;
    }


    public void setOnDismissListener(OnDialogCloseListener listener) {
        mOnDismissListener = listener;
    }

    @OnClick({R.id.group1_item1, R.id.group1_item2, R.id.group1_item3, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.group1_item1:
                break;
            case R.id.group1_item2:
                break;
            case R.id.group1_item3:
                break;
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

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemHolder holder=new ItemHolder(View.inflate(parent.getContext(), R.layout.item_lesson_operate, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.name.setText(names[opGroup2.get(position).getId()]);
            holder.icon.setImageResource(icons[opGroup2.get(position).getId()]);
            holder.btnItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return opGroup2 ==null?0: opGroup2.size();
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
        mAdapter.notifyDataSetChanged();
        if(isShowing()){

        }else {
            show();
        }
    }


}
