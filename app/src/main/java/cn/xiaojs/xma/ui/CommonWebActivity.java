package cn.xiaojs.xma.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.ContentManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.contents.Article;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.JsInvokeNativeInterface;

public class CommonWebActivity extends BaseActivity {

    public final static String EXTRA_TITLE="extra_title";
    public final static String EXTRA_URL="extra_url";

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
        String title=getIntent().getStringExtra(EXTRA_TITLE);
        if(title==null){
            needHeader(false);
        }else {
            setMiddleTitle(title);
        }
        initWebView();
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

    private void initWebView(){
        WebSettings webSettings=contentView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        contentView.addJavascriptInterface(new JsInvokeNativeInterface(this,contentView),"android");
    }

    private void loadContent() {
        String url=getIntent().getStringExtra(EXTRA_URL);
        contentView.loadUrl(url);
    }

    private void showError() {
        emptyView.setVisibility(View.VISIBLE);
        emptyView1.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.VISIBLE);
    }


    public static void invoke(Context contex,String title,String url){
        Intent intent =new Intent(contex,CommonWebActivity.class);
        intent.putExtra(EXTRA_TITLE,title);
        intent.putExtra(EXTRA_URL,url);
        contex.startActivity(intent);
    }

}