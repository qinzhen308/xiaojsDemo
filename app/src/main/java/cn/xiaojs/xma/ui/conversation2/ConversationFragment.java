package cn.xiaojs.xma.ui.conversation2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.ScanQrcodeActivity;
import cn.xiaojs.xma.ui.base2.Base2Fragment;
import cn.xiaojs.xma.ui.classroom2.widget.SwapRecylcerView;
import cn.xiaojs.xma.ui.search.SearchActivity;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ConversationFragment extends Base2Fragment
        implements ConversationDataProvider.OnDataListener{

    @BindView(R.id.recyclerview)
    SwapRecylcerView recyclerView;

    private ConversationAdapter adapter;
    private ConversationDataProvider dataProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //dataProvider = ConversationDataProvider.getProvider(getContext());
        //dataProvider.setDataListener(this);
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

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConversationAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setTouchEventListener(new SwapRecylcerView.TouchEventListener() {
            @Override
            public boolean patchTouchEvent(MotionEvent ev) {

                if (ev.getAction()== MotionEvent.ACTION_DOWN) {
                    return adapter.closeOpendSwap();
                }

                return false;
            }
        });

        load();
        //dataProvider.startLoad();
    }

    @Override
    public void onDataLoadComplete(ArrayList<Contact> conversations) {

    }

    private void load() {
        showLoadingStatus();
        SocialManager.getContacts2(getContext(), new APIServiceCallback<ArrayList<ContactGroup>>() {
            @Override
            public void onSuccess(ArrayList<ContactGroup> contactGroups) {

                if (contactGroups != null && contactGroups.size() > 0) {
                    for (ContactGroup cg : contactGroups) {
                        if (cg.set.equals("dialogs")) {
                            adapter.addContact(cg.collection);
                            break;
                        }
                    }
                }

                if (adapter.getItemCount() > 1) {
                    hiddenTips();
                } else {
                    showFinalTips();
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                showFinalTips();
            }
        });
    }


    public void updateConversation(String id) {

        if (adapter != null && adapter.getItemCount()>0) {

            ArrayList<Contact> contacts = adapter.getContacts();



        }else {
            //TODO 直接添加一项，或者直接刷新接口
        }
    }

    /*@OnClick({R.id.btn_scan,R.id.btn_add})
    public void onViewClick(View v){
        switch (v.getId()){
            case R.id.btn_scan2:
                if (PermissionUtil.isOverMarshmallow() && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    mContext.requestPermissions(new String[]{Manifest.permission.CAMERA}, MainActivity.PERMISSION_CODE);
                    PermissionGen.needPermission(getActivity() , MainActivity.PERMISSION_CODE,Manifest.permission.CAMERA);

                } else {
                    startActivity(new Intent(getActivity(), ScanQrcodeActivity.class));
                }
                break;
            case R.id.btn_add:
                showMenu(btnAdd);
                break;
        }

    }*/


}
