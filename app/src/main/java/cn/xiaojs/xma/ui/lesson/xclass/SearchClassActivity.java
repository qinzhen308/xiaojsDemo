package cn.xiaojs.xma.ui.lesson.xclass;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/5/31.
 * 搜索与我相关的班
 */

public class SearchClassActivity extends BaseActivity {


    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.search_input)
    EditTextDel searchInput;
    @BindView(R.id.search_ok)
    TextView searchOk;
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.search_empty)
    FrameLayout searchEmpty;
//    @BindView(R.id.recyclerview)
//    RecyclerView recyclerview;
    @BindView(R.id.listview)
    PullToRefreshSwipeListView
    listview;

    @BindString(R.string.cancel)
    String btn_text_cancel;

    @BindString(R.string.search)
    String btn_text_search;

//    HomeClassAdapter mAdapter;
    ClassAdapter mAdapter;
    private final static int BEGIN_SEARCH=0xff;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_search_class);
        needHeader(false);
//        mAdapter=new HomeClassAdapter(recyclerview);
//        recyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
//        recyclerview.setAdapter(mAdapter);
        mAdapter=new ClassAdapter(this,listview);
        mAdapter.setKeyword("囃");//这个关键字只是为了不让adapter第一次加载出数据
        listview.setAdapter(mAdapter);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = searchInput.getText().toString();
                if(query.length()>0){
                    searchOk.setText(btn_text_search);
                }else {
                    searchOk.setText(btn_text_cancel);
                }
            }
        });
    }



    @OnClick({R.id.back, R.id.search_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                break;
            case R.id.search_ok:
                searchOrCancel(searchOk.getText().toString());
                break;
        }
    }

    private void searchOrCancel(String text){
        if(btn_text_search.equals(text)){
            search();
        }else {
            finish();
        }
    }

    private void search(){
        mAdapter.setKeyword(searchInput.getText().toString());
        mAdapter.refresh();
    }

//    private void request(){
//        ToastUtil.showToast(getApplicationContext(),"搜索--关键字："+searchInput.getText().toString());
//    }

    private boolean verifyInput(String content){
        if(content.length()<2){
            Toast.makeText(this,"不能少于2个字符",Toast.LENGTH_LONG).show();
            return false;
        }

        if(content.length()>8){
            Toast.makeText(this,"不能多余8个字符",Toast.LENGTH_LONG).show();
            return false;
        }
        String regEx="[a-zA-Z0-9_\u4e00-\u9fa5]+";
        Pattern pattern=Pattern.compile(regEx);
        Matcher matcher=pattern.matcher(content);
        if(!matcher.matches()){
            Toast.makeText(this,R.string.subject_input_tip,Toast.LENGTH_LONG).show();
            return false;
        }
        regEx="[(0-9)]+";
        pattern=Pattern.compile(regEx);
        matcher=pattern.matcher(content);
        if(matcher.matches()){
            Toast.makeText(this,R.string.subject_input_tip2,Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }




}
