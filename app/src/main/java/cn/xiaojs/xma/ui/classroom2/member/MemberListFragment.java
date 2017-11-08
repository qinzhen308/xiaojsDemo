package cn.xiaojs.xma.ui.classroom2.member;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.EnrollImport;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatSessionFragment;
import cn.xiaojs.xma.ui.classroom2.chat.SingleSessionFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.contact2.ContactFragment;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.ClassItem;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;
import cn.xiaojs.xma.ui.lesson.xclass.AddStudentActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ImportStudentFormClassActivity;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class MemberListFragment extends BottomSheetFragment implements DialogInterface.OnKeyListener,
        ContactFragment.ChoiceCompletedListener {

    @BindView(R.id.cl_root)
    ConstraintLayout rootLay;
    @BindView(R.id.member_list)
    RecyclerView recyclerView;
    @BindView(R.id.add_btn)
    ImageView addBtnView;

    private MemberAdapter memberAdapter;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_memberlist, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getDialog() == null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rootLay.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            rootLay.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            getDialog().setOnKeyListener(this);
        }

        if (getDialog() != null
                && (classroomEngine.getIdentity() == CTLConstant.UserIdentity.LEAD
                || classroomEngine.getIdentity() == CTLConstant.UserIdentity.ADVISER)) {
            addBtnView.setVisibility(View.VISIBLE);
        } else {
            addBtnView.setVisibility(View.GONE);
        }


        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<Attendee> attendees = new ArrayList<>();
        Map<String, Attendee> attendeeMap = classroomEngine.getMembers();
        if (attendeeMap != null && attendeeMap.size() > 0) {
            attendees.addAll(attendeeMap.values());
            Collections.sort(attendees);
        }

        memberAdapter = new MemberAdapter(MemberListFragment.this
                , getContext(), attendees);

        recyclerView.setAdapter(memberAdapter);

        if (memberAdapter.getItemCount() <= 0) {
            showFinalTips();
        }

    }

    @OnClick({R.id.add_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                addStudents();
                break;
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            dismiss();
            return true;
        }

        return false;
    }

    public void enterChatSession(Attendee attendee) {
        SingleSessionFragment.invoke(getFragmentManager(), attendee.accountId, attendee.name);
    }


    private void addStudents() {

        ListBottomDialog dialog = new ListBottomDialog(getContext());

        String[] items = getResources().getStringArray(R.array.classroom2_add_student);
        dialog.setItems(items);
        dialog.setTitleVisibility(View.GONE);
        dialog.setRightBtnVisibility(View.GONE);
        dialog.setLeftBtnVisibility(View.GONE);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://手机号添加
                        AddByPhoneFragment addByPhoneFragment = new AddByPhoneFragment();
                        addByPhoneFragment.setTargetFragment(MemberListFragment.this,
                                CTLConstant.REQUEST_ADD_MEMBERS);
                        addByPhoneFragment.show(getFragmentManager(), "addphone");
                        break;
                    case 1://从通讯录中添加
                        ContactFragment.invokeWithChoice(getFragmentManager(),
                                ListView.CHOICE_MODE_MULTIPLE, MemberListFragment.this);
                        break;
                }

            }

        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CTLConstant.REQUEST_ADD_MEMBERS:

                    StudentEnroll studentEnroll = data.getParcelableExtra(CTLConstant.EXTRA_MEMBER);
                    if (studentEnroll == null)
                        return;

                    ArrayList<StudentEnroll> enrolls = new ArrayList<>();
                    enrolls.add(studentEnroll);
                    addByPerson(enrolls);
                    break;
            }
        }
    }

    @Override
    public void onChoiceData(boolean person, ArrayList<AbsContactItem> data) {

        if (data == null || data.size() <= 0)
            return;

        if (person) {
            ArrayList<StudentEnroll> senrolls = new ArrayList<>(data.size());
            for (AbsContactItem item : data) {
                FriendItem friendItem = (FriendItem) item;
                StudentEnroll studentEnroll = new StudentEnroll();
                studentEnroll.id = friendItem.contact.id;
                studentEnroll.name = friendItem.contact.name;

                senrolls.add(studentEnroll);
            }

            addByPerson(senrolls);

        } else {

            ArrayList<EnrollImport> imports = new ArrayList<>(data.size());
            for (AbsContactItem item : data) {
                ClassItem classItem = (ClassItem) item;
                EnrollImport enrollImport = new EnrollImport();
                enrollImport.id = classItem.contact.id;
                enrollImport.subtype = classItem.contact.subtype;

                imports.add(enrollImport);
            }

            addByClasses(imports);

        }


    }

    private void addByPerson(ArrayList<StudentEnroll> studentEnrolls) {

        ClassEnrollParams enrollParams = new ClassEnrollParams();
        ClassEnroll classEnroll = new ClassEnroll();
        classEnroll.students = studentEnrolls;
        enrollParams.enroll = classEnroll;
        String cid = classroomEngine.getCtlSession().cls.id;

        showProgress(true);
        LessonDataManager.addClassStudent(getContext(), cid, enrollParams, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                cancelProgress();
                Toast.makeText(getContext(), R.string.add_success, Toast.LENGTH_SHORT).show();
                refreshMembers();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addByClasses(final ArrayList<EnrollImport> imports) {


        ClassEnroll classEnroll = new ClassEnroll();
        classEnroll.importe = imports;

        ClassEnrollParams params = new ClassEnrollParams();
        params.enroll = classEnroll;

        String cid = classroomEngine.getCtlSession().cls.id;

        showProgress(true);
        LessonDataManager.addClassStudent(getContext(), cid, params, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                cancelProgress();
                Toast.makeText(getContext(), R.string.add_success, Toast.LENGTH_SHORT).show();
                refreshMembers();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void refreshMembers() {
        classroomEngine.getMembers(classroomEngine.getTicket(),
                new APIServiceCallback<LiveCollection<Attendee>>() {
            @Override
            public void onSuccess(LiveCollection<Attendee> object) {

                if (object!=null && memberAdapter!=null) {
                    memberAdapter.updateData(object.attendees);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }
}
