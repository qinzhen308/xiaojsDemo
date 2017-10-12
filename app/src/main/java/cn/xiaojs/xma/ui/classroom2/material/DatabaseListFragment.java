package cn.xiaojs.xma.ui.classroom2.material;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.material.ConflictRes;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.material.ShareDoc;
import cn.xiaojs.xma.model.material.ShareResource;
import cn.xiaojs.xma.model.material.UserDoc;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.classroom2.base.BaseRoomFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.widget.LoadmoreRecyclerView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.MaterialUtil;
import cn.xiaojs.xma.util.ToastUtil;
import cn.xiaojs.xma.util.XjsUtils;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class DatabaseListFragment extends BaseRoomFragment
        implements LoadmoreRecyclerView.LoadmoreListener {

    @BindView(R.id.rlist)
    LoadmoreRecyclerView recyclerView;
    @BindView(R.id.cancel_btn)
    Button cancelBtnView;
    @BindView(R.id.search_input)
    EditTextDel searchInputView;

    @BindView(R.id.search_bottom_line)
    View lineView;

    private MaterialAdapter materialAdapter;
    private List<LibDoc> libDocs;
    private Pagination pagination;
    private boolean loading;

    private String directoryId;

    private Stack<DocFolder> folderStack;
    private OnOperatingListener operatingListener;

    private int filterIndex;
    private int sortIndex;
    private String searchKey;

    private boolean searching;
    private boolean forbiddenLoadSearch;


    public interface OnOperatingListener {
        void onEnterFolder();
        void onBackSuper();
        void onEnterSearch();
    }


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

        folderStack = new Stack<>();

        libDocs = new ArrayList<>();
        materialAdapter = new MaterialAdapter(this,
                XiaojsConfig.mLoginUser.getId(), libDocs);
        recyclerView.setAdapter(materialAdapter);
        recyclerView.setLoadmoreListener(this);

        pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(50);

        loadData(null);

    }

    @OnClick({R.id.sort_btn, R.id.filter_btn, R.id.cancel_btn, R.id.bottom_input})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.sort_btn:
                showSortMenu();
                break;
            case R.id.filter_btn:
                showFilterMenu();
                break;
            case R.id.cancel_btn:
                exitSearchMode();
                break;
            case R.id.bottom_input:
                enterSearchMode();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case CTLConstant.REQUEST_CHOOSE_CLASSES:
                    Contact contact = (Contact) data.getSerializableExtra(CTLConstant.EXTRA_CONTACT);
                    String docId = data.getStringExtra(CTLConstant.EXTRA_DOC_ID);
                    toPatchShare(contact,docId, false);
                    break;
                case CTLConstant.REQUEST_MATERIAL_MOVE:
                    refreshData();
                    break;
            }
        }

    }

    @Override
    public void onLoadMore() {
        if (loading)
            return;

        loadData(searchKey);
    }

    private void loadData(String name) {
        loading = true;

//        materialAdapter.setLoading(loading);
//        materialAdapter.notifyDataSetChanged();

        CollaManager.getDocuments(getContext(),
                XiaojsConfig.mLoginUser.getId(),
                directoryId,
                Collaboration.SubType.PERSON,
                name,
                getCategoryByIndex(),
                getSortByIndex(),0, pagination, new APIServiceCallback<UserDoc>() {
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
        loadData(null);
    }

    public void setOnOperatingListener(OnOperatingListener listener) {
        operatingListener = listener;
    }

    public boolean canBackSuper() {
        return !folderStack.empty();
    }

    private void reLoadData() {
        libDocs.clear();
        materialAdapter.notifyDataSetChanged();

        pagination.setPage(1);
        loadData(null);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 进入下级目录／返回上级目录
    //

    public void enterNext(LibDoc doc, int position) {

        if (operatingListener!= null) {
            operatingListener.onEnterFolder();
        }

        folderStack.push(
                new DocFolder(directoryId, libDocs, pagination.getPage(),
                        filterIndex, sortIndex, searchKey,searching));
        directoryId = doc.id;

        searchKey = null;
        searching = false;
        hiddeSearch();


        libDocs.clear();
        materialAdapter.notifyDataSetChanged();

        pagination.setPage(1);
        loadData(null);

    }

    public void backSuper() {

        if (canBackSuper()) {
            DocFolder docFolder = folderStack.pop();
            directoryId = docFolder.directory;
            pagination.setPage(docFolder.page);
            filterIndex = docFolder.filterIndex;
            sortIndex = docFolder.sortIndex;

            searchKey = docFolder.searchKey;
            searching = docFolder.searching;
            if (docFolder.searching) {
                showSearch();
            }else {
                hiddeSearch();
            }


            libDocs.clear();
            libDocs.addAll(docFolder.data);
            materialAdapter.notifyDataSetChanged();

        }

        if (operatingListener != null) {
            operatingListener.onBackSuper();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 搜索
    //

    private void exitSearchMode() {
        searching = false;
        searchKey = null;

        hiddeSearch();

        backSuper();
    }

    private void enterSearchMode() {

        showSearch();

        searching = true;

        folderStack.push(
                new DocFolder(directoryId, libDocs, pagination.getPage(),
                        filterIndex, sortIndex, null, false));

        filterIndex = 0;
        sortIndex = 0;

        if (operatingListener !=null) {
            operatingListener.onEnterSearch();
        }

    }

    private void hiddeSearch() {

        forbiddenLoadSearch = true;

        searchInputView.setEnabled(false);
        searchInputView.setText("");
        searchInputView.setVisibility(View.GONE);
        cancelBtnView.setVisibility(View.GONE);
        XjsUtils.hideIMM(getContext(), searchInputView.getWindowToken());
    }

    private void showSearch() {

        forbiddenLoadSearch = true;

        cancelBtnView.setVisibility(View.VISIBLE);
        searchInputView.setText(searchKey);
        searchInputView.setVisibility(View.VISIBLE);
        searchInputView.setEnabled(true);
        searchInputView.requestFocus();
        XjsUtils.showIMM(getContext(), searchInputView);
    }

    @OnTextChanged(value = R.id.search_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void searchTextChanged(Editable editable) {

        if (forbiddenLoadSearch) {
            forbiddenLoadSearch = false;
            return;
        }


        if (XiaojsConfig.DEBUG) {
            Logger.d("searchTextChanged----------%s", editable.toString());
        }

        pagination.setPage(1);
        libDocs.clear();
        materialAdapter.notifyDataSetChanged();

        searchKey = editable.toString();
        loadData(searchKey);
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 筛选
    //


    private void showFilterMenu(){
        String[] titles = getResources().getStringArray(R.array.material_filters);
        FilterPopupMenu menu = new FilterPopupMenu(getContext(),titles, filterIndex);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filterIndex = position;
                reLoadData();
            }
        });
        menu.showAsDropDown(lineView);
    }

    private String getCategoryByIndex() {

        String category = Collaboration.TypeName.ALL;

        switch (filterIndex) {
            case 0:
                category = Collaboration.TypeName.ALL;
                break;
            case 1:
                category = Collaboration.TypeName.PICTURE_IN_LIBRARY;
                break;
            case 2:
                category = Collaboration.TypeName.PPT_IN_LIBRARY;
                break;
            case 3:
                category = Collaboration.TypeName.PDF_IN_LIBRARY;
                break;
            case 4:
                category = Collaboration.TypeName.WORD_IN_LIBRARY;
                break;
            case 5:
                category = Collaboration.TypeName.MEDIA_IN_LIBRARY;
                break;
            case 6:
                category = Collaboration.TypeName.DIRECTORY_IN_LIBRARY;
                break;
        }

        return category;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 排序
    //


    private void showSortMenu(){
        String[] titles = getResources().getStringArray(R.array.material_sorts);
        FilterPopupMenu menu = new FilterPopupMenu(getContext(),titles, sortIndex);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sortIndex = position;
                reLoadData();
            }
        });
        menu.showAsDropDown(lineView);
    }

    private String getSortByIndex() {

        String sort = "name";

        switch (sortIndex) {
            case 0:
                sort = "name";
                break;
            case 1:
                sort = "createdOn";
                break;
//            case 2:
//                sort = "name";
//                break;
//            case 3:
//                sort = "name";
//                break;
//            case 4:
//                sort = "name";
//                break;
//            case 5:
//                sort = "name";
//                break;
//            case 6:
//                sort = "name";
//                break;
        }

        return sort;
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 分享
    //

    public void chooseClasses(final LibDoc bean, final int position) {
        ChooseClassFragment chooseClassFragment = new ChooseClassFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CTLConstant.EXTRA_DOC_ID, bean.id);
        chooseClassFragment.setArguments(bundle);
        chooseClassFragment.setTargetFragment(this, CTLConstant.REQUEST_CHOOSE_CLASSES);
        chooseClassFragment.show(getFragmentManager(),"chooseClass");
    }

    private void toPatchShare(final Contact choosedClass, final String docId, boolean repeat) {

        ShareResource resource = new ShareResource();
        resource.documents = new String[] {docId};
        resource.subtype = choosedClass.subtype;
        resource.repeated = repeat;

        showProgress(true);
        CollaManager.shareDocuments(getContext(),
                choosedClass.account, resource, new APIServiceCallback<ShareDoc>() {
            @Override
            public void onSuccess(ShareDoc object) {
                cancelProgress();

                if (object != null && object.repeated != null && object.repeated.length > 0) {
                    //说明分享的文件有已存在的，需要询问用户
                    shareConflictWithPatch(choosedClass, docId, object.repeated);
                } else {
                    //shareSuccess(targetId, classname, subType);
                    ToastUtil.showToast(getContext(), R.string.shareok_and_to_class);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();

                ToastUtil.showToast(getContext(), errorMessage);

            }
        });
    }


    private void shareConflictWithPatch(final Contact choosedClass,
                                        final String docId, final ConflictRes[] conflicts) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_dlg_list, null);
        final ListView listView = (ListView) view;


        TextView headerView = new TextView(getContext());
        headerView.setText(R.string.query_go_on_share_when_confilc);
        headerView.setTextSize(14);
        headerView.setTextColor(getResources().getColor(R.color.font_light_gray));
        headerView.setPadding(0, 0, 0, 10);


        ArrayAdapter<ConflictRes> adapter = new ArrayAdapter<ConflictRes>(getContext(),
                R.layout.layout_text_item_only, conflicts);


        listView.addHeaderView(headerView);
        listView.setAdapter(adapter);

        final CommonDialog dialog = new CommonDialog(getContext());
        dialog.setTitle(R.string.patch_share_material);
        dialog.setCustomView(listView);
        dialog.setLefBtnText(R.string.cancel);
        dialog.setRightBtnText(R.string.go_on_share_material);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();

                String[] documentIds = new String[conflicts.length];
                for (int i = 0; i < conflicts.length; i++) {
                    documentIds[i] = conflicts[i].id;
                }

                toPatchShare(choosedClass, docId, true);

            }
        });

        dialog.show();

    }

    public void shareSuccess(final String classId, final String classname, final String subType) {
//        final CommonDialog dialog = new CommonDialog(getContext());
//        dialog.setTitle("提示");
//        dialog.setDesc(R.string.shareok_and_to_class);
//        dialog.setLefBtnText(R.string.dont_go);
//        dialog.setRightBtnText(R.string.go_class);
//
//        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
//            @Override
//            public void onClick() {
//                dialog.dismiss();
//                databank(classId, classname, subType);
//            }
//        });
//        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
//            @Override
//            public void onClick() {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
    }




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
                        toMove(bean, position);
                        break;
                }
            }
        });
        dialog.show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 移动
    //

    private void toMove(final LibDoc bean, final int position) {
        MoveFolderFragment moveFolderFragment = new MoveFolderFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(CTLConstant.EXTRA_DOC, bean);
        bundle.putSerializable(CTLConstant.EXTRA_DOC_ID, directoryId);
        moveFolderFragment.setArguments(bundle);
        moveFolderFragment.setTargetFragment(this, CTLConstant.REQUEST_MATERIAL_MOVE);
        moveFolderFragment.show(getFragmentManager(), "moveto");
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
