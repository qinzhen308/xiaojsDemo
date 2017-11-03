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

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.provider.DataObserver;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.base2.Base2Fragment;
import cn.xiaojs.xma.ui.classroom2.widget.SwapRecylcerView;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ConversationFragment extends Base2Fragment {

    @BindView(R.id.recyclerview)
    SwapRecylcerView recyclerView;
    @BindView(R.id.title_bar)
    TextView titleView;

    private ConversationAdapter adapter;
    private int titlebarHeight;

    private DataProvider dataProvider;

    private DataObserver dataObserver = new DataObserver() {
        @Override
        public void onLoadComplete() {

            if (XiaojsConfig.DEBUG) {
                Logger.d("received data provider load complete!");
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


        dataProvider = DataProvider.getProvider(getContext());
        dataProvider.registesObserver(dataObserver);
        if (dataProvider.isCompleted()) {
            showApapter();
        }else {
            showLoadingStatus();
        }

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
    }




}
