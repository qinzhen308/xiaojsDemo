package cn.xiaojs.xma.ui.recordlesson;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.recordedlesson.RLessonDetail;
import cn.xiaojs.xma.model.recordedlesson.Section;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.grade.ImportVideoActivity;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;
import cn.xiaojs.xma.ui.recordlesson.model.RLLesson;
import cn.xiaojs.xma.ui.recordlesson.util.RecordLessonHelper;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.ObjectUtil;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/7/18.
 */

public class RecordedLessonActivity extends BaseActivity {

    public static final String EXTRA_LESSON_ID="extra_lesson_id";


    @BindView(R.id.listview)
    DragSortListView listview;
    @BindView(R.id.next_btn)
    View nextBtn;
    RecordedLessonAdapter adapter;

    View footer;

    boolean isEditMode = false;
    @BindView(R.id.layout_edit_bar)
    LinearLayout layoutEditBar;

    ArrayList srcList;
    @BindView(R.id.all_check_view)
    TextView allCheckView;

    private String lessonId;

    RLessonDetail recreateData;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_recorded_lesson);
        needHeader(true);
        setRightText(R.string.manage_it);
        setMiddleTitle(R.string.record_lesson_directory);
        lessonId=getIntent().getStringExtra(EXTRA_LESSON_ID);
        initView();
        initData();
    }


    private void initView() {
        footer = LayoutInflater.from(this).inflate(R.layout.layout_recorded_lesson_footer, null);
        footer.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importVideo();
            }
        });
        listview.setChoiceMode(ListView.CHOICE_MODE_NONE);
        listview.setDragEnabled(false);
        adapter = new RecordedLessonAdapter(this, listview);
        listview.addFooterView(footer);
        listview.setAdapter(adapter);
        listview.setDragEnabled(false);
//        DragSortController manager=new DragSortController(mListview);
//        manager.setDragInitMode(DragSortController.ON_LONG_PRESS);
//        mListview.setFloatViewManager(manager);

//        listview.setDropListener(onDrop);
        listview.setDragSortListener(dragSortListener);
    }

    private DragSortListView.DragSortListener dragSortListener = new DragSortListView.DragSortListener() {
        @Override
        public void drag(int from, int to) {
            Logger.d("----dragsortlistview----from=" + from + ",to=" + to);
            if (from == to) {

            }
        }

        @Override
        public void drop(int from, int to) {
            Logger.d("----dragsortlistview----drop----from=" + from + ",to" + to);
            adapter.onDrop(from, to);
        }

        @Override
        public void remove(int which) {

        }

        @Override
        public void startDrag(int which) {
            Logger.d("----dragsortlistview----startDrag----which=" + which);
            adapter.onStartDrag(which);
        }
    };


    private void loadRecreateData(){
        showProgress(true);
        LessonDataManager.getRecordedCourse(this, lessonId, new APIServiceCallback<RLessonDetail>() {
            @Override
            public void onSuccess(RLessonDetail object) {
                cancelProgress();
                recreateData=object;
                if (object == null) {
                    showEmptyView("空");
                }else{
                    handleRecreateData();
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                showFailedView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadRecreateData();
                    }
                });
            }
        });

    }


    @OnClick({R.id.next_btn, R.id.right_view, R.id.left_image,
            R.id.all_check_view, R.id.btn_delete, R.id.add_dir, R.id.btn_lesson})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next_btn:
                next();
                break;
            case R.id.right_view:
                if (isEditMode) {
                    changeMode(false);
                    saveManaged();
                } else {
                    changeMode(true);
                    readyToManage();
                }
                break;
            case R.id.left_image:
                if (isEditMode) {
                    changeMode(false);
                    cancelManaged();
                }else {
                    finish();
                }
                break;
            case R.id.all_check_view:
                allCheck(!allCheckView.isSelected());
                break;
            case R.id.btn_delete:
                deleteSelected();
                break;
            case R.id.add_dir:
                addNewDir();
                break;
            case R.id.btn_lesson:
                addNewLesson();
                break;
        }
    }

    private void next(){
        if(ArrayUtil.isEmpty(srcList)){
            ToastUtil.showToast(getApplicationContext(), "请先创建录播课目录");
            return;
        }
        CreateRecordedLessonActivity.invoke(this,(ArrayList<RLDirectory>)(Object)srcList,recreateData);
    }

    private void readyToManage() {
        if (adapter.getList() == null) {
            return;
        }
        adapter.setList(ObjectUtil.deepClone((ArrayList) adapter.getList()));
        adapter.setEditMode(true);
    }

    private void saveManaged() {
        srcList = (ArrayList) adapter.getList();
        adapter.setEditMode(false);
    }

    private void cancelManaged() {
        adapter.setList(srcList);
        adapter.setEditMode(false);
    }

    @Override
    public void onBackPressed() {
        if (isEditMode) {
            changeMode(false);
            cancelManaged();
            return;
        }
        super.onBackPressed();
    }


    private void initData() {
        if(TextUtils.isEmpty(lessonId)){
            ArrayList<LibDoc> doc=(ArrayList<LibDoc>)getIntent().getSerializableExtra(ImportVideoActivity.EXTRA_CHOICE_DATA);
            buildDirs(doc);
        }else {
            loadRecreateData();
        }
    }

    private void buildDirs(ArrayList<LibDoc> docs){
        if(ArrayUtil.isEmpty(docs)){
            ToastUtil.showToast(getApplicationContext(),"导入失败");
            return;
        }
        if(srcList==null){
            srcList=new ArrayList();
        }
        RLDirectory dir=new RLDirectory(docs.get(0).name);
        for(int i=0,size=docs.size();i<size;i++){
            LibDoc doc=docs.get(i);
            RLLesson child=new RLLesson();
            child.name=doc.name;
            child.setLibDoc(doc);
            dir.addChild(child);
        }
        srcList.add(dir);
        adapter.setList(srcList);
        adapter.notifyDataSetChanged();
    }

    private void handleRecreateData(){
        if(srcList==null){
            srcList=new ArrayList();
        }
        if(ArrayUtil.isEmpty(recreateData.sections)){
            return;
        }
        for(Section sp:recreateData.sections){
            RLDirectory dir=new RLDirectory(sp.name);
            srcList.add(dir);
            if(ArrayUtil.isEmpty(sp.sections)){
                continue;
            }
            for(Section sc:sp.sections){
                RLLesson lesson=new RLLesson();
                lesson.name=sc.name;
                lesson.videoName=sc.document.name;
                lesson.videoId=sc.document.id;
                lesson.videoMimeType=sc.document.mimeType;
                lesson.videoKey=sc.document.key;
                dir.addChild(lesson);
            }
        }
        adapter.setList(srcList);
        adapter.notifyDataSetChanged();
    }


    private void changeMode(boolean isEditMode) {
        if (this.isEditMode == isEditMode) return;
        this.isEditMode = isEditMode;
        if (isEditMode) {
            setRightText(R.string.finish);
            listview.removeFooterView(footer);
            nextBtn.setVisibility(View.GONE);
            layoutEditBar.setVisibility(View.VISIBLE);
            listview.setDragEnabled(true);
        } else {
            setRightText(R.string.manage_it);
            listview.addFooterView(footer);
            nextBtn.setVisibility(View.VISIBLE);
            layoutEditBar.setVisibility(View.GONE);
            listview.setDragEnabled(false);
            //重置选中状态
            allCheck(false);
        }
    }

    private void importVideo() {
        Intent i = new Intent(this, ImportVideoActivity.class);
        i.putExtra(ImportVideoActivity.EXTRA_TITLE,getString(R.string.import_video_tips));
        i.putExtra(ImportVideoActivity.EXTRA_ALREADY_CHOICE_DATA, RecordLessonHelper.getIds(srcList));
        startActivityForResult(i,ImportVideoActivity.REQUEST_CODE);
    }

    private void allCheck(boolean isChecked) {
        List list = adapter.getList();
        allCheckView.setSelected(isChecked);
        if (ArrayUtil.isEmpty(list)) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof RLDirectory) {
                ((RLDirectory) o).setChecked(isChecked);
            }
        }
        adapter.notifyDataSetChanged();
    }


    private void deleteSelected(){
        if(adapter.getList()==null){
            ToastUtil.showToast(getApplicationContext(),"已经删空了...");
            return;
        }
        Iterator iterator=adapter.getList().iterator();
        while (iterator.hasNext()){
            Object o=iterator.next();
            if(o instanceof RLDirectory){
                RLDirectory dir=(RLDirectory) o;
                if(dir.isChecked()){
                    iterator.remove();
                }else {
                    dir.removeChecked();
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void addNewDir(){
        final CommonDialog dialog=new CommonDialog(this);
        dialog.setTitle(R.string.add_new_directory);
        final EditText editText=new EditText(this);
        FrameLayout.LayoutParams lp=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin=getResources().getDimensionPixelSize(R.dimen.px20);
        lp.topMargin=getResources().getDimensionPixelSize(R.dimen.px20);
        editText.setLayoutParams(lp);
        editText.setHint(R.string.add_new_dir_tip);
        editText.setLines(1);
        editText.setTextColor(getResources().getColor(R.color.font_black));
        editText.setBackgroundResource(R.drawable.common_search_bg);
        editText.setGravity(Gravity.LEFT|Gravity.TOP);
        int padding=getResources().getDimensionPixelSize(R.dimen.px10);
        editText.setPadding(padding,padding,padding,padding);
        editText.setHintTextColor(getResources().getColor(R.color.font_gray));
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_28px));
        dialog.setCustomView(editText);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                String dirName=editText.getText().toString().trim();
                if(dirName.length()==0){
                    ToastUtil.showToast(getApplicationContext(), R.string.add_new_dir_not_input_tip);
                    return;
                }
                adapter.addDir(new RLDirectory(dirName));
                dialog.dismiss();

            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addNewLesson(){
        List dirs=adapter.getList();
        if(ArrayUtil.isEmpty(dirs)){
            ToastUtil.showToast(getApplicationContext(), R.string.please_add_dir_first);
            return;
        }
        AddLessonDirActivity.invoke(this,(ArrayList<RLDirectory>)(Object)dirs);


    }

    public static void invoke(Context context) {
        Intent intent = new Intent(context, RecordedLessonActivity.class);
        context.startActivity(intent);
    }

    public static void invoke(Context context,String rLessonId) {
        Intent intent = new Intent(context, RecordedLessonActivity.class);
        if(!TextUtils.isEmpty(rLessonId)){
            intent.putExtra(EXTRA_LESSON_ID,rLessonId);
        }
        context.startActivity(intent);
    }

    public static void invoke(Context context,Intent data) {
        Intent intent = new Intent(context, RecordedLessonActivity.class);
        intent.fillIn(data,Intent.FILL_IN_DATA);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(RESULT_OK==resultCode){
            if(requestCode==AddLessonDirActivity.REQUEST_CODE_ADD){
                int selectedDirPostion=data.getIntExtra(SelectDirectoryActivity.EXTRA_KEY_SELETED_POSITION,SelectDirectoryActivity.SELECTED_POSITION_NONE);
                adapter.addLesson(selectedDirPostion,(RLLesson) data.getSerializableExtra(AddLessonDirActivity.EXTRA_KEY_NEW_LESSON));
            }else if(requestCode==AddLessonDirActivity.REQUEST_CODE_EDIT){
                int selectedDirPostion=data.getIntExtra(SelectDirectoryActivity.EXTRA_KEY_SELETED_POSITION,SelectDirectoryActivity.SELECTED_POSITION_NONE);
                int selectedLessonPostion=data.getIntExtra(AddLessonDirActivity.EXTRA_KEY_LESSON_POSITION,SelectDirectoryActivity.SELECTED_POSITION_NONE);
                RLLesson lesson=(RLLesson) data.getSerializableExtra(AddLessonDirActivity.EXTRA_KEY_NEW_LESSON);
                adapter.editLessonItem(selectedDirPostion,selectedLessonPostion,lesson);
            }else if(requestCode==ImportVideoActivity.REQUEST_CODE){//绑定视频，code要改
                buildDirs((ArrayList<LibDoc>) data.getSerializableExtra(ImportVideoActivity.EXTRA_CHOICE_DATA));
            }
        }
    }


}
