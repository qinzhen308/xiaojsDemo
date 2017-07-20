package cn.xiaojs.xma.ui.grade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsListAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * Created by maxiaobao on 2017/7/19.
 */

public class ImportVideoActivity extends BaseActivity {

    @BindView(R.id.list)
    PullToRefreshListView listView;
    @BindView(R.id.search)
    EditTextDel searchView;



    private DataAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_import_video);
        setMiddleTitle(R.string.import_video);
        setRightText(R.string.finish);

        init();
    }

    @OnClick({R.id.left_image, R.id.right_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image:
                break;
        }
    }

    private void init() {
        adapter = new DataAdapter(this, listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

    }

    public class DataAdapter extends AbsListAdapter<LibDoc, DataAdapter.Holder> {

        public DataAdapter(Context context, PullToRefreshListView listView) {
            super(context, listView);
        }

        @Override
        protected void onDataItemClick(int position, LibDoc bean) {
            if (bean.showAction) {
                //enter folder
            }else {
                super.onDataItemClick(position, bean);
            }

        }

        @Override
        protected void setViewContent(Holder holder, LibDoc bean, int position) {

            if (bean.showAction) {
                holder.checkedView.setVisibility(View.INVISIBLE);
            } else {
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
        protected void doRequest() {

            List<LibDoc> libDocs = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                LibDoc doc = new LibDoc();
                if (i == 0 || i == 3 || i == 6) {
                    doc.showAction = true;
                    doc.name = "folder" + i;
                }else {
                    doc.name = "file.doc" + i;
                }


                doc.used = 1024 * i;
                doc.uploadedOn = new Date();

                libDocs.add(doc);
            }


            onSuccess(libDocs);

        }

        class Holder extends BaseHolder {

            @BindView(R.id.check_view)
            CheckedTextView checkedView;
            @BindView(R.id.icon)
            ImageView iconView;
            @BindView(R.id.name)
            TextView nameView;
            @BindView(R.id.size)
            TextView sizeView;


            public Holder(View view) {
                super(view);
            }
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
}
