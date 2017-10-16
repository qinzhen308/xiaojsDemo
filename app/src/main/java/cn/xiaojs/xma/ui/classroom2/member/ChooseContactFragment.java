package cn.xiaojs.xma.ui.classroom2.member;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.db.ContactDao;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class ChooseContactFragment extends BottomSheetFragment {


    @BindView(R.id.rlist)
    RecyclerView recyclerView;

    private ArrayList<Contact> contactList;

    private ContactMulAdapter adapter;

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_choose_contact, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        contactList = new ArrayList<>();
        adapter = new ContactMulAdapter(getContext(), contactList);

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        loadContacts();

    }

    @OnClick({R.id.back_btn,R.id.ok_btn})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
            case R.id.ok_btn:
                dismiss();
                break;
        }
    }

    private void loadContacts() {
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<Object, ArrayList<Contact>>() {

                    @Override
                    public ArrayList<Contact> apply(@NonNull Object o) throws Exception {

                        ContactDao dao = new ContactDao();
                        return dao.getContacts(getContext());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Contact>>() {
                    @Override
                    public void accept(ArrayList<Contact> contacts) throws Exception {

                        if (contacts !=null && contacts.size()>0) {
                            contactList.clear();
                            contactList.addAll(contacts);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }


}
