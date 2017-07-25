package cn.xiaojs.xma.ui.recordlesson;

import android.app.Activity;
import android.content.Intent;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.grade.ImportVideoActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.ui.recordlesson.util.RecordLessonHelper;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/7/20.
 */

public class AddLessonDirActivity extends BaseActivity {
    public static final String EXTRA_KEY_DIRS="extra_key_dirs";
    public static final String EXTRA_KEY_NEW_LESSON="extra_key_new_lesson";
    public static final String EXTRA_KEY_LESSON_POSITION="extra_key_lesson_position";
    public static final int REQUEST_CODE_ADD=32;
    public static final int REQUEST_CODE_EDIT=31;

    private final int MAX_NAME_CHAR = 50;


    @BindView(R.id.et_directory_name)
    EditTextDel etDirectoryName;
    @BindView(R.id.btn_relevance_video)
    TextView btnRelevanceVideo;
    @BindView(R.id.btn_belong_to_dir)
    TextView tvBelongToDir;
    ArrayList<RLDirectory> dirs;

    private int selectedDirPostion=-1;
    private int lessonPosition=-1;
    RLLesson lesson;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_add_lesson_dir);
        setMiddleTitle(R.string.add_new_lesson_dir);
        setRightText(R.string.finish);
        etDirectoryName.setForbidEnterChar(true);
        etDirectoryName.setHint(getString(R.string.live_lesson_name_hint, MAX_NAME_CHAR));
        etDirectoryName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_NAME_CHAR)});
        dirs=(ArrayList<RLDirectory>) getIntent().getSerializableExtra(EXTRA_KEY_DIRS);
        initLessonInfo();
    }

    private void initLessonInfo(){
        Intent intent=getIntent();
        if(!intent.hasExtra(EXTRA_KEY_NEW_LESSON)){
            lesson=new RLLesson();
            return;
        }
        selectedDirPostion=intent.getIntExtra(SelectDirectoryActivity.EXTRA_KEY_SELETED_POSITION,-1);
        lessonPosition=intent.getIntExtra(EXTRA_KEY_LESSON_POSITION,-1);
        lesson=(RLLesson) intent.getSerializableExtra(EXTRA_KEY_NEW_LESSON);
        etDirectoryName.setText(lesson.name);
        btnRelevanceVideo.setText(lesson.videoName);
        tvBelongToDir.setText(dirs.get(selectedDirPostion).name);
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
        Intent i = new Intent(this, ImportVideoActivity.class);
        i.putExtra(ImportVideoActivity.EXTRA_CHOICE_MODE, AbsListView.CHOICE_MODE_SINGLE);
        i.putExtra(ImportVideoActivity.EXTRA_TITLE,getString(R.string.choice_inner_video));
        i.putExtra(ImportVideoActivity.EXTRA_ALREADY_CHOICE_DATA,RecordLessonHelper.getIds(dirs));
        startActivityForResult(i,ImportVideoActivity.REQUEST_CODE);
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
        if(TextUtils.isEmpty(lesson.videoId)){
            ToastUtil.showToast(getApplicationContext(),"请关联视频");
            return;
        }

        lesson.name=name;

        // TODO: 2017/7/20 判断绑定视频
        Intent intent=new Intent();
        intent.putExtra(SelectDirectoryActivity.EXTRA_KEY_SELETED_POSITION,selectedDirPostion);
        intent.putExtra(EXTRA_KEY_LESSON_POSITION,lessonPosition);
        intent.putExtra(EXTRA_KEY_NEW_LESSON,lesson);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void updateLesson(LibDoc docs){
        if(docs==null){
            ToastUtil.showToast(getApplicationContext(),"选择视频失败");
            return;
        }
        RLLesson duplicationItem= RecordLessonHelper.isDuplication(dirs,docs);
        if(duplicationItem!=null){
            ToastUtil.showToast(getApplicationContext(),"该视频已经在《"+duplicationItem.name+"》中绑定");
            return;
        }
        if(etDirectoryName.getText().toString().trim().length()==0){
            lesson.name=docs.name;
            etDirectoryName.setText(lesson.name);
        }
        lesson.videoId=docs.id;
        lesson.videoName=docs.name;
        btnRelevanceVideo.setText(lesson.videoName);
    }


    public static void invoke(Activity context, ArrayList<RLDirectory> dirs){
        Intent intent=new Intent(context,AddLessonDirActivity.class);
        intent.putExtra(EXTRA_KEY_DIRS,dirs);
        context.startActivityForResult(intent,REQUEST_CODE_ADD);
    }

    public static void invoke(Activity context, ArrayList<RLDirectory> dirs,RLLesson editLesson,int editDirPosition,int editLessonPosition){
        Intent intent=new Intent(context,AddLessonDirActivity.class);
        intent.putExtra(EXTRA_KEY_DIRS,dirs);
        if(editLesson!=null){
            intent.putExtra(SelectDirectoryActivity.EXTRA_KEY_SELETED_POSITION,editDirPosition);
            intent.putExtra(EXTRA_KEY_LESSON_POSITION,editLessonPosition);
            intent.putExtra(EXTRA_KEY_NEW_LESSON,editLesson);
        }
        context.startActivityForResult(intent,REQUEST_CODE_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK==resultCode){
            if(requestCode==SelectDirectoryActivity.REQUEST_CODE){
                selectedDirPostion=data.getIntExtra(SelectDirectoryActivity.EXTRA_KEY_SELETED_POSITION,SelectDirectoryActivity.SELECTED_POSITION_NONE);
                tvBelongToDir.setText(dirs.get(selectedDirPostion).name);
            }else if(requestCode==ImportVideoActivity.REQUEST_CODE){
                updateLesson((LibDoc) data.getSerializableExtra(ImportVideoActivity.EXTRA_CHOICE_DATA));
            }
        }
    }
}
