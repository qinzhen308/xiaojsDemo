package cn.xiaojs.xma.ui.classroom2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.IEventer;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.Publish;
import cn.xiaojs.xma.model.VisitorParams;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.ClassJoin;
import cn.xiaojs.xma.model.ctl.JoinCriteria;
import cn.xiaojs.xma.model.ctl.ModifyModeParam;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ChangeNotify;
import cn.xiaojs.xma.ui.base.AbsListAdapter;
import cn.xiaojs.xma.ui.classroom2.base.BaseDialogFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.SessionDataObserver;
import cn.xiaojs.xma.ui.classroom2.member.MemberListFragment;
import cn.xiaojs.xma.ui.classroom2.member.MemberType;
import cn.xiaojs.xma.ui.share.ShareQrcodeClassroomActivity;
import cn.xiaojs.xma.ui.lesson.xclass.AddLessonNameActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassScheduleActivity;
import cn.xiaojs.xma.ui.lesson.xclass.StudentsListActivity;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.widget.AdapterGirdView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class ClassDetailFragment extends BaseDialogFragment {
    public static final String EXTRA_CLASSID = "classid";
    public static final String EXTRA_TEACHING = "teaching";
    public static final String EXTRA_VERI = "verify";
    public final int REQUEST_NAME_CODE = 0x1;
    public final int REQUEST_ADD_ONE_STUDENT_CODE = 0x2;
    public final int REQUEST_STUDENT_LIST_CODE = 0x3;
    public final int REQUEST_ADD_LESSON = 0x4;

    @BindView(R.id.class_name)
    TextView className;
    @BindView(R.id.more_btn)
    ImageView moreBtn;
    @BindView(R.id.class_qrcode)
    TextView classQrcode;
    @BindView(R.id.class_canlender)
    TextView classCanlender;
    @BindView(R.id.class_material_title)
    TextView classMaterialTitle;
    @BindView(R.id.class_member_title)
    TextView classMemberTitle;
    @BindView(R.id.member_list)
    AdapterGirdView memberList;
    @BindView(R.id.class_top_check)
    CheckedTextView classTopCheck;
    @BindView(R.id.class_disturb_check)
    CheckedTextView classDisturbCheck;
    @BindView(R.id.class_verify_check)
    CheckedTextView classVerifyCheck;
    @BindView(R.id.class_public_check)
    CheckedTextView classPublicCheck;
    @BindView(R.id.class_anonymous_check)
    CheckedTextView classAnonymousCheck;

    @BindView(R.id.cb_allow_guest_chart)
    CheckBox cbAllowGuestChart;
    @BindView(R.id.cb_allow_guest_read)
    CheckBox cbAllowGuestRead;
    @BindView(R.id.layout_guest_authority)
    View layoutGuestAuthority;

    MemberAdapter mAdapter;

    private ClassInfo classInfo;
    private String classId;
    private boolean teaching;
    private ClassroomEngine classroomEngine;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classId = ClassroomEngine.getEngine().getCtlSession().cls.id;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_classroom2_class_detail, container, false);
        ButterKnife.bind(this, view);

        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_NOTI);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        classroomEngine = ClassroomEngine.getEngine();

        initView();
        loadClassInfo();
        classroomEngine.observeSessionData(dataObserver);
    }

    @Override
    public void onDestroy() {
        classroomEngine.unObserveSessionData(dataObserver);
        super.onDestroy();
    }

    private void initView() {
        mAdapter = new MemberAdapter(getActivity());
        memberList.setAdapter(mAdapter);
    }


    //@OnCheckedChanged

    @OnClick({R.id.back_btn, R.id.more_btn, R.id.class_top_check, R.id.class_material_title, R.id.class_member_title,
            R.id.class_disturb_check, R.id.class_verify_check, R.id.class_public_check,
            R.id.class_anonymous_check, R.id.class_name_title, R.id.class_qrcode_title,
            R.id.cb_allow_guest_chart, R.id.cb_allow_guest_read, R.id.class_canlender_title})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
            case R.id.class_name_title:
                if (teaching) {
                    modifyName();
                }
                break;
            case R.id.more_btn:
                if (Ctl.ClassState.IDLE.equals(classInfo.state) &&
                        AccountDataManager.getAccountID(getActivity()).equals(classInfo.owner != null ? classInfo.owner.getId() : "")) {
                    showMoreDlg();
                } else if (!AccountDataManager.getAccountID(getActivity()).equals(classInfo.owner != null ? classInfo.owner.getId() : "")) {//不是创建者
                    showMoreDlgByStudent();
                }
                break;
            case R.id.class_top_check:
                break;
            case R.id.class_disturb_check:
                changeNotify();
                break;
            case R.id.class_verify_check:
                modifyVerify(!classVerifyCheck.isChecked());
                break;
            case R.id.class_public_check:
                modifyPublish(!classPublicCheck.isChecked());
                break;
            case R.id.class_anonymous_check:
                modifyVistor(!classAnonymousCheck.isChecked());
                break;
            case R.id.cb_allow_guest_chart:
                modifyVistorTalk(cbAllowGuestChart.isChecked());
                break;
            case R.id.cb_allow_guest_read:
                modifyVistorLibrary(cbAllowGuestRead.isChecked());
                break;
            case R.id.class_qrcode_title:
                /*if(classInfo!=null&& !ArrayUtil.isEmpty(classInfo.advisers)){
                    ShareBeautifulQrcodeActivity.invoke(getActivity(),ShareBeautifulQrcodeActivity.TYPE_CLASS,classId,className.getText().toString(),classInfo.advisers[0]);
                }*/
                if (classInfo != null) {

                    ShareQrcodeClassroomActivity.invoke(getActivity(), classInfo.id, classInfo.title);
                }

                break;
            case R.id.class_canlender_title:
                openSchedule();
                break;
            case R.id.class_material_title:
                databank();
                break;
            case R.id.class_member_title:
                memberList();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NAME_CODE:
                    if (data != null) {
                        String newName = data.getStringExtra(AddLessonNameActivity.EXTRA_NAME);
                        if (!TextUtils.isEmpty(newName)) {
                            classInfo.title = newName;
                            className.setText(newName);

                            ClassInfo info = classroomEngine.getClassInfo();
                            if (info !=null) {
                               info.title = newName;
                            }

                        }
                    }

                    break;
                case REQUEST_STUDENT_LIST_CODE:
                    if (data != null) {
                        //
                    }
                    loadStudents();
                    break;
                case REQUEST_ADD_ONE_STUDENT_CODE:
                    if (data != null) {
                        if (classInfo != null && classInfo.join != null) {
                            classInfo.join.current = 1;
                        }
                    }
                    loadStudents();

                    break;
                case REQUEST_ADD_LESSON:
                    loadClassInfo();
                    break;
            }
        }
    }

 /*   private void loadStudents(){
        JoinCriteria criteria=new JoinCriteria();
        criteria.joined=true;
        criteria.title = null;
        Pagination pagination=new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(teaching?4:5);
        LessonDataManager.getClassStudents(getActivity(), classId, criteria, pagination,
                new APIServiceCallback<CollectionPage<StudentEnroll>>() {
                    @Override
                    public void onSuccess(CollectionPage<StudentEnroll> object) {

                        if (object != null) {
                            mAdapter.setList(object.objectsOfPage);
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                    }
                });
    }*/

    private void loadStudents() {
        Observable.create(new ObservableOnSubscribe<ArrayList<Attendee>>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayList<Attendee>> e) throws Exception {

                ArrayList<Attendee> attendees = new ArrayList<>();

                Map<String, Attendee> attendeeMap = classroomEngine.getMembers();
                if (attendeeMap != null && attendeeMap.size() > 0) {
                    for (Attendee att : attendeeMap.values()) {

                        String pst = TextUtils.isEmpty(att.psTypeInLesson) ?
                                att.psType : att.psTypeInLesson;

                        if (classroomEngine.getUserIdentity(pst) == CTLConstant.UserIdentity.VISITOR) {
                            break;
                        }

                        attendees.add(att);
                    }
                }

                Collections.sort(attendees);

                e.onNext(attendees);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ArrayList<Attendee>>() {
            @Override
            public void accept(ArrayList<Attendee> attendees) throws Exception {
                ArrayList<Attendee> justNeedAttendees = new ArrayList<Attendee>();
                int maxSize = teaching ? 4 : 5;
                for (int i = 0, size = attendees.size() > maxSize ? maxSize : attendees.size(); i < size; i++) {
                    justNeedAttendees.add(attendees.get(i));
                }
                mAdapter.setList(justNeedAttendees);
                mAdapter.notifyDataSetChanged();
                classMemberTitle.setText("成员（" + attendees.size() + "）");

            }
        });
    }

    private void loadClassInfo() {
        showProgress(true);
        LessonDataManager.getClassInfo(getActivity(), classId, new APIServiceCallback<ClassInfo>() {
            @Override
            public void onSuccess(ClassInfo object) {

                cancelProgress();

                if (object != null) {
                    classroomEngine.setClassInfo(object);
                    classInfo = object;
                    bingView();
                    loadStudents();
                } else {
//                    showEmptyView(getString(R.string.empty_tips));
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
//                showFailedView(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        loadClassInfo();
//                    }
//                });
            }
        });
    }

    private void bingView() {

        if (classInfo == null) {
            return;
        }

        String mid = AccountDataManager.getAccountID(getActivity());
        //创建者
        if (Ctl.ClassState.IDLE.equals(classInfo.state) && mid.equals(classInfo.owner != null ? classInfo.owner.getId() : "")) {
            moreBtn.setVisibility(View.VISIBLE);

        } else if (!mid.equals(classInfo.owner != null ? classInfo.owner.getId() : "")) {//学生
            moreBtn.setVisibility(View.VISIBLE);
        } else {
            moreBtn.setVisibility(View.INVISIBLE);
        }

        teaching = initTeaching();

        className.setText(classInfo.title);

        //班主任的显示逻辑
        /*String teacher = "";
        if(classInfo.advisers != null && classInfo.advisers.length > 0){
            teacher+=classInfo.advisers[0].name;
            if(classInfo.advisers.length>1){
                teacher+="、"+classInfo.advisers[1].name;
            }
            if( classInfo.advisers.length>2 ){//大于2个才能点击弹框
                teacher+="...";
                teacherNameView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_entrance,0);
            }else {
                teacherNameView.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
        }else {
            teacher=classInfo.owner!=null?classInfo.owner.name:"";
        }
        teacherNameView.setText(teacher);*/

        if (classInfo.lessons == 0) {
            classCanlender.setText("无课");
        } else {
            classCanlender.setText(getString(R.string.number_lesson, classInfo.lessons));
        }

//        int studentCount = classInfo.join != null ? classInfo.join.current : 0;
//        classMemberTitle.setText("成员（"+studentCount+"）");
//        opentimeView.setText(TimeUtil.format(classInfo.createdOn, TimeUtil.TIME_YYYY_MM_DD));
//        creatorView.setText(classInfo.owner!=null?classInfo.owner.name:"");

        if (teaching) {
            classVerifyCheck.setVisibility(View.VISIBLE);
            boolean verify = (classInfo.join != null && classInfo.join.mode == Ctl.JoinMode.VERIFICATION) ?
                    true : false;

            classVerifyCheck.setChecked(verify);

            classPublicCheck.setVisibility(View.VISIBLE);
            classPublicCheck.setChecked(classInfo.publish != null && classInfo.publish.accessible);
            classAnonymousCheck.setVisibility(View.VISIBLE);
//            classAnonymousCheck.setChecked(classInfo.);
            classAnonymousCheck.setChecked(classInfo.visitor);
            if (classInfo.visitor) {
                layoutGuestAuthority.setVisibility(View.VISIBLE);
            } else {
                layoutGuestAuthority.setVisibility(View.GONE);
            }
            cbAllowGuestRead.setChecked(classInfo.library);
            cbAllowGuestChart.setChecked(classInfo.talk);

        } else {
            classVerifyCheck.setVisibility(View.GONE);
            classPublicCheck.setVisibility(View.GONE);
            classAnonymousCheck.setVisibility(View.GONE);
        }

        // TODO: 2017/11/6
        classTopCheck.setVisibility(View.GONE);


        boolean silent = DataProvider.getProvider(getContext()).getClassSilent(classId);
        classDisturbCheck.setChecked(silent);
    }

    private boolean initTeaching() {
        if (classInfo == null)
            return false;

        String mid = AccountDataManager.getAccountID(getActivity());

        //创建者
        if (mid.equals(classInfo.owner.getId())) {
            return true;
        }

        //班主任
        if (classInfo.advisers != null && classInfo.advisers.length > 0) {
            for (Account account : classInfo.advisers) {
                if (mid.equals(account.getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    private void databank() {
        if (classroomEngine.isVistor()) {
            Toast.makeText(getContext(), R.string.no_permision_tips, Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: 2017/11/6 资料库 打开activity or fragment？

    }

    private void memberList() {

        MemberListFragment memberListfragment = MemberListFragment.createInstance(MemberListFragment.FROM_CLASSROOM_DETAIL);
        memberListfragment.show(getFragmentManager(), "member");

    }

    private void changeNotify() {

        final ChangeNotify changeNotify = new ChangeNotify();
        changeNotify.to = classInfo.id;

        //FIXME 接口没有返回数据
        changeNotify.silent = !classDisturbCheck.isChecked();

        XMSManager.sendChangeNotify(getContext(), changeNotify,
                new cn.xiaojs.xma.data.api.socket.EventCallback<EventResponse>() {
                    @Override
                    public void onSuccess(EventResponse response) {
                        DataProvider dataProvider = DataProvider.getProvider(getContext());
                        dataProvider.updateSilent(changeNotify.to, changeNotify.silent);

                        classDisturbCheck.setChecked(changeNotify.silent);
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {

                    }
                });
    }

    private void openSchedule() {
       /* ScheduleFragment fragment=ScheduleFragment.createInstance(classId);
        fragment.setTargetFragment(this,REQUEST_ADD_LESSON);
        fragment.show(getChildFragmentManager(),"schedule");*/
        ClassScheduleActivity.invoke(getActivity(), classInfo.id, classInfo.title, teaching);

    }

    private void modifyName() {
        Intent i = new Intent(getActivity(), AddLessonNameActivity.class);
        i.putExtra(AddLessonNameActivity.EXTRA_CLASSID, classId);
        //i.putExtra(AddLessonNameActivity.EXTRA_VER_MODE, classInfo.join.mode);
        i.putExtra(AddLessonNameActivity.EXTRA_NAME, className.getText().toString());
        i.putExtra(AddLessonNameActivity.EXTRA_ROLE, AddLessonNameActivity.ROLE_CLASS);
        startActivityForResult(i, REQUEST_NAME_CODE);
    }

    private void showMoreDlg() {
        ListBottomDialog dialog = new ListBottomDialog(getActivity());
        String[] items = new String[]{getString(R.string.disband_classroom)};
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://解散
                        showDisbandConfirmDlg(classInfo.id);
                        break;
                }
            }
        });
        dialog.show();
    }

    //退出班级--学生
    private void abortClass() {
        final CommonDialog dialog = new CommonDialog(getActivity());
        dialog.setDesc(R.string.abort_class_tip);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {

                dialog.dismiss();

                showProgress(true);
                LessonDataManager.abortClass(getActivity(), classId, new APIServiceCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody object) {
                        cancelProgress();
                        ToastUtil.showToast(getActivity(), R.string.logout_tips);
                        DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.CREATE_CLASS_CHANGED);
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                        ToastUtil.showToast(getActivity(), errorMessage);
                    }
                });
            }
        });
        dialog.show();
    }

    //学生操作
    private void showMoreDlgByStudent() {
        ListBottomDialog dialog = new ListBottomDialog(getActivity());
        String[] items = new String[]{getString(R.string.abort_class)};
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://退出
                        abortClass();
                        break;
                }
            }
        });
        dialog.show();
    }

    //删除
    private void showDisbandConfirmDlg(final String classId) {
        final CommonDialog dialog = new CommonDialog(getActivity());
        dialog.setTitle(R.string.disband_classroom);
        dialog.setDesc(R.string.disband_class_tip);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
            }
        });
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.cancel();
                disbandClass(classId);
            }
        });
        dialog.show();
    }

    private void disbandClass(String classId) {

        showProgress(true);
        LessonDataManager.removeClass(getActivity(), classId, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                Toast.makeText(getActivity(), R.string.disband_success, Toast.LENGTH_SHORT).show();
                DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.CREATE_CLASS_CHANGED);
                getActivity().finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void modifyVerify(final boolean verify) {

        ModifyModeParam modeParam = new ModifyModeParam();

        final int mode = verify ? Ctl.JoinMode.VERIFICATION : Ctl.JoinMode.OPEN;
        modeParam.join = mode;

        showProgress(true);
        LessonDataManager.modifyClass(getActivity(), classId, modeParam, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                classVerifyCheck.setChecked(verify);

                ClassInfo info = classroomEngine.getClassInfo();
                if (info != null) {
                    if (info.join == null) {
                        info.join = new ClassJoin();
                    }

                    info.join.mode = mode;
                }

                Toast.makeText(getActivity(),
                        R.string.lesson_edit_success,
                        Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                classVerifyCheck.setChecked(!verify);
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void modifyPublish(final boolean isPublish) {

        Publish publishParam = new Publish();
        publishParam.accessible = isPublish;

        showProgress(true);
        LessonDataManager.modifyClass(getActivity(), classId, publishParam, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                classPublicCheck.setChecked(isPublish);

                ClassInfo info = classroomEngine.getClassInfo();
                if (info != null) {
                    if (info.publish == null) {
                        info.publish = new Publish();
                    }

                    info.publish.accessible = isPublish;
                }


                Toast.makeText(getActivity(),
                        R.string.lesson_edit_success,
                        Toast.LENGTH_SHORT)
                        .show();


            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                classPublicCheck.setChecked(!isPublish);
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void modifyVistor(final boolean allowGuest) {

        VisitorParams visitorParams = new VisitorParams();
        visitorParams.visitor = allowGuest;

        showProgress(true);
        LessonDataManager.modifyClass(getActivity(), classId, visitorParams, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                classAnonymousCheck.setChecked(allowGuest);
                if (allowGuest) {
                    layoutGuestAuthority.setVisibility(View.VISIBLE);
                } else {
                    //允许访客关闭后，访客相关设置全部清空为false
                    layoutGuestAuthority.setVisibility(View.GONE);
                    cbAllowGuestRead.setChecked(false);
                    cbAllowGuestChart.setChecked(false);
                }

                ClassInfo info = classroomEngine.getClassInfo();
                if (info !=null) {
                    info.visitor = allowGuest;
                }



                Toast.makeText(getActivity(),
                        R.string.lesson_edit_success,
                        Toast.LENGTH_SHORT)
                        .show();


            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                classAnonymousCheck.setChecked(!allowGuest);
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void modifyVistorTalk(final boolean allowChart) {

        VisitorParams visitorParams = new VisitorParams();
        visitorParams.talk = allowChart;

        showProgress(true);
        LessonDataManager.modifyClass(getActivity(), classId, visitorParams, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                cbAllowGuestChart.setChecked(allowChart);

                ClassInfo info = classroomEngine.getClassInfo();
                if (info !=null) {
                    info.talk = allowChart;
                }

                Toast.makeText(getActivity(),
                        R.string.lesson_edit_success,
                        Toast.LENGTH_SHORT)
                        .show();


            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                cbAllowGuestChart.setChecked(!allowChart);
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void modifyVistorLibrary(final boolean allowLib) {

        VisitorParams visitorParams = new VisitorParams();
        visitorParams.library = allowLib;

        showProgress(true);
        LessonDataManager.modifyClass(getActivity(), classId, visitorParams, new APIServiceCallback<CLResponse>() {
            @Override
            public void onSuccess(CLResponse object) {
                cancelProgress();
                cbAllowGuestRead.setChecked(allowLib);

                ClassInfo info = classroomEngine.getClassInfo();
                if (info !=null) {
                    info.library = allowLib;
                }

                Toast.makeText(getActivity(),
                        R.string.lesson_edit_success,
                        Toast.LENGTH_SHORT)
                        .show();


            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                cbAllowGuestRead.setChecked(!allowLib);
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }


    private SessionDataObserver dataObserver = new SessionDataObserver() {
        @Override
        public void onMemberUpdated() {
            loadStudents();
        }
    };

    public class MemberAdapter extends AbsListAdapter<Attendee, MemberHolder> {


        public MemberAdapter(Activity activity) {
            super(activity);
        }

        @Override
        public void onBindViewHolder(int position, MemberHolder holder) {
            if (holder.root instanceof IViewModel) {
                ((IViewModel) holder.root).bindData(position, getItem(position));
            }
            if (holder.root instanceof IEventer) {
                ((IEventer) holder.root).setEventCallback(new EventCallback() {
                    @Override
                    public void onEvent(int what, Object... object) {
                        if (what == EVENT_1) {

                        } else if (what == EVENT_2) {//添加学生
                            /*Intent i = new Intent(getActivity(), StudentsListActivity.class);
                            i.putExtra(StudentsListActivity.EXTRA_CLASS, ClassroomEngine.getEngine().getCtlSession().cls.id);
                            i.putExtra(EXTRA_TEACHING, teaching);
                            boolean verflag = (classInfo.join != null && classInfo.join.mode == Ctl.JoinMode.VERIFICATION) ?
                                    true : false;
                            i.putExtra(EXTRA_VERI, verflag);
                            startActivityForResult(i, REQUEST_STUDENT_LIST_CODE);*/
                            MemberListFragment memberListfragment = MemberListFragment.createInstance(MemberListFragment.FROM_CLASSROOM_DETAIL);
                            memberListfragment.show(getFragmentManager(), "member");
                        }
                    }
                });
            }

        }

        @Override
        public Object getItem(int position) {
            if (!teaching) {
                return super.getItem(position);
            } else if (getCount() - 1 == position) {
                return null;
            }
            return super.getItem(position);
        }

        @Override
        public int getCount() {
            if (teaching) {
                return super.getCount() + 1;
            } else {
                return super.getCount();
            }
        }

        @Override
        public MemberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MemberHolder holder = null;
            if (viewType == 0) {
                holder = new MemberHolder(new MemberItemView(mContext));
            } else {
                holder = new MemberHolder(new AddMemberItemView(mContext));
            }
            return holder;
        }

        @Override
        public int getItemViewType(int position) {
            if (!teaching) {
                return 0;
            }
            return position == getCount() - 1 ? 1 : 0;
        }

        @Override
        public int getViewTypeCount() {
            return teaching ? 2 : 1;
        }
    }

    public static class MemberHolder extends AbsListAdapter.ViewHolder {

        public MemberHolder(View v) {
            super(v);
        }
    }


}
