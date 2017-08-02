package cn.xiaojs.xma.ui.grade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsListAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.material.UserDoc;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.MaterialUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * Created by maxiaobao on 2017/7/19.
 */

public class ImportVideoActivity extends BaseActivity {

    public static final String EXTRA_CHOICE_MODE = "cmode";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_CHOICE_DATA = "choice_data";
    public static final String EXTRA_ALREADY_CHOICE_DATA = "already_choice_data";
    public static final int REQUEST_CODE=44;
    public static final int REQUEST_UPLOAD_CODE=22;
    @BindView(R.id.list)
    PullToRefreshListView listView;
    @BindView(R.id.search)
    EditTextDel searchView;
    @BindView(R.id.lay_folder_tip)
    LinearLayout tipLay;
    @BindView(R.id.tip_name)
    TextView tipNameView;
    @BindView(R.id.checkall_btn)
    CheckBox checkAllBtn;


    private DataAdapter adapter;
    private Stack<VideoData> dataStack;
    private String folderName;
    private int choiceMode;

    private HashSet<String> disableIds;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_import_video);
        setRightText(R.string.finish);
        init();
    }

    @OnClick({R.id.left_image, R.id.right_image2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                onBack();
                break;
            case R.id.right_image2:
                complete();
                break;
        }
    }

    @OnCheckedChanged({R.id.checkall_btn})
    public void onCheckedChanged(CompoundButton v, boolean checked) {
        switch (v.getId()) {
            case R.id.checkall_btn:
                changeChoiceMode(checked);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void init() {

        choiceMode = getIntent().getIntExtra(EXTRA_CHOICE_MODE, AbsListView.CHOICE_MODE_MULTIPLE);
        String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        disableIds = (HashSet<String>) getIntent().getSerializableExtra(EXTRA_ALREADY_CHOICE_DATA);

        if (TextUtils.isEmpty(title)) {
            setMiddleTitle(R.string.import_video);
        }else {
            setMiddleTitle(title);
        }

        if (choiceMode == AbsListView.CHOICE_MODE_SINGLE){
            checkAllBtn.setVisibility(View.GONE);
        }

        dataStack = new Stack<>();
        adapter = new DataAdapter(this, listView);
        listView.setChoiceMode(choiceMode);
        listView.setAdapter(adapter);

    }

    private void onBack() {
        if (dataStack.size() <= 0){
            finish();
            return;
        }

        adapter.back();
    }

    private void changeChoiceMode(boolean checked) {

        if (checked) {
            checkAllBtn.setText(R.string.cancel_choice_all);
            choiceAll();
        }else {
            checkAllBtn.setText(R.string.choice_all);
            listView.clearChoices();
            adapter.notifyDataSetChanged();
        }

        updateChoiceCountView();

    }

    private void choiceAll() {
        if (adapter != null
                && adapter.getList() != null
                && adapter.getList().size() > 0) {

            int size = adapter.getList().size();
            for (int i = 0; i < size; i++) {

                LibDoc doc = adapter.getItem(i);
                if (!doc.showAction) {
                    listView.setItemChecked(i, true);
                }

            }
        }
    }

    private void updateTipNameView(String name){
        if (TextUtils.isEmpty(name)) {
            tipLay.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
        }else {
            tipNameView.setText(name);
            tipLay.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
        }
    }

    private void updateChoiceCountView() {

        if (choiceMode == AbsListView.CHOICE_MODE_SINGLE)
            return;

        int count = listView.getCheckItemIds().length;
        if (count <= 0) {
            setRightText(R.string.finish);
        }else {
            setRightText(getString(R.string.finish_with_count, count));
        }
    }

    private void complete() {
        //点击完成
        if (choiceMode == AbsListView.CHOICE_MODE_SINGLE) {
            int pos = listView.getRefreshableView().getCheckedItemPosition();
            if (pos >= 0 && pos < adapter.getCount()) {
                LibDoc libDoc = adapter.getItem(pos);

                Intent data = new Intent();
                data.putExtra(EXTRA_CHOICE_DATA,libDoc);
                setResult(RESULT_OK, data);
            }
        }else {
            long[] ids = listView.getCheckItemIds();
            if (ids !=null && ids.length>0) {

                ArrayList<LibDoc> libDocs = new ArrayList<>(ids.length);

                for (int i=0;i< ids.length;i++) {
                    LibDoc libDoc = adapter.getItem((int) ids[i]);
                    libDocs.add(libDoc);
                }

                Intent data = new Intent();
                data.putExtra(EXTRA_CHOICE_DATA,libDocs);
                setResult(RESULT_OK, data);
            }
        }


        finish();
    }

    private void filterVideo(List<LibDoc> docs){
        Iterator<LibDoc> iterator=docs.iterator();
        while (iterator.hasNext()){
            LibDoc doc=iterator.next();
            if(!(Collaboration.isVideo(doc.mimeType)||Collaboration.isStreaming(doc.mimeType))){
                iterator.remove();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_UPLOAD_CODE && resultCode == RESULT_OK) {
            if (adapter != null) {
                adapter.refresh();
            }
        }
    }

    public class DataAdapter extends AbsListAdapter<LibDoc, DataAdapter.Holder> {

        private int choiceResId;
        private int disableResId;


        class Holder extends BaseHolder {

            @BindView(R.id.check_view)
            CheckedTextView checkedView;
            @BindView(R.id.icon)
            ImageView iconView;
            @BindView(R.id.name)
            TextView nameView;
            @BindView(R.id.size)
            TextView sizeView;
            @BindView(R.id.open_button)
            TextView openBtn;


            public Holder(View view) {
                super(view);
            }
        }


        public DataAdapter(Context context, PullToRefreshListView listView) {
            super(context, listView);

            if (choiceMode == AbsListView.CHOICE_MODE_SINGLE) {
                choiceResId = R.drawable.single_check_selector;
                disableResId = R.drawable.ic_choice_single_disable;
            }else {
                choiceResId = R.drawable.multi_check_selector;
                disableResId = R.drawable.ic_choice_multipul_disable;
            }

            setEmptyDesc(R.string.choice_video_empty_tips);
            setEmptyButton(R.string.upload_now, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, MaterialFragmentActivity.class);
                    intent.putExtra(MaterialFragmentActivity.EXTRA_FROM,
                            MaterialFragmentActivity.FROM_CHOICE_EMPTY);
                    startActivityForResult(intent,REQUEST_UPLOAD_CODE);

                }
            });

        }

        @Override
        protected void onDataItemClick(int position, LibDoc bean) {
            if (bean.showAction) {
                //enter folder
                enterNext(bean);
            }else {

                super.onDataItemClick(position, bean);

            }

            updateChoiceCountView();

        }


        @Override
        public boolean isEnabled(int position) {

            if (position < 0)
                return false;

            LibDoc bean = getItem(position);

            if (disableIds != null && disableIds.size() >0) {
                if (disableIds.contains(bean.id)) {
                    return false;
                }
            }


            if (bean.showAction
                    || FileUtil.VIDEO == FileUtil.getFileType(bean.mimeType)
                    || FileUtil.STEAMIMG == FileUtil.getFileType(bean.mimeType)){
                return true;
            }

            return false;
        }

        @Override
        protected void setViewContent(Holder holder, final LibDoc bean, int position) {

            if (bean.showAction || !isEnabled(position)) {

                if (disableIds!=null && disableIds.contains(bean.id)) {
                    holder.checkedView.setCheckMarkDrawable(disableResId);
                    holder.checkedView.setVisibility(View.VISIBLE);
                }else {
                    holder.checkedView.setCheckMarkDrawable(choiceResId);
                    holder.checkedView.setVisibility(View.INVISIBLE);
                }

            } else {

                holder.checkedView.setCheckMarkDrawable(choiceResId);
                holder.checkedView.setVisibility(View.VISIBLE);
            }

            thumbnail(bean.showAction, bean.mimeType, bean.key, holder);

            holder.nameView.setText(bean.name);

            StringBuilder sb = new StringBuilder();

            if (bean.used <= 0) {
                sb.append("");
            } else {
                sb.append(XjsUtils.getSizeFormatText(bean.used));
                sb.append("  ");
            }
            sb.append(TimeUtil.format(bean.uploadedOn, TimeUtil.TIME_YYYY_MM_DD_HH_MM));

            holder.sizeView.setText(sb);


            holder.openBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialUtil.openMaterial((Activity) mContext,bean);
                }
            });


        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_import_vodeo_item, null);
        }

        @Override
        protected Holder initHolder(View view) {
            return new Holder(view);
        }

        @Override
        protected void onDataEmpty() {
        }

        @Override
        protected void doRequest() {

            CollaManager.getDocuments(mContext,
                    AccountDataManager.getAccountID(mContext),
                    Collaboration.SubType.PERSON,
                    mPagination,
                    new APIServiceCallback<UserDoc>() {
                @Override
                public void onSuccess(UserDoc object) {

                    if (getList() !=null) {
                        getList().clear();
                    }
                    if (object != null && object.documents!=null && object.documents.size() > 0) {
                        filterVideo(object.documents);
                        DataAdapter.this.onSuccess(object.documents);
                    } else {
                        DataAdapter.this.onSuccess(null);
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    DataAdapter.this.onFailure(errorCode, errorMessage);
                }
            });


//            int tempi = 0;
//
//            switch (dataStack.size()) {
//                case 0:
//                    tempi = 0;
//                    break;
//                case 1:
//                    tempi = 1000;
//                    break;
//                case 2:
//                    tempi = 2000;
//                    break;
//                default:
//                    break;
//            }
//
//            List<LibDoc> libDocs = new ArrayList<>();
//            for (int i = tempi; i < tempi + 10; i++) {
//                LibDoc doc = new LibDoc();
//                if (i == 0 || i == 3 || i == 6 || i==1000 || i== 2000) {
//                    doc.showAction = true;
//                    doc.name = "folder" + i;
//                }else {
//                    doc.name = "file.doc" + i;
//                }
//
//
//                doc.used = 1024 * i;
//                doc.uploadedOn = new Date();
//
//                libDocs.add(doc);
//            }
//
//            onSuccess(libDocs);

        }

        private void enterNext(LibDoc bean) {

            int currentPage = mPagination.getPage();
            dataStack.push(new VideoData(bean.id,
                    folderName,
                    currentPage,
                    new ArrayList<LibDoc>(getList())));

            listView.clearChoices();
            checkAllBtn.setChecked(false);

            if (getList() !=null) {
                getList().clear();
            }
            adapter.notifyDataSetChanged();

            folderName = bean.name;
            updateTipNameView(folderName);

            doRefresh();
        }

        private void back() {

            if (dataStack.size() <= 0) {
                return;
            }

            VideoData videoData = dataStack.pop();

            updateTipNameView(videoData.name);

            if (getList() !=null) {
                getList().clear();
            }
            onSuccess(videoData.libDocs);
            mPagination.setPage(videoData.page);
            listView.clearChoices();
        }

        private void thumbnail(boolean folder, String mimeType, String key, Holder holder) {

            String iconUrl = "";
            int errorResId;

            if (folder) {
                Glide.with(mContext)
                        .load(R.drawable.ic_folder)
                        .into(holder.iconView);
                return;
            }

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
            } else if (FileUtil.VIDEO == FileUtil.getFileType(mimeType)
                    || FileUtil.STEAMIMG == FileUtil.getFileType(mimeType)) {
                errorResId = R.drawable.ic_video_mine;
            } else {
                errorResId = R.drawable.ic_unknown;
            }

            Glide.with(mContext)
                    .load(iconUrl)
                    .placeholder(errorResId)
                    .error(errorResId)
                    .into(holder.iconView);
        }

    }

    static class VideoData{
        String id;
        String name;
        int page;
        List<LibDoc> libDocs;

        public VideoData(String id, String name, int page, List<LibDoc> libDocs) {
            this.id = id;
            this.name = name;
            this.page = page;
            this.libDocs = libDocs;
        }
    }
}
