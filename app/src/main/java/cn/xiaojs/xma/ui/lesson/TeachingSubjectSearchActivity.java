package cn.xiaojs.xma.ui.lesson;

/**
 * Created by Paul Z on 2017/5/11.
 *
 * 搜索教学能力
 */
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.CategoriesManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class TeachingSubjectSearchActivity extends BaseActivity {

    @BindView(R.id.search_input)
    EditText mInput;
    @BindView(R.id.search_ok)
    TextView btnSearchOk;

    @BindView(R.id.listview)
    ListView mListView;
    TeachingSubjectSearchAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_teaching_subject_search);
        needHeader(false);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //search();
                toSearch();
            }
        });
        initView();
    }

    private void initView(){
        mAdapter=new TeachingSubjectSearchAdapter(this);
        mListView.setAdapter(mAdapter);
        mAdapter.setOnSelectedListener(new TeachingSubjectSearchAdapter.OnSelectedListener() {
            @Override
            public void onSelected(int position) {
                if(position>=0){
                    btnSearchOk.setText(getString(R.string.ok));
                }else {
                    btnSearchOk.setText(getString(R.string.cancel));
                }
            }
        });
    }




    @OnClick({R.id.search_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_ok:
                if(btnSearchOk.getText().toString().equals(R.string.cancel)){
                    Toast.makeText(getApplicationContext(),mAdapter.getSelectedItem().toString(),Toast.LENGTH_LONG).show();
                }else {

                }
                finish();
                break;

        }
    }

    private final static int BEGIN_SEARCH=0xff;

    private void toSearch() {
        handler.removeMessages(BEGIN_SEARCH);
        String query = mInput.getText().toString();
        Message msg=new Message();
        msg.what=BEGIN_SEARCH;
        msg.obj=query;
        handler.sendMessageDelayed(msg,1000);
//        List<Object> list=new ArrayList<>();
//        if(!TextUtils.isEmpty(query)){
//            list.add(query);
//        }
//        mAdapter.setList(list);
//        mAdapter.notifyDataSetChanged();
    }


    private void updateDisplay(String key,List<CSubject> object) {
        List<Object> list=new ArrayList<>();
        list.add(key);
        if(object!=null){
            list.addAll(object);
        }
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
    }


    private void doRequest(final String key){
        if(TextUtils.isEmpty(key)||key.trim().length()==0){
            mAdapter.clear();
            return;
        }
        CategoriesManager.searchSubjects(this,key , new APIServiceCallback<List<CSubject>>() {
            @Override
            public void onSuccess(List<CSubject> object) {
                updateDisplay(key,object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                updateDisplay(key,null);
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
                doRequest((String)msg.obj);
            }
        }
    };


}
