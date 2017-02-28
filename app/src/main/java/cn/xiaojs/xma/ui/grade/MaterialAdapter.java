package cn.xiaojs.xma.ui.grade;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2017/1/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.colla.LibDoc;
import cn.xiaojs.xma.model.colla.UserDoc;

public class MaterialAdapter extends AbsSwipeAdapter<LibDoc, MaterialAdapter.Holder> {

    private boolean mIsMine;
    private String mOwner;

    public MaterialAdapter(Context context, PullToRefreshSwipeListView listView,String owner) {
        super(context, listView);
        if (TextUtils.isEmpty(owner)){
            mIsMine = true;
        }
        mOwner = owner;
    }

    @Override
    protected void setViewContent(final Holder holder, LibDoc bean, int position) {
        holder.showOpera(false);
        if (mIsMine){
            holder.opera2.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.share_selector,0,0);
            holder.opera2.setText(R.string.share);
        }
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showOpera(holder.opera.getVisibility() != View.VISIBLE);
            }
        });

//        if (FileUtil.DOC == FileUtil.getFileType(bean.)) {
//            holder.image.setImageResource(R.drawable.ic_word);
//        } else if (position % 6 == 1) {
//            holder.image.setImageResource(R.drawable.ic_ppt);
//        } else if (position % 6 == 2) {
//            holder.image.setImageResource(R.drawable.ic_excel);
//        } else if (position % 6 == 3) {
//            holder.image.setImageResource(R.drawable.ic_picture);
//        } else if (position % 6 == 4) {
//            holder.image.setImageResource(R.drawable.ic_pdf);
//        } else if (position % 6 == 5) {
//            holder.image.setImageResource(R.drawable.ic_unknown);
//        }
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        CollaManager.getDocuments(mContext, mOwner, mPagination, new APIServiceCallback<UserDoc>() {
            @Override
            public void onSuccess(UserDoc object) {
                if (object != null){
                    MaterialAdapter.this.onSuccess(object.documents);
                }else {
                    MaterialAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                MaterialAdapter.this.onFailure(errorCode,errorMessage);
            }
        });
    }

    class Holder extends BaseHolder {
        @BindView(R.id.material_item_image)
        ImageView image;
        @BindView(R.id.material_item_name)
        TextView name;
        @BindView(R.id.material_item_desc)
        TextView desc;
        @BindView(R.id.material_item_expand)
        ImageView expand;

        @BindView(R.id.material_item_opera_wrapper)
        LinearLayout opera;
        @BindView(R.id.material_item_opera1)
        TextView opera1;
        @BindView(R.id.material_item_opera2)
        TextView opera2;

        @BindView(R.id.material_item)
        View item;

        public void showOpera(boolean b) {
            if (b) {
                opera.setVisibility(View.VISIBLE);
                expand.setImageResource(R.drawable.ic_arrow_up);
            } else {
                opera.setVisibility(View.GONE);
                expand.setImageResource(R.drawable.ic_arrow_down);
            }
        }

        public Holder(View view) {
            super(view);
        }
    }
}
