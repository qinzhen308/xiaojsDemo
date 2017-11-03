package cn.xiaojs.xma.ui.contact2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.provider.DataObserver;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.base2.Base2Fragment;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ClassroomsFragment extends Base2Fragment {

    @BindView(R.id.listview)
    ListView listView;

    ClassroomsAdapter adapter;

    private DataProvider dataProvider;

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

        adapter = new ClassroomsAdapter(getContext());
        listView.setAdapter(adapter);

        dataProvider = DataProvider.getProvider(getContext());
        dataProvider.registesObserver(dataObserver);
        if (dataProvider.isCompleted()) {
            showAdpater();
        }else {
            showLoadingStatus();
        }

    }

    @Override
    public void onDestroy() {

        if (dataObserver != null) {
            dataProvider.unregistesObserver(dataObserver);
        }

        super.onDestroy();
    }

    @OnItemClick({R.id.listview})
    void onListItemClick(int position) {
        if (adapter != null) {
            Contact contact = adapter.getItem(position);
            enterClass(getActivity(), contact.ticket);
        }
    }

    private void showAdpater() {
        adapter.addDatas(dataProvider.getConversations());
        if (adapter.getCount() > 0) {
            hiddenTips();
        } else {
            showFinalTips();
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


    private DataObserver dataObserver = new DataObserver() {
        @Override
        public void onLoadComplete() {
            showAdpater();
        }
    };


}
