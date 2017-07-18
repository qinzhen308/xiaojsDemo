package cn.xiaojs.xma.ui.grade;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.xclass.VerificationActivity;

/**
 * Created by maxiaobao on 2017/7/18.
 */

public class ChoiceFolderActivity extends BaseActivity {

    @BindView(R.id.folder_list)
    PullToRefreshSwipeListView listView;

    private FolderAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_choice_folder);
        setMiddleTitle(getString(R.string.file_move));
        setRightText(R.string.ok);

        init();

    }

    @OnClick({R.id.left_image, R.id.right_image2})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:         //返回
                finish();
                break;
            case R.id.right_image2:        //确定
                //TODO 确定
                break;
        }
    }

    private void init() {
        adapter = new FolderAdapter(this, listView);
        listView.setAdapter(adapter);
    }

    private class FolderAdapter extends AbsSwipeAdapter<Void, FolderAdapter.Holder> {

        public FolderAdapter(Context context, PullToRefreshSwipeListView listView) {
            super(context, listView);
        }

        @Override
        protected void setViewContent(Holder holder, Void bean, int position) {

            //TODO
        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_contact_choice_child, null);
        }

        @Override
        protected Holder initHolder(View view) {
            return new Holder(view);
        }

        @Override
        protected void doRequest() {

            //TODO
        }

        class Holder extends BaseHolder {

            //@BindView(R.id.icon)
            //ImageView iconView;
            @BindView(R.id.title)
            TextView titleView;
            @BindView(R.id.time)
            TextView timeView;

            public Holder(View view) {
                super(view);
            }
        }
    }
}
