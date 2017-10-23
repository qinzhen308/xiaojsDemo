package cn.xiaojs.xma.ui.classroom2.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.ui.classroom2.base.BaseRoomFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by maxiaobao on 2017/10/17.
 */

public class ChooseMemberFragment extends BaseRoomFragment {

    @BindView(R.id.rlist)
    RecyclerView recyclerView;

    private ChooseMemberAdapter memberAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom2_choose_member, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        loadMember();
    }

    @OnClick({R.id.ok_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ok_btn:
                ok();
                break;
        }
    }

    private void loadMember() {
        Observable.create(new ObservableOnSubscribe<ArrayList<Attendee>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayList<Attendee>> emitter) throws Exception {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("load online member start...");
                }

                ArrayList<Attendee> attendees = null;
                Map<String, Attendee> attendeeMap = ClassroomEngine.getEngine().getMembers();
                if (attendeeMap != null && attendeeMap.size() > 0) {
                    attendees = new ArrayList<>();

                    String myAccountId = AccountDataManager.getAccountID(getContext());

                    for (Attendee attendee : attendeeMap.values()) {

                        if (attendee.accountId.equals(myAccountId)) {
                            continue;
                        }

                        boolean online = attendee.xa == 0 ? false : true;
                        if (online) {
                            attendees.add(attendee);
                        }
                    }

                }

                emitter.onNext(attendees);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Attendee>>() {
                    @Override
                    public void accept(ArrayList<Attendee> attendees) throws Exception {

                        if (getActivity() == null) {
                            return;
                        }

                        memberAdapter = new ChooseMemberAdapter(getContext(), attendees);
                        recyclerView.setAdapter(memberAdapter);
                    }
                });
    }

    private void ok() {

        Attendee attendee = null;
        if (memberAdapter !=null) {
            attendee = memberAdapter.getCheckItem();
        }

        Fragment target = getTargetFragment();
        if (target != null) {
            Intent intent = new Intent();
            intent.putExtra(CTLConstant.EXTRA_MEMBER, attendee);
            target.onActivityResult(CTLConstant.REQUEST_CHOOSE_MEMBER, Activity.RESULT_OK, intent);
        }

    }
}
