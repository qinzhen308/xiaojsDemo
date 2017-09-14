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

import android.content.Intent;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Date;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.material.UserDoc;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.MaterialUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;
import cn.xiaojs.xma.util.XjsUtils;
import okhttp3.ResponseBody;

public class MaterialAdapter extends AbsSwipeAdapter<LibDoc, MaterialAdapter.Holder> {

    private boolean mIsMine;
    private String mOwner;

    private MaterialFragment materialFragment;


    public MaterialAdapter(MaterialFragment fragment, PullToRefreshSwipeListView listView, String owner) {
        super(fragment.getActivity(), listView);
        if (TextUtils.isEmpty(owner)) {
            mIsMine = true;
        }

        materialFragment = fragment;

        mOwner = owner;

        setDescNow("空空如也～");
    }

/*
    @Override
    protected PullToRefreshBase.Mode getRefreshMode() {
        return PullToRefreshBase.Mode.PULL_FROM_START;
    }
*/

    @Override
    protected void onDataItemClick(int position, LibDoc bean) {

        if (materialFragment.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
            return;
        }

        MaterialUtil.openMaterial(materialFragment.getActivity(), bean);
    }

    @Override
    protected void setViewContent(final Holder holder, final LibDoc bean, int position) {
        holder.showOpera(false);
        if (mIsMine) {
            holder.opera2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.share_selector, 0, 0);
            holder.opera2.setText(R.string.share);
        }

        thumbnail(bean.mimeType, bean.key, holder,bean);

        holder.name.setText(bean.name);

        if(Collaboration.State.INIT.equals(bean.state)||Collaboration.State.CONVERTING.equals(bean.state)){
            holder.desc.setTextColor(mContext.getResources().getColor(R.color.main_orange));
            holder.desc.setText("转码中...");
        }else if(Collaboration.State.FAULTED.equals(bean.state)){
            holder.desc.setTextColor(mContext.getResources().getColor(R.color.main_orange));
            holder.desc.setText("转码失败");
        }else {
            holder.desc.setTextColor(mContext.getResources().getColor(R.color.common_text));
            Date date=bean.uploadedOn!=null?bean.uploadedOn:bean.createdOn;
            StringBuilder sb = new StringBuilder();
            if(date!=null){
                sb.append(TimeUtil.format(date, TimeUtil.TIME_YYYY_MM_DD_HH_MM));
            }

            if (bean.used <= 0) {
                sb.append("");
            }else{
                sb.append("    ");
                sb.append(XjsUtils.getSizeFormatText(bean.used));

            }
            holder.desc.setText(sb);
        }






        if (materialFragment.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {

            holder.expand.setVisibility(View.GONE);
            holder.opera.setVisibility(View.GONE);
            holder.checkedTextView.setVisibility(View.VISIBLE);

        }else {
            holder.expand.setVisibility(View.VISIBLE);
            holder.opera.setVisibility(View.GONE);
            holder.checkedTextView.setVisibility(View.GONE);
        }

        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showOpera(holder.opera.getVisibility() != View.VISIBLE);
            }
        });


        holder.opera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDoanloadTips(bean);
            }
        });

        holder.opera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                materialFragment.chooseShare(new String[]{bean.id});

            }
        });

        holder.opera3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                materialFragment.confirmDel(bean.id);

            }
        });

        holder.opera4.setVisibility(View.VISIBLE);
        holder.opera4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreDlg();
            }
        });

    }

    private void showDoanloadTips(final LibDoc bean){
        if(Collaboration.isStreaming(bean.mimeType)){
            final CommonDialog commonDialog=new CommonDialog(mContext);
            commonDialog.setRightBtnText(R.string.yes);
            commonDialog.setLefBtnText(R.string.no);
            commonDialog.setDesc(R.string.donwload_doc_conver_vedio_tip);
            commonDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    commonDialog.dismiss();
                    convertStreamToMp4(bean);
                }
            });
            commonDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    commonDialog.dismiss();
                }
            });
            commonDialog.show();
        }else {
            download(bean);
        }
    }

    private void convertStreamToMp4(LibDoc libDoc){
//        showProgress(true);
        CollaManager.convertDocument(mContext, libDoc.id, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
//                cancelProgress();
                ToastUtil.showToast(mContext,"转码中...");
                refresh();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
//                cancelProgress();
                ToastUtil.showToast(mContext,errorMessage);
            }
        });
    }

    private void download(LibDoc bean) {
//        if (DownloadManager.allowDownload(mContext)) {
            DownloadManager.enqueueDownload(mContext,
                    bean.name,
                    bean.key,
                    MaterialUtil.getDownloadUrl(bean),
                    bean.mimeType,
                    Social.getDrawing(bean.key, true));
        Toast.makeText(mContext, "已添加到下载队列", Toast.LENGTH_SHORT).show();
//        } else {
//           Toast.makeText(mContext, "当前有下载任务，不能新建下载", Toast.LENGTH_SHORT).show();
//        }
//
//        Intent intent = new Intent(mContext, MaterialDownloadActivity.class);
//        mContext.startActivity(intent);
    }

    private void thumbnail(String mimeType, String key,Holder holder,LibDoc bean) {

        String iconUrl = "";
        int errorResId;

        if (FileUtil.DOC == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_word;
        } else if (FileUtil.PPT == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_ppt;
        } else if (FileUtil.XLS == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_excel;
        } else if (FileUtil.PICTURE == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_picture;
            iconUrl = Social.getDrawing(key, true);
        } else if (FileUtil.PDF == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_pdf;
        } else if (FileUtil.VIDEO == FileUtil.getFileType(mimeType)){
            if(Collaboration.TypeName.RECORDING_IN_LIBRARY.equals(bean.typeName)&&!bean.key.contains("H264")){
                errorResId = R.drawable.ic_record_video;
            }else {
                errorResId = R.drawable.ic_video_mine;
            }
        } else if(FileUtil.STEAMIMG == FileUtil.getFileType(mimeType)){
            errorResId = R.drawable.ic_record_video;
        }else {
            errorResId = R.drawable.ic_unknown;
        }

        Glide.with(mContext)
                .load(iconUrl)
                .placeholder(errorResId)
                .error(errorResId)
                .into(holder.image);
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
        CollaManager.getDocuments(mContext, mOwner, Collaboration.SubType.PERSON,Collaboration.TypeName.ALL, mPagination, new APIServiceCallback<UserDoc>() {
            @Override
            public void onSuccess(UserDoc object) {

              /*  if (getList() !=null) {
                    getList().clear();
                }
*/
                if (object != null && object.documents!=null && object.documents.size() > 0) {
                    MaterialAdapter.this.onSuccess(object.documents);
                } else {
                    MaterialAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                MaterialAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    private void showMoreDlg() {
        ListBottomDialog dialog = new ListBottomDialog(mContext);
        String[] items = mContext.getResources().getStringArray(R.array.opera_material_more);

        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:                 //重命名
                        showRenameDlg();
                        break;
                    case 1:                 //移动到
                        Intent i = new Intent(mContext, MoveFileActivity.class);
                        mContext.startActivity(i);
                        break;
                }
            }
        });
        dialog.show();
    }

    private void showRenameDlg() {
        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.setTitle(R.string.file_rename);

        final EditTextDel editText = new EditTextDel(mContext);
        editText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.px80)));
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setPadding(10, 0, 10, 0);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        editText.setTextColor(mContext.getResources().getColor(R.color.common_text));
        editText.setBackgroundResource(R.drawable.common_edittext_bg);

        dialog.setCustomView(editText);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {


                //TODO 文件重命名

                dialog.dismiss();
            }
        });

        dialog.show();
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
        @BindView(R.id.material_item_opera3)
        TextView opera3;
        @BindView(R.id.material_item_opera4)
        TextView opera4;

        @BindView(R.id.material_item)
        View item;

        @BindView(R.id.check_view)
        CheckedTextView checkedTextView;

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

            opera3.setVisibility(View.VISIBLE);
            opera1.setVisibility(View.VISIBLE);
            opera4.setVisibility(View.VISIBLE);
        }
    }
}
