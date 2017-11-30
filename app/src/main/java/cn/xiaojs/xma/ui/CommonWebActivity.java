package cn.xiaojs.xma.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.ContentManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.contents.Article;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.JsInvokeNativeInterface;
import cn.xiaojs.xma.util.ShareUtil;

public class CommonWebActivity extends BaseActivity {

    public final static String EXTRA_TITLE="extra_title";
    public final static String EXTRA_URL="extra_url";
    public final static String EXTRA_SHARE_URL="extra_share_url";
    public final static String EXTRA_AUTO_TITLE="extra_auto_title";

    @BindView(R.id.content_webview)
    WebView contentView;

    @BindView(R.id.error_layout)
    RelativeLayout errorLayout;

    @BindView(R.id.empty_desc)
    TextView emptyView;

    @BindView(R.id.empty_desc1)
    TextView emptyView1;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private boolean canShare;
    private String share_url;

    boolean isAutoTitle;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_common_web);
        String title=getIntent().getStringExtra(EXTRA_TITLE);
        isAutoTitle=getIntent().getBooleanExtra(EXTRA_AUTO_TITLE,false);
        if(isAutoTitle){
            setMiddleTitle(title==null?"":title);
        }else if(title==null){
            needHeader(false);
        }else {
            setMiddleTitle(title);
        }
        canShare=getIntent().getBooleanExtra(EXTRA_SHARE_URL,false);
        if(canShare){
            setRightImage(R.drawable.share_selector);
        }
        initWebView();
        loadContent();
    }

    @OnClick({R.id.left_view,R.id.right_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                finish();
                break;
            case R.id.right_image:
                ShareUtil.shareUrlByUmeng(this,getMiddleTitle(),"",share_url);
                break;
        }
    }

    private void initWebView(){
        WebSettings webSettings=contentView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        contentView.addJavascriptInterface(new JsInvokeNativeInterface(this,contentView),"android");
        contentView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Logger.d("---qz----"+consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(progressBar==null){
                    return;
                }
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if(isAutoTitle&&!TextUtils.isEmpty(title)){
                    setMiddleTitle(title);
                }
            }
        });
        contentView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                share_url=url;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });
    }

    private void loadContent() {
        String url=getIntent().getStringExtra(EXTRA_URL);
        if(URLUtil.isHttpsUrl(url)||URLUtil.isHttpUrl(url)){
            contentView.loadUrl(url);
        }else {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
                contentView.loadData(Html.escapeHtml(url),"text/html; charset=UTF-8", null);
            }else {
                contentView.loadData(url,"text/html", "UTF-8");
            }
        }
    }

    @Override
    protected void onDestroy() {
        contentView.clearHistory();
        contentView.clearCache(true);
        contentView.clearFormData();
        super.onDestroy();
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


    //包含右上角分享按钮的分享功能
    public static void invoke(Context contex,String title,String url,boolean canShare,boolean autoTitle){
        Intent intent =new Intent(contex,CommonWebActivity.class);
        intent.putExtra(EXTRA_TITLE,title);
        intent.putExtra(EXTRA_URL,url);
        intent.putExtra(EXTRA_SHARE_URL,canShare);
        intent.putExtra(EXTRA_AUTO_TITLE,autoTitle);
        contex.startActivity(intent);
    }

}
