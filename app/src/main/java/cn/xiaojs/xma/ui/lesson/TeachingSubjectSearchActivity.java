package cn.xiaojs.xma.ui.lesson;

/**
 * Created by Paul Z on 2017/5/11.
 *
 * 搜索教学能力
 */
import android.content.Intent;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.CategoriesManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.CompetencyParams;
import cn.xiaojs.xma.model.account.CompetencySubject;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class TeachingSubjectSearchActivity extends BaseActivity {

    @BindView(R.id.search_input)
    EditText mInput;
    @BindView(R.id.search_ok)
    TextView btnSearchOk;

    @BindView(R.id.listview)
    PullToRefreshSwipeListView mListView;
    TeachingSubjectSearchAdapter2 mAdapter;

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
        mAdapter=new TeachingSubjectSearchAdapter2(this,mListView);
        mListView.setAdapter(mAdapter);
        mListView.enableLeftSwipe(false);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mAdapter.setOnSelectedListener(new TeachingSubjectSearchAdapter2.OnSelectedListener() {
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
//                    Toast.makeText(getApplicationContext(),mAdapter.getSelectedItem().toString(),Toast.LENGTH_LONG).show();

                }else {
                    if(mAdapter.getSelectedPosition()==0){
                        createSubject(mAdapter.getSelectedItem());
                    }else {
                        finishWithResult(mAdapter.getSelectedItem());
                    }
                }
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
        handler.sendMessageDelayed(msg,500);
//        List<Object> list=new ArrayList<>();
//        if(!TextUtils.isEmpty(query)){
//            list.add(query);
//        }
//        mAdapter.setList(list);
//        mAdapter.notifyDataSetChanged();
    }





//    private void doRequest(final String key){
//        if(TextUtils.isEmpty(key)||key.trim().length()==0){
//            mAdapter.clear();
//            return;
//        }
//        CategoriesManager.searchSubjects(this,key ,, new APIServiceCallback<List<CSubject>>() {
//            @Override
//            public void onSuccess(List<CSubject> object) {
//                updateDisplay(key,object);
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//                updateDisplay(key,null);
//            }
//        });
//
//    }
//


    private void finishWithResult(final CSubject subject) {

        if (subject == null) {
            finish();
            return;
        }

        CompetencyParams params = new CompetencyParams();
        params.setSubject(subject.getId());

        showProgress(false);
        AccountDataManager.requestClaimCompetency(this, params, new APIServiceCallback<CompetencySubject>() {
            @Override
            public void onSuccess(CompetencySubject object) {

                cancelProgress();
                Toast.makeText(TeachingSubjectSearchActivity.this, R.string.claim_succ, Toast.LENGTH_SHORT).show();
                //SecurityManager.updatePermission(TeachingSubjectActivity.this, Su.Permission.COURSE_OPEN_CREATE, true);
                AccountDataManager.setTeacher(TeachingSubjectSearchActivity.this, true);

                //更新alias and tags
                AccountDataManager.saveAliaTags(getApplicationContext(), object.aliasAndTags);
                AccountDataManager.submitAliaTags(getApplicationContext());
                //更新教学能力
                AccountDataManager.addAbility(getApplicationContext(), subject.getName());


                Intent intent = new Intent();
                intent.putExtra(CourseConstant.KEY_SUBJECT, subject);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(TeachingSubjectSearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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
//        String regEx="[^(a-zA-Z0-9\\u4e00-\\u9fa5)]";
//        String regEx="^[a-zA-Z0-9\u4E00-\u9FA5]+$";
        String regEx="[a-zA-Z0-9_\u4e00-\u9fa5]+";
        Pattern pattern=Pattern.compile(regEx);
        Matcher matcher=pattern.matcher(content);
        if(!matcher.matches()){
            Toast.makeText(this,R.string.subject_input_tip,Toast.LENGTH_LONG).show();
            return false;
        }
        regEx="[^(0-9)]";
        pattern=Pattern.compile(regEx);
        matcher=pattern.matcher(content);
        if(!matcher.matches()){
            Toast.makeText(this,R.string.subject_input_tip,Toast.LENGTH_LONG).show();
            return false;
        }


        return true;
    }


    private void createSubject(final CSubject subject){
        String content=subject.getName();
//        if(!verifyInput(content))return;
        showProgress(false);
        CategoriesManager.addOpenSubject(this, subject.getName(), new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                Intent intent = new Intent();
                intent.putExtra(CourseConstant.KEY_SUBJECT, subject);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(TeachingSubjectSearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
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
                mAdapter.doRequest((String)msg.obj);
            }
        }
    };


}
