package cn.xiaojs.xma.ui.share;

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
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/11/13.
 */
public class Template1BgPickBoard extends Dialog implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.cancel)
    ImageView cancel;
    private View mContentView;// dialog content view



    ItemAdapter mAdapter;

    public final static int[] templateThumbsRes={R.drawable.bg_share_class_1_thumb,R.drawable.bg_share_class_2_thumb,R.drawable.bg_share_class_3_thumb,R.drawable.bg_share_class_4_thumb};


    private OnDialogCloseListener mOnDismissListener;


    public Template1BgPickBoard(Context context) {
        super(context, R.style.CommonDialog);
        init();
    }

    public Template1BgPickBoard(Context context, int themeResId) {
        super(context, R.style.CommonDialog);
        init();
    }

    protected Template1BgPickBoard(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(true);
        final Window dialogWindow = getWindow();
        setContentView(R.layout.dialog_template_pick_board);
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
        mAdapter =new ItemAdapter();
        recyclerview.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));


        recyclerview.setAdapter(mAdapter);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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



        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ItemHolder holder=new ItemHolder(View.inflate(parent.getContext(), R.layout.item_template_pick_board, null));
            holder.name.setVisibility(View.GONE);
            return holder;
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, final int position) {
            holder.icon.setImageResource(templateThumbsRes[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnTemplateBgChangeListener!=null){
                        mOnTemplateBgChangeListener.onChange(position);
                    }
                }
            });
            holder.vSelector.setSelected(position==Template1BgPickBoard.this.position);
        }

        @Override
        public int getItemCount() {
            return templateThumbsRes.length;
        }
    }


    public static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView icon;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.v_selector)
        View vSelector;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(ItemHolder.this,itemView);
        }
    }


    int position=-1;
    public Template1BgPickBoard setPosition(int position){
        this.position=position;
        return this;
    }


    OnTemplateBgChangeListener mOnTemplateBgChangeListener;

    public void setOnTemplateBgChangeListener(OnTemplateBgChangeListener onTemplateBgChangeListener) {
        this.mOnTemplateBgChangeListener = onTemplateBgChangeListener;
    }

    public interface OnTemplateBgChangeListener{
        void onChange(int position);
    }

}
