package cn.xiaojs.xma.ui.classroom.document;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.ClassroomDocument;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  --------------------------\-------------------------------------------------------------
 * Author:huangyong
 * Date:2016/12/29
 * Desc:
 *
 * ======================================================================================== */

public class DocumentAdapter extends AbsSwipeAdapter<ClassroomDocument, DocumentAdapter.Holder> implements View.OnClickListener{
    private int mDefaultBgColor;
    private int mHoverBgColor;

    public DocumentAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
        init();
    }

    public DocumentAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        super(context, listView, autoLoad);
        init();
    }

    private void init() {
        mDefaultBgColor = mContext.getResources().getColor(R.color.white);
        mHoverBgColor = mContext.getResources().getColor(R.color.main_blue);
    }

    @Override
    protected void setViewContent(Holder holder, ClassroomDocument bean, int position) {
        holder.fileName.setText(bean.getFileName());
        holder.length.setText(XjsUtils.getSizeFormatText(bean.getLength()));
        holder.modifyTime.setText(TimeUtil.formatDate(bean.getModify(), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
        holder.more.setTag(position);
        int p = position % 5;
        int drawId = 0;
        switch (p) {
            case 0:
                drawId = R.drawable.ic_cr_doc_folder;
                break;
            case 1:
                drawId = R.drawable.ic_cr_doc_ppt;
                break;
            case 2:
                drawId = R.drawable.ic_cr_doc_word;
                break;
            case 3:
                drawId = R.drawable.ic_cr_doc_pdf;
                break;
            case 4:
                drawId = R.drawable.ic_cr_doc_pic;
                break;
        }

        holder.fileName.setCompoundDrawablesWithIntrinsicBounds(drawId, 0, 0, 0);
        holder.action.setVisibility(bean.isShowAction() ? View.VISIBLE : View.GONE);
        //holder.info.setBackgroundColor(bean.isShowAction() ? mHoverBgColor : mDefaultBgColor);
    }

    @Override
    protected View createContentView(int position) {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_cr_document_item, null);
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);

        holder.info = view.findViewById(R.id.info);
        holder.fileName = (TextView) view.findViewById(R.id.file_name);
        holder.length = (TextView) view.findViewById(R.id.size);
        holder.modifyTime = (TextView) view.findViewById(R.id.modify_time);
        holder.more = (ImageView) view.findViewById(R.id.more);
        holder.more.setOnClickListener(this);

        holder.action = view.findViewById(R.id.action);
        holder.rename = (TextView) view.findViewById(R.id.rename);
        holder.delete = (TextView) view.findViewById(R.id.delete);

        return holder;
    }

    @Override
    protected void doRequest() {
        onSuccess(getTestData());
    }

    @Override
    public void onClick(View v) {
        List<ClassroomDocument> data = getList();
        int pos = (int)v.getTag();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                ClassroomDocument clsDoc = data.get(i);
                if (i == pos) {
                    clsDoc.setShowAction(clsDoc.isShowAction() ? false : true);
                } else {
                    clsDoc.setShowAction(false);
                }
            }

            notifyDataSetChanged();
        }
    }

    public class Holder extends BaseHolder {
        public Holder(View v) {
            super(v);
        }

        public View info;
        public TextView fileName;
        public TextView length;
        public TextView modifyTime;
        public ImageView more;

        public View action;
        public TextView rename;
        public TextView delete;
    }


    private List<ClassroomDocument> getTestData() {
        List<ClassroomDocument> data = new ArrayList<ClassroomDocument>();

        for (int i = 0; i < 12; i++) {
            ClassroomDocument clsDoc = new ClassroomDocument();
            clsDoc.setFileName("人力资源管理");
            clsDoc.setModify(System.currentTimeMillis());
            clsDoc.setLength(890 * (i + 1));
            data.add(clsDoc);
        }
        return data;
    }
}
