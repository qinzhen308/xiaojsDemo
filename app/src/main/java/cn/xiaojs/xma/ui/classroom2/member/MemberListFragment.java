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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.ClassEnroll;
import cn.xiaojs.xma.model.ctl.ClassEnrollParams;
import cn.xiaojs.xma.model.ctl.ClassInfo;
import cn.xiaojs.xma.model.ctl.EnrollImport;
import cn.xiaojs.xma.model.ctl.JoinCriteria;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.chat.SingleSessionFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.classroom2.core.SessionDataObserver;
import cn.xiaojs.xma.ui.classroom2.material.AddNewFragment;
import cn.xiaojs.xma.ui.contact2.ContactFragment;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.ClassItem;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

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
    private EventListener.ELMember eventListener;

    private ArrayList<Attendee> vistors;
    private String classId;

    private int from=FROM_CLASSROOM_HOME_BOTTOM;
    public static final int FROM_CLASSROOM_HOME_BOTTOM=0;
    public static final int FROM_CLASSROOM_DETAIL=1;
    public static final String EXTRA_FROM="extra_from";

    private FrameLayout thirdLayout;
    private BottomSheetFragment thirdFragment;




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data=getArguments();
        if(data!=null){
            from=data.getInt(EXTRA_FROM,FROM_CLASSROOM_HOME_BOTTOM);
        }
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_memberlist, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        classId = classroomEngine.getCtlSession().cls.id;

        if (getDialog() == null||from==FROM_CLASSROOM_DETAIL) {
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
        memberAdapter = new MemberAdapter(MemberListFragment.this, getContext(), attendees);

        recyclerView.setAdapter(memberAdapter);

        load(null);

        eventListener = classroomEngine.observerMember(memberConsumer);
        classroomEngine.observeSessionData(dataObserver);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        classroomEngine.unObserveSessionData(dataObserver);

        if (eventListener != null) {
            eventListener.dispose();
            eventListener = null;
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


    private void load(final ArrayList<Attendee> tempAtts) {

        showLoadingStatus();

        Observable.create(new ObservableOnSubscribe<Map<Integer, ArrayList<Attendee>>>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<Map<Integer, ArrayList<Attendee>>> e) throws Exception {
                ArrayList<Attendee> attendees = new ArrayList<>();

                if (classroomEngine.getClassJoin() == Ctl.JoinMode.VERIFICATION
                        && classroomEngine.classManageable()) {

                    JoinCriteria criteria=new JoinCriteria();
                    criteria.joined=true;
                    Pagination pagination = new Pagination();
                    pagination.setPage(1);
                    pagination.setMaxNumOfObjectsPerPage(1);

                    CollectionPage<StudentEnroll> students = LessonDataManager.getClassStudentsSync(
                            getContext(), classId, criteria, pagination);

                    int applyCount = 0;
                    if (students!=null) {
                        applyCount = students.countOfApplying;
                    }

                    Attendee verfyItem = new Attendee();
                    verfyItem.ctype = -2;
                    verfyItem.unReadMsgCount = applyCount;
                    attendees.add(verfyItem);
                }



                ArrayList<Attendee> vistors = new ArrayList<>();

                if (tempAtts == null) {
                    Map<String, Attendee> attendeeMap = classroomEngine.getMembers();
                    if (attendeeMap != null && attendeeMap.size() > 0) {
                        for (Attendee att : attendeeMap.values()) {

                            String pst = TextUtils.isEmpty(att.psTypeInLesson)?
                                    att.psType : att.psTypeInLesson;


                            if (classroomEngine.getUserIdentity(pst) == CTLConstant.UserIdentity.VISITOR) {
                                vistors.add(att);
                                continue;
                            }

                            attendees.add(att);
                        }
                    }
                } else {
                    if (tempAtts.size() > 0) {

                        for (Attendee att : tempAtts) {

                            String pst = TextUtils.isEmpty(att.psTypeInLesson)?
                                    att.psType : att.psTypeInLesson;

                            if (classroomEngine.getUserIdentity(pst) == CTLConstant.UserIdentity.VISITOR) {
                                vistors.add(att);
                                continue;
                            }

                            attendees.add(att);
                        }
                    }
                }

                if (vistors.size() > 0) {
                    Attendee vistor = new Attendee();
                    vistor.ctype = -1;
                    vistor.unReadMsgCount = vistors.size();
                    attendees.add(vistor);

                    Collections.sort(vistors);
                }

                Collections.sort(attendees);


                Map<Integer, ArrayList<Attendee>> dataMap = new HashMap<>();
                dataMap.put(MemberType.VISTOR, vistors);
                dataMap.put(MemberType.NORMAL, attendees);

                e.onNext(dataMap);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Map<Integer, ArrayList<Attendee>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Map<Integer, ArrayList<Attendee>> integerArrayListMap) {

                        if (memberAdapter != null) {

                            ArrayList<Attendee> atts = integerArrayListMap.get(MemberType.NORMAL);
                            memberAdapter.updateData(atts);

                            if (atts.size() > 0) {
                                hiddenTips();
                            } else {
                                showFinalTips();
                            }
                        } else {
                            showFinalTips();
                        }
                        vistors = integerArrayListMap.get(MemberType.VISTOR);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        showFinalTips();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void enterVistorlist() {
        VistorListFragment.invokeShow(getFragmentManager(), vistors);
    }

    public void enterVerifyList() {

        if (getDialog() == null) {

            VerifyListFragment verifyListFragment = new VerifyListFragment();
            verifyListFragment.setClassid(classId);
            verifyListFragment.setTargetFragment(this, CTLConstant.REQUEST_VERIFY_MEMBER);

            if (thirdLayout == null) {
                initThirdLayout();
            }
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_database_framelayout, verifyListFragment)
                    .addToBackStack("")
                    .commitAllowingStateLoss();

            thirdFragment = verifyListFragment;

        } else {
            VerifyListFragment.invokeShow(classId,this,CTLConstant.REQUEST_VERIFY_MEMBER);
        }



    }

    private void initThirdLayout() {
        thirdLayout = new FrameLayout(getContext());
        thirdLayout.setId(R.id.fragment_database_framelayout);
        thirdLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        ((RelativeLayout) rootLayout).addView(thirdLayout);
    }

    private void destoryThird() {

        if (thirdFragment != null) {
            getFragmentManager()
                    .beginTransaction()
                    .remove(thirdFragment)
                    .commitAllowingStateLoss();
            thirdFragment = null;
        }

        if (thirdLayout != null) {
            ((RelativeLayout) rootLayout).removeView(thirdLayout);
            thirdLayout = null;
        }

    }


    public void enterChatSession(Attendee attendee) {

        if (attendee.accountId.equals(AccountDataManager.getAccountID(getContext()))) {
            return;
        }

        if (classroomEngine.isVistor()) {
            Toast.makeText(getContext(), R.string.no_permision_tips, Toast.LENGTH_SHORT).show();
            return;
        }


        DataProvider dataProvider = DataProvider.getProvider(getContext());
        int followType = Social.FllowType.NA;
        if (dataProvider.existInContact(attendee.accountId)) {
            followType = Social.FllowType.FOLLOW_SHIP;
        }

        SingleSessionFragment.invoke(getFragmentManager(),
                attendee.accountId, attendee.name, followType);
    }


    private void addStudents() {

        if (classroomEngine.isVistor()) {
            Toast.makeText(getContext(), R.string.no_permision_tips, Toast.LENGTH_SHORT).show();
            return;
        }


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
                        String clsid = classroomEngine.getCtlSession().cls.id;
                        ContactFragment.invokeWithChoice(getFragmentManager(),clsid,
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
                case CTLConstant.REQUEST_VERIFY_MEMBER:
                    refreshMembers();
                    break;
                case CTLConstant.REQUEST_VERIFY_DESTORY:
                    destoryThird();
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

                        if (object != null && memberAdapter != null) {
                            load(object.attendees);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                    }
                });
    }

    private void updateMemByEvent() {
        load(null);
    }


    private Consumer<EventReceived> memberConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {

            switch (eventReceived.eventType) {
                case Su.EventType.JOIN:
                case Su.EventType.LEAVE:
                    updateMemByEvent();
                    break;
            }
        }
    };

    private SessionDataObserver dataObserver = new SessionDataObserver() {
        @Override
        public void onMemberUpdated() {
            load(null);
        }
    };


    public static MemberListFragment createInstance(int from){
        MemberListFragment fragment=new MemberListFragment();
        Bundle data=new Bundle();
        fragment.setArguments(data);
        data.putInt(EXTRA_FROM,from);
        return fragment;
    }
}
