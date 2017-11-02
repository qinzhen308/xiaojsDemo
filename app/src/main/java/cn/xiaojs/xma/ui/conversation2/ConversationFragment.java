package cn.xiaojs.xma.ui.conversation2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.ScanQrcodeActivity;
import cn.xiaojs.xma.ui.base2.Base2Fragment;
import cn.xiaojs.xma.ui.classroom2.widget.SwapRecylcerView;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.xclass.CreateClassActivity;
import cn.xiaojs.xma.ui.recordlesson.CreateRecordlessonActivity;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.ui.view.CommonPopupMenu1;
import cn.xiaojs.xma.util.JudgementUtil;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ConversationFragment extends Base2Fragment
        implements ConversationDataProvider.OnDataChangedListener {

    @BindView(R.id.recyclerview)
    SwapRecylcerView recyclerView;
    @BindView(R.id.title_bar)
    TextView titleView;
    @BindView(R.id.btn_add)
    ImageView btnAdd;

    private ConversationAdapter adapter;
    private ConversationDataProvider dataProvider;
    private int titlebarHeight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataProvider = ConversationDataProvider.getProvider(getContext());
        dataProvider.addDataChangedListener(this);
    }

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

        adapter.setContacts(dataProvider.getConversations());
        if (adapter.getItemCount() > 1) {
            hiddenTips();
        } else {
            showFinalTips();
        }


    }

    @Override
    public void onDataMoveOrInsert(ArrayList<Contact> conversations,
                                   boolean snew, Contact contact, int index, int oldIndex) {

        adapter.notifyDataSetChanged();

//        if (snew) {
//            adapter.notifyItemInserted(index);
//        }else {
//
//            if (index == oldIndex) {
//                adapter.notifyItemChanged(index);
//            }else {
//                adapter.notifyItemMoved(oldIndex, index);
//            }
//        }
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
        CommonPopupMenu1 menu = new CommonPopupMenu1(getActivity());
        String[] items = getActivity().getResources().getStringArray(R.array.add_menu3);
        menu.setWidth(getActivity().getResources().getDimensionPixelSize(R.dimen.px280));
        menu.addTextItems(items);
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


    @Override
    public void onDataUpdate(Contact contact, int index) {
        adapter.notifyItemChanged(index);
    }




}
