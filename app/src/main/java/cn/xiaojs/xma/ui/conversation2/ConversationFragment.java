package cn.xiaojs.xma.ui.conversation2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.provider.DataObserver;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.model.ctl.ScheduleOptions;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.ScanQrcodeActivity;
import cn.xiaojs.xma.ui.base2.Base2Fragment;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.chat.SingleSessionFragment;
import cn.xiaojs.xma.ui.classroom2.widget.SwapRecylcerView;
import cn.xiaojs.xma.ui.lesson.xclass.CreateClassActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.recordlesson.CreateRecordlessonActivity;
import cn.xiaojs.xma.ui.view.CommonPopupMenu2;
import cn.xiaojs.xma.util.JudgementUtil;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ConversationFragment extends Base2Fragment {

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.recyclerview)
    SwapRecylcerView recyclerView;
    @BindView(R.id.title_bar)
    TextView titleView;
    @BindView(R.id.btn_add)
    ImageView btnAdd;

    private ConversationAdapter adapter;
    private int titlebarHeight;

    private DataProvider dataProvider;

    private DataObserver dataObserver = new DataObserver() {
        @Override
        public void onLoadComplete() {

            if (XiaojsConfig.DEBUG) {
                Logger.d("received data provider load complete!");
            }

            if (refreshLayout !=null && refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }

            showApapter();
        }

        @Override
        public void onConversationUpdate(Contact contact, int index) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onConversationInsert(Contact contact, int insertIndex) {
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onConversationMove(Contact contact, int fromIndex, int toIndex) {
            adapter.notifyDataSetChanged();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation2, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titlebarHeight = titleView.getHeight();

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConversationAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setTouchEventListener(new SwapRecylcerView.TouchEventListener() {
            @Override
            public boolean patchTouchEvent(MotionEvent ev) {

                //FIXME
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    return adapter.closeOpendSwap();
                }

                return false;
            }
        });

        adapter.setItemClickListener(new ConversationAdapter.ItemClickListener() {
            @Override
            public void onItemClick(Contact contact, int position) {
                if (!TextUtils.isEmpty(contact.subtype)
                        && ConversationType.TypeName.PRIVATE_CLASS.equals(contact.subtype)){

                    String ticket = dataProvider.getClassTicket(contact.id);
                    if (!TextUtils.isEmpty(ticket)) {
                        Classroom2Activity.invoke(getActivity(), ticket);
                    }

                }else {
                    SingleSessionFragment.invoke(getFragmentManager(),contact.id, contact.title);
                }
            }
        });


        dataProvider = DataProvider.getProvider(getContext());
        dataProvider.registesObserver(dataObserver);
        if (dataProvider.isCompleted()) {
            showApapter();
        }else {
            showLoadingStatus();
        }


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DataManager.syncData(getContext());
            }
        });

    }

    @Override
    public void onDestroy() {

        if (dataProvider != null) {
            dataProvider.unregistesObserver(dataObserver);
            dataObserver = null;
        }

        super.onDestroy();
    }

    private void showApapter() {
        adapter.addContact(dataProvider.getConversations());
        if (adapter.getItemCount() > 1) {
            hiddenTips();
        } else {
            showFinalTips();
        }

        requestTodayScheduleCount();
    }


    @OnClick({R.id.btn_scan,R.id.btn_add})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.btn_scan:
                if (PermissionUtil.isOverMarshmallow() && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, MainActivity.PERMISSION_CODE);
                    PermissionGen.needPermission(getActivity() , MainActivity.PERMISSION_CODE,Manifest.permission.CAMERA);

                } else {
                    startActivity(new Intent(getActivity(), ScanQrcodeActivity.class));
                }
                break;
            case R.id.btn_add:
                showMenu(btnAdd);
                break;
        }

    }

    private void showMenu(View targetView) {
        CommonPopupMenu2 menu = new CommonPopupMenu2(getActivity());
        String[] items = getActivity().getResources().getStringArray(R.array.add_menu3);
        menu.setWidth(getActivity().getResources().getDimensionPixelSize(R.dimen.px280));
        menu.addTextItems(items);
        menu.setTextColor(Color.WHITE);
        menu.addImgItems(new Integer[]{R.drawable.ic_add_class3,R.drawable.ic_add_course});
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 0:
                        AnalyticEvents.onEvent(getActivity(),34);
                        if (JudgementUtil.checkTeachingAbility(getActivity())) {
                            getActivity().startActivity(new Intent(getActivity(), CreateClassActivity.class));
                        }
                        break;
                    case 1:             //开录播课
                        if (JudgementUtil.checkTeachingAbility(getActivity())) {
                            getActivity().startActivity(new Intent(getActivity(), CreateRecordlessonActivity.class));
                        }
                        break;
                }

            }
        });
        int offset = getActivity().getResources().getDimensionPixelSize(R.dimen.px68);
        menu.show(targetView, offset);
    }


    private void requestTodayScheduleCount() {

        if (adapter == null)
            return;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        long time = System.currentTimeMillis();
        long start = ScheduleUtil.ymdToTimeMill(year, month, day);
        long end = start + ScheduleUtil.DAY - 1000;
        if (XiaojsConfig.DEBUG) {
            Logger.d("----qz----start time mil=" + start + "---end time mil=" + end);
            Logger.d("----qz----start GMT+8:00 Time=" + ScheduleUtil.getDateYMDHMS(start) + "---end GMT+8:00 Time=" + ScheduleUtil.getDateYMDHMS(end));
            Logger.d("----qz----start UTC Time=" + ScheduleUtil.getUTCDate(start) + "---end UTC Time=" + ScheduleUtil.getUTCDate(end));
        }
        ScheduleOptions options = new ScheduleOptions.Builder().setStart(ScheduleUtil.getUTCDate(start))
                .setEnd(ScheduleUtil.getUTCDate(end))
                .build();
        LessonDataManager.getClassesSchedule(getActivity(), options, new APIServiceCallback<ScheduleData>() {
            @Override
            public void onSuccess(ScheduleData object) {

                if (object !=null && object.calendar != null && object.calendar.size()>0) {
                    Contact timetable = adapter.getItem(0);
                    timetable.unread = object.calendar.size();
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }




}
