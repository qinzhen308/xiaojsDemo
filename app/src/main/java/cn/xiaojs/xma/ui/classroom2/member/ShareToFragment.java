package cn.xiaojs.xma.ui.classroom2.member;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom2.base.BaseRoomFragment;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.util.BitmapUtils;
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

    private Bitmap targetBitmap;
    private MovieFragment rootFragment;
    private DataProvider dataProvider;

    public void setTargetBitmap(Bitmap targetBitmap) {
        this.targetBitmap = targetBitmap;
    }

    public void setRootFragment(MovieFragment rootFragment) {
        this.rootFragment = rootFragment;
    }

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
        dataProvider = DataProvider.getProvider(getContext());
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        load();
    }

    @OnClick({R.id.ok_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.ok_btn:
                ok();
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
                        attendee.ctype = Communications.TalkType.PEER;
                        attendees.add(attendee);
                    }
                }


                Attendee attendee = new Attendee();
                attendee.name = classroomEngine.getClassTitle();
                attendee.accountId = classroomEngine.getCtlSession().cls.id;
                attendee.ctype = Communications.TalkType.OPEN;

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

    private void ok() {

        //Fragment target = getTargetFragment();
        //if (target != null) {
            Intent intent = new Intent();
            ArrayList<Attendee> atts = getChoiceItems();
            if (shareToAdapter != null && atts != null && atts.size() > 0) {
                toShare(atts, targetBitmap);
            }

            //target.onActivityResult(CTLConstant.REQUEST_CHOOSE_MEMBER, Activity.RESULT_OK, intent);
        //}


    }


    private void toShare(final ArrayList<Attendee> attendees, final Bitmap bitmap) {

        showProgress(true);

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> e) throws Exception {

                Bitmap resizeBmp = BitmapUtils.resizeDownBySideLength(bitmap, CTLConstant.SHARE_IMG_SIZE, false);
                String imgEncode = ClassroomBusiness.bitmapToBase64(resizeBmp);

                String mid = AccountDataManager.getAccountID(getContext());

                for (Attendee att : attendees) {
                    Talk talkBean = new Talk();
                    talkBean.from = mid;
                    talkBean.body = new Talk.TalkContent();
                    talkBean.body.text = imgEncode;
                    talkBean.body.contentType = Communications.ContentType.STYLUS;
                    talkBean.time = System.currentTimeMillis();
                    talkBean.to = att.accountId;
                    talkBean.type = att.ctype;


                    XMSManager.sendTalk(getContext().getApplicationContext(),
                            true, talkBean, new EventCallback<TalkResponse>() {
                        @Override
                        public void onSuccess(TalkResponse talkResponse) {

                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {

                        }
                    });

                    if (Communications.TalkType.OPEN == att.ctype) {
                        talkBean.name = att.name;
                        updateChatSession(talkBean);
                    }else {
                        updateConveration(talkBean, att.name);
                    }
                }

                e.onNext(new Object());
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {

                if (getActivity() == null)
                    return;
                cancelProgress();
                Toast.makeText(getContext(),"分享成功",Toast.LENGTH_SHORT).show();
                rootFragment.exitSlidePanel();
            }
        });

    }

    private void updateChatSession(Talk talk) {
        if (talk == null)
            return;

        Intent i = new Intent(CTLConstant.ACTION_SEND_TALK);
        i.putExtra(CTLConstant.EXTRA_TALK, talk);
        getContext().sendBroadcast(i);
    }

    private void updateConveration(Talk talkItem, String name) {
        Contact contact = new Contact();
        contact.id = talkItem.to;
        contact.name = name;
        contact.title = name;
        contact.lastMessage = talkItem.body.text;
        contact.lastTalked = talkItem.time;
        contact.unread = 0;

        dataProvider.moveOrInsertConversation(contact);
    }
}
