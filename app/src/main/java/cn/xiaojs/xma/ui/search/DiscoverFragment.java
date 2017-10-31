package cn.xiaojs.xma.ui.search;

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
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.base.CommonRVAdapter;
import cn.xiaojs.xma.util.ArrayUtil;

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
    @BindView(R.id.tab6)
    RadioButton tab6;
    @BindView(R.id.tab_bar)
    RadioGroup tabBar;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

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

    @OnClick({ R.id.search_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_ok:
                mInput.setText("");
                break;
        }
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
    }

    @Override
    protected View getContentView() {
        View v=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_discover,null);
        appLoadState=new AppLoadState2(getActivity(),(ViewGroup) v.findViewById(R.id.load_state_container));
        appLoadState.setEmptyImageAndTip(R.drawable.img_discover_default,"涨知识，来这里！好多大牛和课程");
        stateView=new LoadStatusViewDecoratee(appLoadState);
        stateView.change(LoadStateListener.STATE_ALL_EMPTY,"");
        return v;
    }

    @Override
    protected void init() {
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter=new CommonRVAdapter(recyclerview);
        recyclerview.setAdapter(mAdapter);
        tab2.setVisibility(View.GONE);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(mInput.getText().toString())){
                    searchOk.setVisibility(View.GONE);
                }else {
                    searchOk.setVisibility(View.VISIBLE);
                }
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
                    /*case R.id.tab2:
                        typeName= Social.SearchType.LESSON;
                        break;*/
                    case R.id.tab3:
                        typeName= Social.SearchType.COURSE;

                        break;
                    case R.id.tab4:
                        typeName= Social.SearchType.CLASS;
                        break;
                    case R.id.tab5:
                        typeName= Social.SearchType.PERSON;
                        break;
                    case R.id.tab6:
                        typeName= Social.SearchType.ORGANIZATION;
                        break;
                }
                if(!TextUtils.isEmpty(keywords)){
                    dataPageLoader.refresh();
                }
            }
        });
        initPageLoad();
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
