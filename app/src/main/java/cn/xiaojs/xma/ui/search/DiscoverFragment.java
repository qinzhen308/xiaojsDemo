package cn.xiaojs.xma.ui.search;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.stateview.AppLoadState2;
import cn.xiaojs.xma.common.pageload.stateview.LoadStateListener;
import cn.xiaojs.xma.common.pageload.stateview.LoadStatusViewDecoratee;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.base.CommonRVAdapter;
import cn.xiaojs.xma.ui.widget.LoadingView;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.JsInvokeNativeInterface;

/**
 * Created by Paul Z on 2017/10/30.
 * 首页--发现（原全局搜索）
 */
public class DiscoverFragment extends BaseFragment {

    @BindView(R.id.search_input)
    EditText mInput;
    @BindView(R.id.search_ok)
    TextView searchOk;
    @BindView(R.id.tab1)
    RadioButton tab1;
    @BindView(R.id.tab2)
    RadioButton tab2;
    @BindView(R.id.tab3)
    RadioButton tab3;
    @BindView(R.id.tab4)
    RadioButton tab4;
    @BindView(R.id.tab5)
    RadioButton tab5;
    @BindView(R.id.tab_bar)
    RadioGroup tabBar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindView(R.id.webview)
    WebView webView;
    @BindView(R.id.layout_first_content)
    View layoutFirstContent;
    @BindView(R.id.title_btn)
    View titleBtn;
    @BindView(R.id.layout_content)
    View layoutContent;

    @BindView(R.id.tab_recommend_1)
    TextView tabRecommend1;
    @BindView(R.id.tab_recommend_2)
    TextView tabRecommend2;
    @BindView(R.id.tab_recommend_3)
    TextView tabRecommend3;
    @BindView(R.id.tab_recommend_4)
    TextView tabRecommend4;

    @BindView(R.id.web_loading_view)
    LoadingView webLoadingView;

    CommonRVAdapter mAdapter;
    private final static int BEGIN_SEARCH=0xff;
    private final static int MAX_PER_PAGE=10;

    String typeName=Social.SearchType.ALL;
    String keywords;

    DataPageLoader<SearchResultV2,CollectionResult<SearchResultV2>> dataPageLoader;
    LoadStatusViewDecoratee stateView;
    AppLoadState2 appLoadState;



    private void initPageLoad(){
        dataPageLoader=new DataPageLoader<SearchResultV2,CollectionResult<SearchResultV2>>() {
            PageChangeInRecyclerView pageChangeInRecyclerView;
            @Override
            public void onRequst(int page) {
                if(TextUtils.isEmpty(keywords)){
                    isLoading=false;
                    return;
                }
                stateView.change(LoadStateListener.STATE_LOADING,"");
                searchRequest(page);
            }

            @Override
            public List<SearchResultV2> adaptData(CollectionResult<SearchResultV2> object) {
                if(object==null)return new ArrayList<>();
                return object.results;
            }

            @Override
            public void onSuccess(List<SearchResultV2> curPage, List<SearchResultV2> all) {
                pageChangeInRecyclerView.completeLoading();
                if(ArrayUtil.isEmpty(all)){
                    appLoadState.setEmptyImageAndTip(R.drawable.img_discover_no_data,"什么也没找到，换个词试试");
                    stateView.change(LoadStateListener.STATE_ALL_EMPTY,"");
                    bindData(new ArrayList<SearchResultV2>());
                }else {
                    stateView.change(LoadStateListener.STATE_NORMAL,"");
                    bindData(all);
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                pageChangeInRecyclerView.completeLoading();
                stateView.change(LoadStateListener.STATE_LOADING_ERROR,"");
                bindData(new ArrayList<SearchResultV2>());
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView=new PageChangeInRecyclerView(recyclerview,this);
            }
        };
    }

    private void toSearch() {
        handler.removeMessages(BEGIN_SEARCH);
        String query = mInput.getText().toString();
        Message msg=new Message();
        msg.what=BEGIN_SEARCH;
        msg.obj=query;
        handler.sendMessageDelayed(msg,300);
    }

    @OnClick({ R.id.search_ok,R.id.title_btn,R.id.tab_recommend_1,R.id.tab_recommend_2,R.id.tab_recommend_3,R.id.tab_recommend_4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_ok:
//                mInput.setText("");
                exitSearch();
                break;
            case R.id.title_btn:
                beginSearch(Social.SearchType.ALL);
                break;
            case R.id.tab_recommend_1:
                beginSearch(Social.SearchType.CLASS);
                break;
            case R.id.tab_recommend_2:
                beginSearch(Social.SearchType.COURSE);
                break;
            case R.id.tab_recommend_3:
                beginSearch(Social.SearchType.PERSON);
                break;
            case R.id.tab_recommend_4:
                beginSearch(Social.SearchType.ORGANIZATION);
                break;
        }
    }

    private void beginSearch(String typeName){
        this.typeName=typeName;
        layoutFirstContent.setVisibility(View.GONE);
        tabBar.setVisibility(View.VISIBLE);
        layoutContent.setVisibility(View.VISIBLE);
        mInput.setVisibility(View.VISIBLE);
        titleBtn.setVisibility(View.GONE);
        searchOk.setVisibility(View.VISIBLE);
        switch (typeName){
            case Social.SearchType.ALL:
                tabBar.check(R.id.tab1);
                break;
            case Social.SearchType.CLASS:
                tabBar.check(R.id.tab2);
                break;
            case Social.SearchType.COURSE:
                tabBar.check(R.id.tab3);
                break;
            case Social.SearchType.PERSON:
                tabBar.check(R.id.tab4);
                break;
            case Social.SearchType.ORGANIZATION:
                tabBar.check(R.id.tab5);
                break;
        }
    }
    private void exitSearch(){
        searchOk.setVisibility(View.GONE);
        layoutFirstContent.setVisibility(View.VISIBLE);
        tabBar.setVisibility(View.GONE);
        layoutContent.setVisibility(View.GONE);
        mInput.setVisibility(View.GONE);
        titleBtn.setVisibility(View.VISIBLE);
        mInput.setText("");
    }

    private void initWebView(){
        WebSettings settings=webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsInvokeNativeInterface(getActivity(),webView),"android");
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Logger.d("---qz----"+consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

            }
        });
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                webLoadingView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webLoadingView.setVisibility(View.GONE);
            }
        });
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(XiaojsConfig.DEBUG){
                Logger.d("qz--handleMessage---what="+msg.what+"---key="+msg.obj);
            }
            if(msg.what==BEGIN_SEARCH){
                String key=msg.obj.toString();
                if(TextUtils.isEmpty(key)){
                    keywords=key;
                    bindData(new ArrayList<SearchResultV2>());
                }else {
                    keywords=key;
                    dataPageLoader.refresh();
                }
            }
        }
    };

    ServiceRequest mRequest;
    public void cancelRequst(){
        if(mRequest==null)return;
        mRequest.cancelRequest();
    }
    private void searchRequest(int page){
        cancelRequst();
        if(TextUtils.isEmpty(keywords)){
            return;
        }
        mRequest=SearchManager.search(getActivity(),typeName,keywords,page,MAX_PER_PAGE,dataPageLoader);
    }

    private void bindData(List<SearchResultV2> data){
        mAdapter.setList(data);
        mAdapter.notifyDataSetChanged();
        if(TextUtils.isEmpty(keywords)){
//            appLoadState.setEmptyImageAndTip(R.drawable.img_discover_default,"涨知识，来这里！好多大牛和课程");
            appLoadState.setEmptyImageAndTip(R.drawable.transparent,"");
            stateView.change(LoadStateListener.STATE_ALL_EMPTY,"");
        }
    }

    @Override
    protected View getContentView() {
        View v=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discover,null);
        appLoadState=new AppLoadState2(getActivity(),(ViewGroup) v.findViewById(R.id.load_state_container));
//        appLoadState.setEmptyImageAndTip(R.drawable.img_discover_default,"涨知识，来这里！好多大牛和课程");
        appLoadState.setEmptyImageAndTip(R.drawable.transparent,"");
        stateView=new LoadStatusViewDecoratee(appLoadState);
        stateView.change(LoadStateListener.STATE_ALL_EMPTY,"");
        return v;
    }

    @Override
    protected void init() {
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter=new CommonRVAdapter(recyclerview);
        recyclerview.setAdapter(mAdapter);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toSearch();
            }
        });

        tabBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.tab1:
                        typeName= Social.SearchType.ALL;
                        break;
                    case R.id.tab2:
                        typeName= Social.SearchType.CLASS;
                        break;
                    case R.id.tab3:
                        typeName= Social.SearchType.COURSE;
                        break;
                    case R.id.tab4:
                        typeName= Social.SearchType.PERSON;
                        break;
                    case R.id.tab5:
                        typeName= Social.SearchType.ORGANIZATION;
                        break;
                }
                if(!TextUtils.isEmpty(keywords)){
                    dataPageLoader.refresh();
                }
            }
        });
        initPageLoad();
        initWebView();
        webView.loadUrl(ApiManager.getDiscoverHomeUrl());
    }

    @Override
    protected int registerDataChangeListenerType() {
        return SimpleDataChangeListener.FOLLOW_USER;
    }

    @Override
    protected void onDataChanged() {
        dataPageLoader.refresh();
    }
}
