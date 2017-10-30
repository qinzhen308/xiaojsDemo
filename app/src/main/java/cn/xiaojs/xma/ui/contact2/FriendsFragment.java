package cn.xiaojs.xma.ui.contact2;

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
import cn.xiaojs.xma.ui.base2.Base2Fragment;
import cn.xiaojs.xma.ui.contact2.model.ContactsWhitIndex;
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

        loadData();

    }

    @OnItemClick({R.id.contact_listview})
    void onListItemClick(int position) {
       //TODO 进入会话列表
    }

    private void loadData() {

        showLoadingStatus();

        FriendsDataProvider provider = new FriendsDataProvider(getContext());
        provider.loadFriends(friendDataReciver);

    }

    private Consumer<ContactsWhitIndex> friendDataReciver
            = new Consumer<ContactsWhitIndex>() {
        @Override
        public void accept(ContactsWhitIndex cwIndex) throws Exception {

            if (getActivity() == null)
                return;

            showFinalTips();

            if (cwIndex.contacts != null && cwIndex.contacts.size()>0) {

                hiddenTips();

                FriendsAdapter friendsAdapter = new FriendsAdapter(getContext(), cwIndex.contacts);
                listView.setAdapter(friendsAdapter);
                livIndex = new LivIndex(listView, letterIndexView, null, null, cwIndex.indexMap);
                livIndex.show();
            }else {
                showFinalTips();
            }


        }
    };
}
