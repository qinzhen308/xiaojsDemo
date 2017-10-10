package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.material.UserDoc;
import cn.xiaojs.xma.ui.classroom2.widget.LoadmoreRecyclerView;
import cn.xiaojs.xma.ui.grade.MoveFileActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.MaterialUtil;
import cn.xiaojs.xma.util.ToastUtil;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class DatabaseListFragment extends Fragment implements LoadmoreRecyclerView.LoadmoreListener {

    @BindView(R.id.rlist)
    LoadmoreRecyclerView recyclerView;

    private MaterialAdapter materialAdapter;
    private List<LibDoc> libDocs;
    private Pagination pagination;
    private boolean loading;

    private String directoryId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom2_databaselist, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        libDocs = new ArrayList<>();
        materialAdapter = new MaterialAdapter(this,
                XiaojsConfig.mLoginUser.getId(), libDocs);
        recyclerView.setAdapter(materialAdapter);
        recyclerView.setLoadmoreListener(this);

        pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(50);

        loadData();

    }

    @OnClick({R.id.sort_btn, R.id.filter_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.sort_btn:
                break;
            case R.id.filter_btn:
                break;
        }
    }

    @Override
    public void onLoadMore() {
        if (loading)
            return;

        loadData();
    }

    private void loadData() {
        loading = true;

//        materialAdapter.setLoading(loading);
//        materialAdapter.notifyDataSetChanged();

        CollaManager.getDocuments(getContext(),
                XiaojsConfig.mLoginUser.getId(),
                Collaboration.SubType.PERSON,
                Collaboration.TypeName.ALL, pagination, new APIServiceCallback<UserDoc>() {
                    @Override
                    public void onSuccess(UserDoc object) {

                        loading = false;

                        directoryId = object.libId;

                        // materialAdapter.setLoading(loading);

                        if (object != null && object.documents != null && object.documents.size() > 0) {
                            libDocs.addAll(object.documents);
                            pagination.setPage(pagination.getPage() + 1);
                            materialAdapter.notifyDataSetChanged();
                        }

                        //materialAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        loading = false;

//                         materialAdapter.setLoading(loading);
//                        materialAdapter.notifyDataSetChanged();
                    }
                });
    }

    public String getCurrentDirectoryId() {
        return directoryId;
    }

    public void refreshData() {
        libDocs.clear();
        pagination.setPage(1);
        loadData();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 分享
    //





    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 更多
    //

    public void showMoreDlg(final LibDoc bean, final int position) {
        ListBottomDialog dialog = new ListBottomDialog(getContext());
        String[] items = getResources().getStringArray(R.array.opera_material_more);

        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:                 //重命名
                        showRenameDlg(bean, position);
                        break;
                    case 1:                 //移动到
//                        Intent i = new Intent(mContext, MoveFileActivity.class);
//                        mContext.startActivity(i);
                        break;
                }
            }
        });
        dialog.show();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 重命名
    //

    private void showRenameDlg(final LibDoc bean, final int position) {
        final CommonDialog dialog = new CommonDialog(getContext());
        dialog.setTitle(R.string.file_rename);

        final EditTextDel editText = new EditTextDel(getContext());
        editText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.px80)));
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setPadding(10, 0, 10, 0);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        editText.setTextColor(getResources().getColor(R.color.common_text));
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

                String newName = editText.getText().toString().trim();
                if (TextUtils.isEmpty(newName)) {
                    ToastUtil.showToast(getContext(), "名称不能为空");
                    return;
                }

                if (newName.equals(bean.name)) {
                    dialog.dismiss();
                    return;
                }

                dialog.dismiss();
                rename(bean,newName, position);
            }
        });

        dialog.show();
    }

    private void rename(final LibDoc bean, final String newName, final int position) {

        CollaManager.editDocument(getContext(),
                bean.id, newName, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                ToastUtil.showToast(getContext(), "修改成功");
                bean.name = newName;
                materialAdapter.notifyItemChanged(position);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getContext(), errorMessage);

            }
        });
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 删除
    //

    public void confirmDel(final String docId, final int position) {
        final CommonDialog dialog = new CommonDialog(getContext());
        dialog.setTitle("提示");
        dialog.setDesc("确定需要删除该资料吗？");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                deleteDoc(docId, position);
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteDoc(String docId, final int position) {

        CollaManager.deleteDocument(getContext(), docId, true, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(getContext(), R.string.delete_success, Toast.LENGTH_SHORT).show();

                libDocs.remove(position);
                materialAdapter.notifyItemRemoved(position);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 下载
    //
    public void showDoanloadTips(final LibDoc bean, final int position) {
        if (Collaboration.isStreaming(bean.mimeType)) {
            final CommonDialog commonDialog = new CommonDialog(getContext());
            commonDialog.setRightBtnText(R.string.yes);
            commonDialog.setLefBtnText(R.string.no);
            commonDialog.setDesc(R.string.donwload_doc_conver_vedio_tip);
            commonDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    commonDialog.dismiss();
                    convertStreamToMp4(bean, position);
                }
            });
            commonDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    commonDialog.dismiss();
                }
            });
            commonDialog.show();
        } else {
            download(bean);
        }
    }

    private void convertStreamToMp4(final LibDoc libDoc, final int position) {

        CollaManager.convertDocument(getContext(),
                libDoc.id, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                ToastUtil.showToast(getContext(), "转码中...");
                libDoc.state = Collaboration.State.CONVERTING;
                materialAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getContext(), errorMessage);
            }
        });
    }

    private void download(LibDoc bean) {
        DownloadManager.enqueueDownload(getContext(),
                bean.name,
                bean.key,
                MaterialUtil.getDownloadUrl(bean),
                bean.mimeType,
                Social.getDrawing(bean.key, true));
        Toast.makeText(getContext(), "已添加到下载队列", Toast.LENGTH_SHORT).show();

    }

}
