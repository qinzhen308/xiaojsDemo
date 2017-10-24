package cn.xiaojs.xma.ui.classroom2.material;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.loader.DataLoder;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.model.material.UserDoc;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.widget.LoadmoreRecyclerView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class MoveFolderFragment extends BottomSheetFragment implements
        LoadmoreRecyclerView.LoadmoreListener,
        DialogInterface.OnKeyListener {


    @BindView(R.id.rlist)
    LoadmoreRecyclerView recyclerView;

    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.tips)
    TextView tipsView;

    private FolderAdapter folderAdapter;

    private List<LibDoc> libDocs;
    private Pagination pagination;
    private String directoryId;
    private boolean loading;
    private Stack<DocFolder> folderStack;


    private LibDoc targetDoc;
    private String oldInDir;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_move_folder, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().setOnKeyListener(this);

        targetDoc = (LibDoc) getArguments().getSerializable(CTLConstant.EXTRA_DOC);
        oldInDir = getArguments().getString(CTLConstant.EXTRA_DOC_ID);

        folderStack = new Stack<>();
        libDocs = new ArrayList<>();

        folderAdapter = new FolderAdapter(this, libDocs);

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(folderAdapter);
        recyclerView.setLoadmoreListener(this);

        pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(50);


        loadData();
    }


    @OnClick({R.id.back_btn, R.id.cancel_btn, R.id.create_btn, R.id.move_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                backSuper();
                break;
            case R.id.cancel_btn:
                dismiss();
                break;
            case R.id.create_btn:
                newFolder();
                break;
            case R.id.move_btn:
                move();
                break;
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
            backSuper();
            return true;
        }

        return false;
    }

    @Override
    public void onLoadMore() {

        if (loading)
            return;
        loadData();

    }

    public void refreshData() {
        libDocs.clear();
        pagination.setPage(1);
        loadData();
    }


    private void loadData() {

        loading = true;

        CollaManager.getDocuments(getContext(),
                XiaojsConfig.mLoginUser.getId(),
                directoryId,
                Collaboration.SubType.PERSON,
                null,
                Collaboration.TypeName.DIRECTORY_IN_LIBRARY,
                "name", 0, pagination, new APIServiceCallback<UserDoc>() {
                    @Override
                    public void onSuccess(UserDoc object) {

                        loading = false;

                        directoryId = object.libId;

                        // materialAdapter.setLoading(loading);

                        if (object != null && object.documents != null && object.documents.size() > 0) {


                            if (Collaboration.TypeName.DIRECTORY_IN_LIBRARY.equals(targetDoc.typeName)) {

                                Observable.fromArray(object.documents)
                                        .observeOn(Schedulers.io())
                                        .filter(new Predicate<List<LibDoc>>() {
                                            @Override
                                            public boolean test(@NonNull List<LibDoc> libDocs) throws Exception {

                                                LibDoc delDoc = null;
                                                for (LibDoc libDoc : libDocs) {

                                                    if (!Collaboration.TypeName.DIRECTORY_IN_LIBRARY.equals(libDoc.typeName)) {
                                                        continue;
                                                    }

                                                    if (libDoc.id.equals(targetDoc.id)) {
                                                        delDoc = libDoc;
                                                        break;
                                                    }
                                                }

                                                if (delDoc != null) {
                                                    libDocs.remove(delDoc);
                                                }

                                                return true;
                                            }
                                        })
//                                        .doOnNext(new Consumer<List<LibDoc>>() {
//                                            @Override
//                                            public void accept(List<LibDoc> libDocs) throws Exception {
//
//                                            }
//                                        })
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<List<LibDoc>>() {
                                            @Override
                                            public void accept(List<LibDoc> libDocsx) throws Exception {

                                                if (getActivity() == null)
                                                    return;

                                                libDocs.addAll(libDocsx);
                                                pagination.setPage(pagination.getPage() + 1);
                                                folderAdapter.notifyDataSetChanged();
                                            }
                                        });

                            } else {
                                libDocs.addAll(object.documents);
                                pagination.setPage(pagination.getPage() + 1);
                                folderAdapter.notifyDataSetChanged();
                            }
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 进入下级目录／返回上级目录
    //

    public void enterNext(LibDoc doc, int position) {

        DocFolder docFolder = new DocFolder(directoryId, libDocs, pagination.getPage(),
                0, 0, null, false);
        docFolder.title = titleView.getText().toString();
        docFolder.tips = tipsView.getText().toString();

        folderStack.push(docFolder);
        directoryId = doc.id;

        titleView.setText(doc.name);
        tipsView.setText("选择目标文件(" + doc.name + ")");


        libDocs.clear();
        folderAdapter.notifyDataSetChanged();

        pagination.setPage(1);
        loadData();

    }

    public void backSuper() {

        if (canBackSuper()) {
            DocFolder docFolder = folderStack.pop();
            directoryId = docFolder.directory;
            pagination.setPage(docFolder.page);

            titleView.setText(docFolder.title);
            tipsView.setText(docFolder.tips);

            libDocs.clear();
            libDocs.addAll(docFolder.data);
            folderAdapter.notifyDataSetChanged();

        } else {
            dismiss();
        }
    }

    public boolean canBackSuper() {
        return !folderStack.empty();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 新建文件夹
    //

    private void newFolder() {

        final CommonDialog dialog = new CommonDialog(getContext());
        dialog.setTitle(R.string.new_folder);

        final EditTextDel editText = new EditTextDel(getContext());
        editText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.px80)));
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setPadding(10, 0, 10, 0);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        editText.setTextColor(getResources().getColor(R.color.common_text));
        editText.setBackgroundResource(R.drawable.common_edittext_bg);
        editText.setHint(R.string.new_folder_hint);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

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

                String name = editText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getContext(), "名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();

                //新建文件夹
                createFolder(name);
            }
        });

        dialog.show();

    }

    private void createFolder(String name) {
        CollaManager.createDirectory(getContext(),
                name, directoryId, new APIServiceCallback<UploadReponse>() {
                    @Override
                    public void onSuccess(UploadReponse object) {
                        ToastUtil.showToast(getContext(), "创建成功");

                        Fragment target = getTargetFragment();
                        if (target != null) {
                            Intent intent = new Intent();
                            target.onActivityResult(CTLConstant.REQUEST_MATERIAL_MOVE,
                                    Activity.RESULT_OK, intent);
                        }

                        refreshData();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        ToastUtil.showToast(getContext(), errorMessage);
                    }
                });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 移动文件夹
    //


    private void move() {

        if (oldInDir.equals(directoryId)) {
            ToastUtil.showToast(getContext(), "已经在该文件夹下了");
            return;
        }


        showProgress(true);
        CollaManager.moveDocuments(getContext(),
                directoryId, new String[]{targetDoc.id}, new APIServiceCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody object) {

                        cancelProgress();
                        ToastUtil.showToast(getContext(), "移动成功");

                        Fragment target = getTargetFragment();
                        if (target != null) {
                            Intent intent = new Intent();
                            target.onActivityResult(CTLConstant.REQUEST_MATERIAL_MOVE,
                                    Activity.RESULT_OK, intent);
                        }

                        dismiss();

                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                        ToastUtil.showToast(getContext(), errorMessage);

                    }
                });

    }


}
