package cn.xiaojs.xma.ui.contact2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

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
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.ClassItem;
import cn.xiaojs.xma.ui.contact2.model.ContactsWhitIndex;
import cn.xiaojs.xma.ui.contact2.query.FriendsDataProvider;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ClassroomsFragment extends Base2Fragment {

    @BindView(R.id.listview)
    ListView listView;
    @BindView(R.id.letter_index)
    LetterIndexView letterIndexView;

    ClassroomsAdapter adapter;
    private LivIndex livIndex;

    private DataProvider dataProvider;
    private int choiceMode;
    private String exId;


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

        choiceMode = getArguments() == null ?
                ListView.CHOICE_MODE_NONE
                : getArguments().getInt(CTLConstant.EXTRA_CHOICE_MODE, ListView.CHOICE_MODE_NONE);
        exId = getArguments() == null ? "" : getArguments().getString(CTLConstant.EXTRA_ID, "");
        listView.setChoiceMode(choiceMode);

        adapter = new ClassroomsAdapter(getContext(), choiceMode);
        listView.setAdapter(adapter);

        dataProvider = DataProvider.getProvider(getContext());
        dataProvider.registesObserver(dataObserver);
        if (dataProvider.isCompleted()) {
            loadData();
        } else {
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
        if (adapter != null && choiceMode == ListView.CHOICE_MODE_NONE) {
            ClassItem contactItem = (ClassItem) adapter.getItem(position);
            enterClass(getActivity(), contactItem.contact.ticket);
        }
    }

    private void loadData() {
        FriendsDataProvider provider = new FriendsDataProvider(getContext());
        boolean filter = choiceMode == ListView.CHOICE_MODE_NONE? false : true;
        if (filter) {
            provider.setFilter(filter);
            provider.setExcludeClassId(exId);
        }
        provider.loadClasses(dataProvider, classesConsumer);
    }

    private void showAdpater(ArrayList<AbsContactItem> classes) {
        adapter.addDatas(classes);
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

    public ArrayList<AbsContactItem> getChoiceItems() {
        ArrayList<AbsContactItem> datas = null;
        long[] ids = listView.getCheckItemIds();
        if (ids != null && ids.length > 0) {
            datas = new ArrayList<>(ids.length);
            for (int i = 0; i < ids.length; i++) {
                AbsContactItem item = adapter.getItem((int) ids[i]);
                datas.add(item);
            }
        }

        return datas;
    }


    private DataObserver dataObserver = new DataObserver() {
        @Override
        public void onLoadComplete() {
            loadData();
        }

        @Override
        public void onClassesUpdate(int action, Contact contact) {
            switch (action) {
                case DataProvider.ACTION_ADD:
                case DataProvider.ACTION_REMOVE:
                    loadData();
                    break;
                case DataProvider.ACTION_UPDATE:
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    break;

            }
        }

    };

    private Consumer<ContactsWhitIndex> classesConsumer = new Consumer<ContactsWhitIndex>() {
        @Override
        public void accept(ContactsWhitIndex cwIndex) throws Exception {

            if (getActivity() == null)
                return;
            if (cwIndex.contacts != null && cwIndex.contacts.size() > 0) {

                hiddenTips();

                showAdpater(cwIndex.contacts);

                //int lsize = cwIndex.letters.size();

                //letterIndexView.setLetters(cwIndex.letters.toArray(new String[lsize]));
                livIndex = new LivIndex(listView, letterIndexView, null, null, cwIndex.indexMap);
                livIndex.show();
            } else {
                showFinalTips();
            }


        }
    };


}
