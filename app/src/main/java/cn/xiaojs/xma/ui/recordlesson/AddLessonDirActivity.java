package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.content.Intent;
import android.text.InputFilter;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.grade.ImportVideoActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/7/20.
 */

public class AddLessonDirActivity extends BaseActivity {
    public static final String EXTRA_KEY_DIRS="extra_key_dirs";
    public static final String EXTRA_KEY_NEW_LESSON="extra_key_new_lesson";
    public static final int REQUEST_CODE=32;

    private final int MAX_NAME_CHAR = 50;


    @BindView(R.id.et_directory_name)
    EditTextDel etDirectoryName;
    @BindView(R.id.btn_relevance_video)
    TextView btnRelevanceVideo;
    @BindView(R.id.btn_belong_to_dir)
    TextView tvBelongToDir;
    ArrayList<RLDirectory> dirs;

    private int selectedDirPostion=-1;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_add_lesson_dir);
        setMiddleTitle(R.string.add_new_lesson_dir);
        setRightText(R.string.finish);
        etDirectoryName.setForbidEnterChar(true);
        etDirectoryName.setHint(getString(R.string.live_lesson_name_hint, MAX_NAME_CHAR));
        etDirectoryName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_NAME_CHAR)});
        dirs=(ArrayList<RLDirectory>) getIntent().getSerializableExtra(EXTRA_KEY_DIRS);
    }


    @OnClick({R.id.btn_relevance_video, R.id.btn_belong_to_dir,R.id.left_image,R.id.right_view})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_relevance_video:
                bindVideo();
                break;
            case R.id.btn_belong_to_dir:
                bindDir();
                break;
            case R.id.left_image:
                finish();
                break;
            case R.id.right_view:
                save();
                break;
        }
    }

    private void bindVideo(){
        // TODO: 2017/7/20 绑定视频 看是否必选
        Intent i = new Intent(this, ImportVideoActivity.class);
        i.putExtra(ImportVideoActivity.EXTRA_CHOICE_MODE, AbsListView.CHOICE_MODE_SINGLE);
        i.putExtra(ImportVideoActivity.EXTRA_TITLE,getString(R.string.choice_inner_video));
        startActivity(i);
    }

    private void bindDir(){
        SelectDirectoryActivity.invoke(this,dirs,selectedDirPostion);
    }


    private void save(){
        String name=etDirectoryName.getText().toString();
        if(name.length()==0){
            ToastUtil.showToast(getApplicationContext(),"请输入二级目录名称");
            return;
        }

        if(selectedDirPostion<0){
            ToastUtil.showToast(getApplicationContext(),"请选择所属一级目录");
            return;
        }
        RLLesson lesson=new RLLesson(name,"");
        // TODO: 2017/7/20 判断绑定视频
        Intent intent=new Intent();
        intent.putExtra(SelectDirectoryActivity.EXTRA_KEY_SELETED_POSITION,selectedDirPostion);
        intent.putExtra(EXTRA_KEY_NEW_LESSON,lesson);
        setResult(RESULT_OK,intent);
        finish();
    }


    public static void invoke(Activity context, ArrayList<RLDirectory> dirs){
        Intent intent=new Intent(context,AddLessonDirActivity.class);
        intent.putExtra(EXTRA_KEY_DIRS,dirs);
        context.startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK==resultCode){
            if(requestCode==SelectDirectoryActivity.REQUEST_CODE){
                selectedDirPostion=data.getIntExtra(SelectDirectoryActivity.EXTRA_KEY_SELETED_POSITION,SelectDirectoryActivity.SELECTED_POSITION_NONE);
                tvBelongToDir.setText(dirs.get(selectedDirPostion).name);
            }else if(requestCode==1212){//绑定视频，code要改

            }
        }
    }
}
