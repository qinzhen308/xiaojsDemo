package cn.xiaojs.xma.ui.mine;

import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.ContentManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.contents.Article;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.content_webview)
    WebView contentView;

    @BindView(R.id.error_layout)
    RelativeLayout errorLayout;

    @BindView(R.id.empty_desc)
    TextView emptyView;

    @BindView(R.id.empty_desc1)
    TextView emptyView1;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_agreement);
        setMiddleTitle("关于我们");
        loadContent();

    }

    @OnClick({R.id.left_view})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                finish();
                break;
        }
    }

    private void loadContent() {

        showProgress(true);
        ContentManager.getArticle(this, "", new APIServiceCallback<Article>() {
            @Override
            public void onSuccess(Article article) {

                cancelProgress();

                if (article == null || article.body== null ){
                    showError();
                    return;
                }
                contentView.loadData(article.body.text,"text/html","UTF-8");
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                showError();

            }
        });
    }

    private void showError() {
        emptyView.setVisibility(View.VISIBLE);
        emptyView1.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
    }
}
