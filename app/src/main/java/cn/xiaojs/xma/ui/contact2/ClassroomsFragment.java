package cn.xiaojs.xma.ui.contact2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.base2.Base2Fragment;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.chat.GroupSessionFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ClassroomsFragment extends Base2Fragment{

    @BindView(R.id.listview)
    ListView listView;

    ClassroomsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact2_classrooms, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        load();
    }

    @OnItemClick({R.id.listview})
    void onListItemClick(int position) {
        if (adapter!=null) {
            Contact contact = adapter.getItem(position);
            enterClass(getActivity(), contact.ticket);
        }
    }

    //进入教室
    private void enterClass(Activity context, String ticket) {
        Intent i = new Intent();
        //i.putExtra(Constants.KEY_TICKET, ticket);
        i.putExtra(CTLConstant.EXTRA_TICKET, ticket);
        i.setClass(context, Classroom2Activity.class);
        context.startActivity(i);
    }



    private void load() {
        showLoadingStatus();
        SocialManager.getContacts2(getContext(), new APIServiceCallback<ArrayList<ContactGroup>>() {
            @Override
            public void onSuccess(ArrayList<ContactGroup> contactGroups) {

                ArrayList<Contact> contacts = null;

                if (contactGroups != null && contactGroups.size() > 0) {
                    for (ContactGroup cg : contactGroups) {
                        if (cg.set.equals("classes")) {
                            contacts = cg.collection;
                            break;
                        }
                    }
                }

                if (contacts != null && contacts.size() > 0) {
                    hiddenTips();
                    adapter = new ClassroomsAdapter(getContext(), contacts);
                    listView.setAdapter(adapter);
                }else {
                    showFinalTips();
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                showFinalTips();

            }
        });
    }
}
