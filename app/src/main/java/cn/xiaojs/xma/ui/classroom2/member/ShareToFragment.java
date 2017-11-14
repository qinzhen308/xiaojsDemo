package cn.xiaojs.xma.ui.classroom2.member;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


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

public class ShareToFragment extends BaseRoomFragment {

    @BindView(R.id.rlist)
    ListView listView;

    private ShareToAdapter shareToAdapter;
    private ClassroomEngine classroomEngine;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom2_share_to, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        classroomEngine = ClassroomEngine.getEngine();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        load();
    }

    @OnClick({R.id.ok_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ok_btn:

                break;
        }
    }


    private void load() {

        Observable.create(new ObservableOnSubscribe<ArrayList<Attendee>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ArrayList<Attendee>> emitter) throws Exception {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("load online member start...");
                }

                ArrayList<Attendee> attendees = new ArrayList<>();
                Map<String, Attendee> attendeeMap = ClassroomEngine.getEngine().getMembers();
                if (attendeeMap != null && attendeeMap.size() > 0) {
                    String myAccountId = AccountDataManager.getAccountID(getContext());

                    for (Attendee attendee : attendeeMap.values()) {

                        if (attendee.accountId.equals(myAccountId)) {
                            continue;
                        }
                        attendees.add(attendee);
                    }
                }


                Attendee attendee = new Attendee();
                attendee.name = classroomEngine.getClassTitle();
                attendee.accountId = classroomEngine.getCtlSession().cls.id;

                attendees.add(0, attendee);


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
                        shareToAdapter = new ShareToAdapter(getContext(), attendees);
                        listView.setAdapter(shareToAdapter);
                    }
                });

    }

    public ArrayList<Attendee> getChoiceItems() {
        ArrayList<Attendee> datas = null;
        long[] ids = listView.getCheckItemIds();
        if (ids != null && ids.length > 0 && shareToAdapter != null) {
            datas = new ArrayList<>(ids.length);
            for (int i = 0; i < ids.length; i++) {
                Attendee item = shareToAdapter.getItem((int) ids[i]);
                datas.add(item);
            }
        }

        return datas;
    }

    private void ok(Attendee attendee) {


        Fragment target = getTargetFragment();
        if (target != null) {
            Intent intent = new Intent();
            intent.putExtra(CTLConstant.EXTRA_MEMBER, attendee);
            target.onActivityResult(CTLConstant.REQUEST_CHOOSE_MEMBER, Activity.RESULT_OK, intent);
        }
    }


    private void toShare(ArrayList<Attendee> attendees) {

    }
}
