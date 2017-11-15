package cn.xiaojs.xma.ui.classroom2.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.ClassroomPref;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.VerifyUtils;

import static butterknife.OnTextChanged.Callback.AFTER_TEXT_CHANGED;
import static butterknife.OnTextChanged.Callback.BEFORE_TEXT_CHANGED;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class AddByPhoneFragment extends BottomSheetFragment {


    @BindView(R.id.tips_content)
    TextView tipsContentView;
    @BindView(R.id.lay_tips)
    LinearLayout tipsRootView;

    @BindView(R.id.phone_input)
    EditTextDel phoneInputView;
    @BindView(R.id.name_input)
    EditTextDel nameInputView;

    private String pid;

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_member_add_by_phone, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tipsContentView.setText("添加成员后，手机号将作为ta登录小教室的唯一账号");

    }

    @OnClick({R.id.back_btn, R.id.ok_btn, R.id.lesson_creation_tips_close})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
            case R.id.ok_btn:
                okAdd();
                break;
            case R.id.lesson_creation_tips_close://关闭提醒
                closeCourCreateTips();
                break;
        }
    }


    @OnTextChanged(value = R.id.phone_input, callback = AFTER_TEXT_CHANGED)
    void onAfterTextChanged(CharSequence text) {
        String phone = text.toString().trim();
        pid = null;
        if (phone.length() == 11) {
            previewStudent(phone);
        }
    }

    private void closeCourCreateTips() {
        tipsRootView.setVisibility(View.GONE);
    }

    private void previewStudent(String phone) {
        if (!VerifyUtils.checkPhoneNum(phone)) {
            return;
        }

        checkAccount(phone);
    }


    private void checkAccount(String phone) {
        try {
            showProgress(true);
            //因为已经注册的用户，需要上传用户的ID，所以在这里要检测是否注册，如果注册了就获取ID.
            SearchManager.search(getContext(),
                    Social.SearchType.PERSON,
                    phone,
                    1,
                    10,
                    new APIServiceCallback<CollectionResult<SearchResultV2>>() {
                        @Override
                        public void onSuccess(CollectionResult<SearchResultV2> result) {
                            cancelProgress();
                            boolean hasExist = false;
                            SearchResultV2 searchResultV2 = null;

                            if (result != null && result.results != null && !result.results.isEmpty()) {
                                searchResultV2 = result.results.get(0);
                                hasExist = true;
                            }

                            if (searchResultV2 == null || TextUtils.isEmpty(searchResultV2.id)) {
                                hasExist = false;
                            }

                            if (hasExist) {
                                pid = searchResultV2.id;
                                String na = searchResultV2.basic.getName();
                                nameInputView.setText(na);
                            }
                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            cancelProgress();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void okAdd() {
        String phone = phoneInputView.getText().toString().trim();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getContext(), R.string.phone_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!VerifyUtils.checkPhoneNum(phone)) {
            Toast.makeText(getContext(), R.string.phone_error, Toast.LENGTH_SHORT).show();
            return;
        }


        String name = nameInputView.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "学生姓名必须输入", Toast.LENGTH_SHORT).show();
            return;
        }

        StudentEnroll studentEnroll = new StudentEnroll();

        if (TextUtils.isEmpty(pid)) {
            studentEnroll.mobile = phone;
            studentEnroll.name = name;
        }else {
            studentEnroll.id = pid;
        }

        Fragment  targetFragment = getTargetFragment();
        if (targetFragment != null) {
            Intent i = new Intent();
            i.putExtra(CTLConstant.EXTRA_MEMBER, studentEnroll);
            targetFragment.onActivityResult(CTLConstant.REQUEST_ADD_MEMBERS,
                    Activity.RESULT_OK, i);
        }

        dismiss();
    }


}
