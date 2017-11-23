package cn.xiaojs.xma.ui.contact2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.provider.DataObserver;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.ui.base2.Base2Fragment;
import cn.xiaojs.xma.ui.classroom2.chat.SingleSessionFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.ContactsWhitIndex;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;
import cn.xiaojs.xma.ui.contact2.query.FriendsDataProvider;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class FriendsFragment extends Base2Fragment {

    @BindView(R.id.contact_listview)
    ListView listView;
    @BindView(R.id.letter_index)
    LetterIndexView letterIndexView;

    private LivIndex livIndex;
    private FriendsAdapter friendsAdapter;

    private DataProvider dataProvider;

    private int choiceMode;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact2_friends, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        choiceMode = getArguments() == null?
                ListView.CHOICE_MODE_NONE
                : getArguments().getInt(CTLConstant.EXTRA_CHOICE_MODE, ListView.CHOICE_MODE_NONE);

        listView.setChoiceMode(choiceMode);

        dataProvider = DataProvider.getProvider(getContext());
        dataProvider.registesObserver(dataObserver);
        if (dataProvider.isCompleted()) {
            loadData();
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


    @OnItemClick({R.id.contact_listview})
    void onListItemClick(int position) {
        if (friendsAdapter != null && choiceMode == ListView.CHOICE_MODE_NONE) {
            FriendItem item = (FriendItem) friendsAdapter.getItem(position);
            SingleSessionFragment.invoke(getFragmentManager(),
                    item.contact.id, item.contact.name, item.contact.followType);
        }

    }

    private void loadData() {

        //showLoadingStatus();

        FriendsDataProvider provider = new FriendsDataProvider(getContext());
        boolean filter = choiceMode == ListView.CHOICE_MODE_NONE? false : true;
        if (filter) {
            provider.setFilter(filter);
        }

        provider.loadFriends(dataProvider, friendDataReciver);

    }

    public ArrayList<AbsContactItem> getChoiceItems() {
        ArrayList<AbsContactItem> datas = null;
        long[] ids = listView.getCheckItemIds();
        if (ids != null && ids.length > 0) {
            datas = new ArrayList<>(ids.length);
            for(int i=0; i<ids.length; i++) {
                AbsContactItem item = friendsAdapter.getItem((int) ids[i]);
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
    };

    private Consumer<ContactsWhitIndex> friendDataReciver
            = new Consumer<ContactsWhitIndex>() {
        @Override
        public void accept(ContactsWhitIndex cwIndex) throws Exception {

            if (getActivity() == null)
                return;

            if (cwIndex.contacts != null && cwIndex.contacts.size() > 0) {

                hiddenTips();

                friendsAdapter = new FriendsAdapter(getContext(), cwIndex.contacts, choiceMode);
                listView.setAdapter(friendsAdapter);

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
