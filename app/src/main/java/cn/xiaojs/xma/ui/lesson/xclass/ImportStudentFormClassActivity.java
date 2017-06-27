package cn.xiaojs.xma.ui.lesson.xclass;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.AbsStudent;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.CriteriaStudents;
import cn.xiaojs.xma.model.ctl.EnrollImport;
import cn.xiaojs.xma.model.ctl.Students;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.ArrayUtil;

import static cn.xiaojs.xma.ui.message.PostDynamicActivity.EXTRA_CLASS_POS;

public class ImportStudentFormClassActivity extends BaseActivity {

    public static final String EXTRA_IMPORTS = "imports";
    public static final int CODE_ADD_STUDENTS_BATCH = 1233;

    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.lv)
    ListView listView;

    @BindView(R.id.v_empty)
    View emptyView;

    private ChoiceAdapter adapter;
    private String classId;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_share_scope);

        classId = getIntent().getStringExtra(ClassInfoActivity.EXTRA_CLASSID);


        setMiddleTitle(R.string.student_add_from_exist_class);
        tipsContentView.setText(R.string.import_class_student_tips);
        tipsRootView.setVisibility(View.VISIBLE);


        setRightText(R.string.finish);
        setRightTextColor(getResources().getColor(R.color.font_orange));
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        getStudents();

    }


    @OnClick({R.id.left_image, R.id.right_image2, R.id.lesson_creation_tips_close})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image2:
                if (adapter != null) {
                    choiceComplete();
                }
                break;
            case R.id.lesson_creation_tips_close://关闭提醒
                closeCourCreateTips();
                break;
        }

    }

    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }

    private synchronized void updateAdapter(ArrayList<AbsStudent> students) {

        if (students == null || students.size() == 0) {
            showEmptyView();
            return;
        }

        if (adapter == null) {

            adapter = new ChoiceAdapter(this,
                    R.layout.layout_multiple_select_item,
                    R.id.title,
                    students);
            listView.setAdapter(adapter);

            int pos = getIntent().getIntExtra(EXTRA_CLASS_POS, -1);
            if (pos >= 0 && listView.getCount() > pos) {
                listView.setItemChecked(pos, true);
            }


//            long[] pos = getIntent().getLongArrayExtra(CHECKED_POS);
//            if (pos !=null && pos.length >0) {
//                for (long p :pos) {
//                    listView.setItemChecked((int)p,true);
//                }
//            }

        }else {
            adapter.clear();
            adapter.addAll(students);
        }


    }

    private void showEmptyView() {
        emptyView.setVisibility(View.VISIBLE);
    }


    private void getStudents(){
        CriteriaStudents criteria=new CriteriaStudents();
        LessonDataManager.getClasses(this,criteria , new APIServiceCallback<Students>() {
            @Override
            public void onSuccess(Students object) {
                if(object!=null){
                    ArrayList<AbsStudent> students=new ArrayList<AbsStudent>();
                    if(!ArrayUtil.isEmpty(object.classes)){
                        students.addAll(object.classes);
                    }
                    if(!ArrayUtil.isEmpty(object.lessons)){
                        students.addAll(object.lessons);
                    }
                    updateAdapter(students);
                }else {
                    showEmptyView();
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void choiceComplete() {

        long[] ids = listView.getCheckItemIds();
        ArrayList<EnrollImport> contacts = new ArrayList<>(ids.length);
        for (long id : ids) {

            AbsStudent student = adapter.getItem((int) id);

            EnrollImport enrollImport = new EnrollImport();
            enrollImport.id = student.id;
            enrollImport.subtype = student.typeName;

            contacts.add(enrollImport);
        }

        if (TextUtils.isEmpty(classId)) {

            finishComplete(contacts);
        } else {
            commitToClass(contacts);
        }

    }

    private void commitToClass(final ArrayList<EnrollImport> imports) {


        ClassEnroll classEnroll = new ClassEnroll();
        classEnroll.importe = imports;

        ClassEnrollParams params = new ClassEnrollParams();
        params.enroll = classEnroll;

        showProgress(true);
        LessonDataManager.addClassStudent(this, classId, params, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                cancelProgress();
                Toast.makeText(ImportStudentFormClassActivity.this, R.string.add_success, Toast.LENGTH_SHORT).show();
                finishComplete(imports);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();
                Toast.makeText(ImportStudentFormClassActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void finishComplete(ArrayList<EnrollImport> imports) {
        Intent i = new Intent();
        i.putExtra(EXTRA_IMPORTS, imports);
        setResult(RESULT_OK, i);
        finish();

    }


    private class ChoiceAdapter extends ArrayAdapter<AbsStudent> {
        public ChoiceAdapter(Context context, int resource, int tid, List<AbsStudent> objects) {
            super(context, resource, tid, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView textView = (TextView) v.findViewById(R.id.title);
            textView.setText(getItem(position).title);
            return v;
        }
    }

}
