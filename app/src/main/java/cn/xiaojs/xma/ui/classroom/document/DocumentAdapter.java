package cn.xiaojs.xma.ui.classroom.document;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.material.ShareDoc;
import cn.xiaojs.xma.model.material.ShareResource;
import cn.xiaojs.xma.model.material.UserDoc;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom2.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.ClassroomType;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.FileUtil;
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
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/12/29
 * Desc:
 *
 * =================================
 *
 * ======================================================= */

public class DocumentAdapter extends AbsSwipeAdapter<LibDoc, DocumentAdapter.Holder> implements View.OnClickListener {
    private final int LIMIT_DATA = 10000;
    private int mDefaultBgColor;
    private int mHoverBgColor;
    private String mOwnerId;
    private String mCategory;
    private String mType;
    private int drawSize = 30;

    private CommonDialog mDelDocDialog;
    private String mMyAccountId;
    private String mLessonId;
    private ClassroomType mClassroomType;



    public DocumentAdapter(Context context, PullToRefreshSwipeListView listView,String lessonId,String ownerId) {
        super(context, listView, true);
        mLessonId = lessonId;
        mOwnerId = ownerId;
        mType = Collaboration.SubType.PERSON;
        mCategory=Collaboration.TypeName.ALL;
        init();
    }

    public void setData(List<LibDoc> data, String type) {
        mType = type;
        setList(data);
    }

    public String getType() {
        return mType;
    }

    public String getLessonId() {
        return mLessonId;
    }

    public void setType(String subType){
        mType=subType;
    }

    public void setCategory(String category){
        mCategory=category;
    }



    public String getOwnerId() {
        return mOwnerId;
    }

    private void init() {
        mDefaultBgColor = mContext.getResources().getColor(R.color.white);
        mHoverBgColor = mContext.getResources().getColor(R.color.main_blue);
        drawSize = mContext.getResources().getDimensionPixelSize(R.dimen.px40);
        mMyAccountId = AccountDataManager.getAccountID(mContext);

        mClassroomType = ClassroomEngine.getEngine().getClassroomType();
    }

    public List<LibDoc> getData() {
        return getList();
    }


    @Override
    public void showProgress(boolean cancelable) {
        if (mContext instanceof ClassroomActivity) {
            ((ClassroomActivity) mContext).showProgress(cancelable);
        }
    }

    @Override
    public void cancelProgress() {
        if (mContext instanceof ClassroomActivity) {
            ((ClassroomActivity) mContext).cancelProgress();
        }
    }

    @Override
    protected void setViewContent(Holder holder, LibDoc bean, int position) {
        holder.fileName.setText(bean.name);
        holder.length.setText(XjsUtils.getSizeFormatText(bean.used));
        holder.modifyTime.setText(TimeUtil.formatDate(bean.uploadedOn != null ? bean.uploadedOn.getTime() : System.currentTimeMillis(),
                TimeUtil.TIME_YYYY_MM_DD_HH_MM));
        holder.more.setTag(position);
        holder.delete.setTag(position);
        holder.share.setTag(position);
        int drawId = 0;
        if (FileUtil.DOC == FileUtil.getFileType(bean.mimeType)) {
            drawId = R.drawable.ic_word;
        } else if (FileUtil.PPT == FileUtil.getFileType(bean.mimeType)) {
            drawId = R.drawable.ic_ppt;
        } else if (FileUtil.XLS == FileUtil.getFileType(bean.mimeType)) {
            drawId = R.drawable.ic_excel;
        } else if (FileUtil.PICTURE == FileUtil.getFileType(bean.mimeType)) {
            drawId = R.drawable.ic_picture;
        } else if (FileUtil.PDF == FileUtil.getFileType(bean.mimeType)) {
            drawId = R.drawable.ic_pdf;
        } else if (FileUtil.VIDEO == FileUtil.getFileType(bean.mimeType)) {
            drawId = R.drawable.ic_video_mine;
        } else if (Collaboration.isStreaming(bean.mimeType)) {
            drawId = R.drawable.ic_record_video;
        } else {
            drawId = R.drawable.ic_unknown;
        }

        Drawable myImage = mContext.getResources().getDrawable(drawId);
        myImage.setBounds(0, 0, drawSize, drawSize);
        holder.fileName.setCompoundDrawables(myImage, null, null, null);
        //holder.fileName.setCompoundDrawablesWithIntrinsicBounds(drawId, 0, 0, 0);
        holder.action.setVisibility(bean.showAction ? View.VISIBLE : View.GONE);
        //holder.info.setBackgroundColor(bean.isShowAction() ? mHoverBgColor : mDefaultBgColor);

        if (Collaboration.SubType.STANDA_LONE_LESSON.equals(mType)
                || Collaboration.SubType.PRIVATE_CLASS.equals(mType)) {

            String owner = bean.owner != null ? bean.owner.id : "";
            if (owner != null && owner.equals(mMyAccountId)) {
                //myself
                holder.more.setVisibility(View.VISIBLE);
            } else {
                holder.more.setVisibility(View.GONE);
            }
        } else {
            holder.more.setVisibility(View.VISIBLE);
        }
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
        holder.share = (TextView) view.findViewById(R.id.share);
        holder.delete = (TextView) view.findViewById(R.id.delete);
        holder.share.setOnClickListener(this);
        holder.delete.setOnClickListener(this);

        if (Collaboration.SubType.STANDA_LONE_LESSON.equals(mType)
                || Collaboration.SubType.PRIVATE_CLASS.equals(mType)) {
            holder.share.setVisibility(View.GONE);
        } else {
            holder.share.setVisibility(View.VISIBLE);
        }

        return holder;
    }

    @Override
    protected void doRequest() {
        String id="";
        if(Collaboration.SubType.PRIVATE_CLASS.equals(mType)||Collaboration.SubType.STANDA_LONE_LESSON.equals(mType)){
            id=mLessonId;
        }else {
            id=mOwnerId;
        }
        CollaManager.getDocuments(mContext, id, mType,mCategory, mPagination, new APIServiceCallback<UserDoc>() {
            @Override
            public void onSuccess(UserDoc object) {
                if (object != null && object.documents.size() > 0) {
                    DocumentAdapter.this.onSuccess(object.documents);
                } else {
                    DocumentAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                DocumentAdapter.this.onFailure(errorCode, errorMessage);
            }
        });

    }

    @Override
    public void onClick(View v) {
        final List<LibDoc> data = getList();
        final int pos = (int) v.getTag();
        LibDoc clsDoc = null;
        if (data != null) {
            switch (v.getId()) {
                case R.id.more:
                    //open action operator
                    for (int i = 0; i < data.size(); i++) {
                        clsDoc = data.get(i);
                        if (i == pos) {
                            clsDoc.showAction = !clsDoc.showAction;
                        } else {
                            clsDoc.showAction = false;
                        }
                    }

                    notifyDataSetChanged();
                    break;
                case R.id.share:
                    //share
                    clsDoc = data.get(pos);
                    showProgress(false);
                    ShareResource shareResource = new ShareResource();
                    shareResource.targetId = mLessonId;

                    String subtype = ClassroomType.ClassLesson == mClassroomType ?
                            Collaboration.SubType.PRIVATE_CLASS : Collaboration.SubType.STANDA_LONE_LESSON;

                    shareResource.subtype = subtype;
                    shareResource.sharedType = Collaboration.ShareType.SHORTCUT;
                    CollaManager.shareDocument(mContext, clsDoc.id, shareResource, new APIServiceCallback<ShareDoc>() {
                        @Override
                        public void onSuccess(ShareDoc object) {
                            cancelProgress();
                            Toast.makeText(mContext, R.string.cr_doc_share_succ, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            cancelProgress();
                            Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case R.id.delete:
                    showDelDocDialog(data, pos);
                    break;
            }
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
        public TextView share;
        public TextView delete;
    }

    /**
     * 删除文档，弹出选择确认
     */
    private void showDelDocDialog(final List<LibDoc> data, final int pos) {
        if (mDelDocDialog == null) {
            mDelDocDialog = new CommonDialog(mContext);
            mDelDocDialog.setTitle(R.string.tips);
            mDelDocDialog.setDesc(R.string.cls_doc_del_desc);
            mDelDocDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mDelDocDialog.dismiss();
                    //delete
                    LibDoc clsDoc = data.get(pos);
                    showProgress(false);
                    boolean shared = (Collaboration.SubType.STANDA_LONE_LESSON.equals(mType))
                            || (Collaboration.SubType.PRIVATE_CLASS.equals(mType));
                    CollaManager.deleteDocument(mContext, clsDoc.id, shared, new APIServiceCallback() {
                        @Override
                        public void onSuccess(Object object) {
                            cancelProgress();
                            data.remove(pos);
                            Toast.makeText(mContext, R.string.cls_doc_del_succ, Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            cancelProgress();
                            Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        mDelDocDialog.show();
    }

}
