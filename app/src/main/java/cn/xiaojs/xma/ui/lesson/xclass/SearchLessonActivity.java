package cn.xiaojs.xma.ui.lesson.xclass;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.orhanobut.logger.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/5/31.
 */

public class SearchLessonActivity extends BaseActivity {


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
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    @BindString(R.string.cancel)
    String btn_text_cancel;

    @BindString(R.string.search)
    String btn_text_search;

    HomeClassAdapter mAdapter;

    private final static int BEGIN_SEARCH=0xff;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_search_lesson);
        mAdapter=new HomeClassAdapter(recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerview.setAdapter(mAdapter);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

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
        handler.removeMessages(BEGIN_SEARCH);
        String query = searchInput.getText().toString();
        Message msg=new Message();
        msg.what=BEGIN_SEARCH;
        msg.obj=query;
        handler.sendMessageDelayed(msg,300);
        if(query.length()>0){
            searchOk.setText(btn_text_search);
        }else {
            searchOk.setText(btn_text_cancel);
        }
    }

    private void request(){
        ToastUtil.showToast(getApplicationContext(),"搜索--关键字："+searchInput.getText().toString());
    }

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

    @Override
    protected void onDestroy() {
        if(handler!=null){
            handler.removeMessages(BEGIN_SEARCH);
        }
        super.onDestroy();
    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(XiaojsConfig.DEBUG){
                Logger.d("qz--handleMessage---what="+msg.what+"---key="+msg.obj);
            }
            if(msg.what==BEGIN_SEARCH){
                request();
            }
        }
    };


}
