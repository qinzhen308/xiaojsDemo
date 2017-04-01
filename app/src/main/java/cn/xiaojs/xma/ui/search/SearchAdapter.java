package cn.xiaojs.xma.ui.search;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;

/**
 * Created by maxiaobao on 2017/4/1.
 */

public class SearchAdapter<T,B extends BaseHolder> extends AbsSwipeAdapter<T,B> {

    public String keyWord;

    public SearchAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    public void setKeyWord(String key) {
        keyWord = key;
    }

    public void requery() {
        reset();
        notifyDataSetChanged();
        request();
    }

    @Override
    protected void setEmptyLayoutParams(View view, RelativeLayout.LayoutParams params) {
        super.setEmptyLayoutParams(view,params);

        ImageView emptyImgView = (ImageView) view.findViewById(R.id.empty_image);
        emptyImgView.setImageResource(R.drawable.ic_search_empty);

        TextView emptyDescView = (TextView) view.findViewById(R.id.empty_desc);
        emptyDescView.setText(R.string.search_empty_tips);
        emptyDescView.setVisibility(View.VISIBLE);


    }

    @Override
    protected void setFailedLayoutParams(View view, RelativeLayout.LayoutParams params) {
        super.setFailedLayoutParams(view, params);

        ImageView emptyImgView = (ImageView) view.findViewById(R.id.empty_image);
        emptyImgView.setImageResource(R.drawable.ic_search_empty);

        TextView emptyDescView = (TextView) view.findViewById(R.id.empty_desc);
        emptyDescView.setText(R.string.search_empty_tips);
        emptyDescView.setVisibility(View.VISIBLE);

        View click = view.findViewById(R.id.empty_click);
        if (click != null) {
            click.setVisibility(View.GONE);
        }
    }

    @Override
    protected void setViewContent(B holder, T bean, int position) {

    }

    @Override
    protected View createContentView(int position) {
        return null;
    }

    @Override
    protected B initHolder(View view) {
        return null;
    }

    @Override
    protected void doRequest() {

    }
}
