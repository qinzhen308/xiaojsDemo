package cn.xiaojs.xma.ui.classroom2.schedule;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.ctl.ModifyClassParams;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.lesson.xclass.CreateClassActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;

/**
 * Created by maxiaobao on 2017/5/18.
 */

public class AddLessonNameFragment extends BottomSheetFragment
        implements DialogInterface.OnKeyListener {

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ROLE = "role";
    public static final String EXTRA_CLASSID = "classid";

    public static final int ROLE_LESSON = 0x1;
    public static final int ROLE_CLASS = 0x2;


    @BindView(R.id.search_input)
    EditTextDel editTextDel;
    @BindView(R.id.complete_list)
    ListView completeListView;

    private int currentRole;
    private String orginName;
    private String classId;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_add_lesson_name, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    protected void initView() {

        currentRole = getArguments().getInt(EXTRA_ROLE, ROLE_LESSON);
        orginName = getArguments().getString(EXTRA_NAME);

        classId = getArguments().getString(EXTRA_CLASSID);

        if (!TextUtils.isEmpty(orginName)) {
            editTextDel.setText(orginName);
        }
    }

    @OnClick({R.id.iv_back, R.id.btn_finish})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                dismiss();
                break;
            case R.id.btn_finish://确定
                complete();
                break;
        }
    }

    private void complete() {

        String name = editTextDel.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity(), R.string.live_lesson_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.length() > CreateClassActivity.MAX_CLASS_CHAR) {
            String nameEr = this.getString(R.string.live_lesson_name_error, CreateClassActivity.MAX_CLASS_CHAR);
            Toast.makeText(getActivity(), nameEr, Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentRole == ROLE_CLASS && !name.equals(orginName)) {
            modifyClassName(name);
        }else {
           setResultAndFinsih(name);
        }



    }

    private void modifyClassName(final String newName) {

        ModifyClassParams classParams = new ModifyClassParams();

        classParams.title = newName;

        showProgress(true);
        LessonDataManager.modifyClass(getActivity(), classId, classParams, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                Toast.makeText(getActivity(),
                        R.string.lesson_edit_success,
                        Toast.LENGTH_SHORT)
                        .show();

                setResultAndFinsih(newName);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setResultAndFinsih(String name) {

        if (!name.equals(orginName)) {
            Intent i = new Intent();
            i.putExtra(EXTRA_NAME, name);
            if(getTargetFragment()!=null){
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,i);
            }
        }
        dismiss();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }


    public static AddLessonNameFragment createInstance(String classId,String orginName,int role){
        AddLessonNameFragment fragment=new AddLessonNameFragment();
        Bundle data=new Bundle();
        data.putString(EXTRA_NAME,orginName);
        data.putString(EXTRA_CLASSID,classId);
        data.putInt(EXTRA_ROLE, role);
        fragment.setArguments(data);
        return fragment;
    }


    //TODO item view :R.layout.layout_complete_single_text_item
}
