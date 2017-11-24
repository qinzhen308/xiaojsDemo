package cn.xiaojs.xma.ui.classroom2.member;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.DecisionReason;
import cn.xiaojs.xma.model.ctl.StudentEnroll;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.chat.SingleSessionFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.classroom2.widget.LoadmoreRecyclerView;
import cn.xiaojs.xma.ui.lesson.xclass.VerificationActivity;
import cn.xiaojs.xma.util.ArrayUtil;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class VerifyListFragment extends BottomSheetFragment implements
        DialogInterface.OnKeyListener, LoadmoreRecyclerView.LoadmoreListener {

    @BindView(R.id.cl_root)
    ConstraintLayout rootLay;
    @BindView(R.id.rlist)
    LoadmoreRecyclerView recyclerView;
    @BindView(R.id.title)
    TextView titleView;


    private VerifyListAdapter adapter;

    private Pagination pagination;
    private boolean loading;

    private ArrayList<StudentEnroll> studentEnrolls;

    private String classid;


    public static void invokeShow(String classid, Fragment targetFrament, int requestCode) {

        VerifyListFragment vistorListFragment = new VerifyListFragment();
        vistorListFragment.setClassid(classid);
        vistorListFragment.setTargetFragment(targetFrament, requestCode);
        vistorListFragment.show(targetFrament.getFragmentManager(), "verify");

    }

    public void setClassid(String classid) {
        this.classid = classid;
    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_verifylist, container, false);
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

        studentEnrolls = new ArrayList<>();

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new VerifyListAdapter(VerifyListFragment.this, getContext(), studentEnrolls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadmoreListener(this);

        pagination = new Pagination();
        pagination.setPage(1);
        pagination.setMaxNumOfObjectsPerPage(10);

        showLoadingStatus();

        load();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.back_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                dismiss();
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

    @Override
    public void dismiss() {
        super.dismiss();

        notifyDestory();
    }

    @Override
    public void onLoadMore() {
        load();
    }

    protected void load() {

        LessonDataManager.getClassStudents(getContext(), classid, false, pagination,
                new APIServiceCallback<CollectionPage<StudentEnroll>>() {
                    @Override
                    public void onSuccess(CollectionPage<StudentEnroll> object) {

                        loading = false;
                        if (pagination.getPage() <= 1) {
                            if (object != null && !ArrayUtil.isEmpty(object.objectsOfPage)) {
                                hiddenTips();
                            } else {
                                showFinalTips();
                            }
                        } else {
                            hiddenTips();
                        }

                        if (object != null && !ArrayUtil.isEmpty(object.objectsOfPage)) {
                            studentEnrolls.addAll(object.objectsOfPage);
                            pagination.setPage(pagination.getPage() + 1);
                            adapter.notifyDataSetChanged();
                        }
                        recyclerView.loadCompleted();

                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        loading = false;
                        recyclerView.loadCompleted();
                    }
                });
    }


    public void ackDecision(int position, final StudentEnroll student, final int decision) {

        DecisionReason reason = new DecisionReason();
        reason.action = decision;

        showProgress(true);
        LessonDataManager.reviewJoinClass(getContext(), student.doc.id, reason, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

                cancelProgress();

                if (decision == Ctl.ACKDecision.ACKNOWLEDGE) {
                    student.state = Platform.JoinClassState.ACCEPTED;
                } else if (decision == Ctl.ACKDecision.REFUSED) {
                    student.state = Platform.JoinClassState.REJECTTED;
                }

                adapter.notifyDataSetChanged();

                notifyRefresh();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void notifyRefresh() {
        Fragment  targetFragment = getTargetFragment();
        if (targetFragment != null) {
            Intent i = new Intent();
            targetFragment.onActivityResult(CTLConstant.REQUEST_VERIFY_MEMBER,
                    Activity.RESULT_OK, i);
        }
    }

    private void notifyDestory() {
        Fragment  targetFragment = getTargetFragment();
        if (targetFragment != null) {
            Intent i = new Intent();
            targetFragment.onActivityResult(CTLConstant.REQUEST_VERIFY_DESTORY,
                    Activity.RESULT_OK, i);
        }
    }
}
