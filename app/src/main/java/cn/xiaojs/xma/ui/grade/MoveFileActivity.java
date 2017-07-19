package cn.xiaojs.xma.ui.grade;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by maxiaobao on 2017/7/18.
 */

public class MoveFileActivity extends BaseActivity {

    @BindView(R.id.folder_list)
    PullToRefreshSwipeListView listView;
    @BindView(R.id.tips)
    TextView tipsView;

    private FolderAdapter adapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_move_file);
        setMiddleTitle(getString(R.string.file_move));
        //setRightText(R.string.move);
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

    protected class FolderAdapter extends AbsSwipeAdapter<Void, FolderAdapter.Holder> {

        public FolderAdapter(Context context, PullToRefreshSwipeListView listView) {
            super(context, listView);
        }

        @Override
        protected void setViewContent(Holder holder, Void bean, int position) {

            //TODO
        }

        @Override
        protected View createContentView(int position) {
            return LayoutInflater.from(mContext).inflate(R.layout.layout_move_file_item, null);
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
