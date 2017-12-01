package cn.xiaojs.xma.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.JsInvokeNativeInterface;
import cn.xiaojs.xma.util.ShareUtil;

/**
 * Created by Paul Z on 2017/12/1
 * 帮助页面
 */
public class HelpActivity extends BaseActivity {

    public final static String EXTRA_TITLE = "extra_title";
    public final static String EXTRA_URL = "extra_url";
    public final static String EXTRA_SHARE_URL = "extra_share_url";
    public final static String EXTRA_AUTO_TITLE = "extra_auto_title";
    @BindView(R.id.close_btn)
    TextView closeBtn;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.middle_title)
    TextView middleTitle;
    @BindView(R.id.iv_share)
    ImageView ivShare;
    @BindView(R.id.title_bar)
    RelativeLayout titleBar;
    @BindView(R.id.content_webview)
    WebView contentView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;




    private boolean canShare;
    private String share_url;

    boolean isAutoTitle;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_help);
        needHeader(false);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        isAutoTitle = getIntent().getBooleanExtra(EXTRA_AUTO_TITLE, false);
        if (isAutoTitle) {
            middleTitle.setText(title == null ? "" : title);
        } else if (title == null) {
            titleBar.setVisibility(View.GONE);
        } else {
            middleTitle.setText(title);
        }
        canShare = getIntent().getBooleanExtra(EXTRA_SHARE_URL, false);
        if (canShare) {
            ivShare.setVisibility(View.VISIBLE);
        }else {
            ivShare.setVisibility(View.GONE);
        }
        initWebView();
        loadContent();
    }

    @OnClick({R.id.iv_back,R.id.close_btn, R.id.iv_share})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                if(contentView.canGoBack()){
                    contentView.goBack();
                }else {
                    finish();
                }
                break;
            case R.id.close_btn:
                finish();
                break;
            case R.id.iv_share:
                ShareUtil.shareUrlByUmeng(this, getMiddleTitle(), "", share_url);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(contentView.canGoBack()){
            contentView.goBack();
            return;
        }
        super.onBackPressed();
    }

    private void initWebView() {
        WebSettings webSettings = contentView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        contentView.addJavascriptInterface(new JsInvokeNativeInterface(this, contentView), "android");
        contentView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Logger.d("---qz----" + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressBar == null) {
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
                if (isAutoTitle && !TextUtils.isEmpty(title)) {
                    middleTitle.setText(title);
                }
            }

        });
        contentView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                share_url = url;

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(contentView.canGoBack()){
                    closeBtn.setVisibility(View.VISIBLE);
                }else {
                    closeBtn.setVisibility(View.GONE);
                }
            }

        });
    }

    private void loadContent() {
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            contentView.loadUrl(url);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                contentView.loadData(Html.escapeHtml(url), "text/html; charset=UTF-8", null);
            } else {
                contentView.loadData(url, "text/html", "UTF-8");
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




    public static void invoke(Context contex, String title, String url) {
        Intent intent = new Intent(contex, HelpActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        contex.startActivity(intent);
    }


    //包含右上角分享按钮的分享功能
    public static void invoke(Context contex, String title, String url, boolean canShare, boolean autoTitle) {
        Intent intent = new Intent(contex, HelpActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_SHARE_URL, canShare);
        intent.putExtra(EXTRA_AUTO_TITLE, autoTitle);
        contex.startActivity(intent);
    }


}
