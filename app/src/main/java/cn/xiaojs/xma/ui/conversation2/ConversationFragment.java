package cn.xiaojs.xma.ui.conversation2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.base2.Base2Fragment;
import cn.xiaojs.xma.ui.classroom2.widget.SwapRecylcerView;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ConversationFragment extends Base2Fragment
        implements ConversationDataProvider.OnDataChangedListener {

    @BindView(R.id.recyclerview)
    SwapRecylcerView recyclerView;
    @BindView(R.id.title_bar)
    TextView titleView;

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

    @Override
    public void onDataUpdate(Contact contact, int index) {
        adapter.notifyItemChanged(index);
    }

    //    private void load() {
//        showLoadingStatus();
//        SocialManager.getContacts2(getContext(), new APIServiceCallback<ArrayList<ContactGroup>>() {
//            @Override
//            public void onSuccess(ArrayList<ContactGroup> contactGroups) {
//
//                if (contactGroups != null && contactGroups.size() > 0) {
//                    for (ContactGroup cg : contactGroups) {
//                        if (cg.set.equals("dialogs")) {
//                            adapter.addContact(cg.collection);
//                            break;
//                        }
//                    }
//                }
//
//                if (adapter.getItemCount() > 1) {
//                    hiddenTips();
//                } else {
//                    showFinalTips();
//                }
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//                showFinalTips();
//            }
//        });
//    }
//
//
//    public void updateConversation(String id) {
//
//        if (adapter != null && adapter.getItemCount()>0) {
//
//            ArrayList<Contact> contacts = adapter.getContacts();
//
//
//
//        }else {
//            //TODO 直接添加一项，或者直接刷新接口
//        }
//
//
//    }


}
