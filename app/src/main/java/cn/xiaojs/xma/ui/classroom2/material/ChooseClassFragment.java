package cn.xiaojs.xma.ui.classroom2.material;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.loader.DataLoder;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class ChooseClassFragment extends BottomSheetFragment {


    @BindView(R.id.rlist)
    RecyclerView recyclerView;

    private ClassAdapter classAdapter;
    private ArrayList<Contact> classes;

    private String comeid;

    private DataProvider dataProvider;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_choose_class, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataProvider = DataProvider.getProvider(getContext());

        comeid = getArguments().getString(CTLConstant.EXTRA_DOC_ID);


        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        classes = new ArrayList<>();
        classAdapter = new ClassAdapter(getContext(), classes);
        recyclerView.setAdapter(classAdapter);

        showLoadingStatus();

        loadData();
    }


    @OnClick({R.id.back_btn, R.id.ok_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
            case R.id.ok_btn:
                ok();
                break;
        }
    }


    private void loadData() {


        ArrayList<Contact> ss = dataProvider.getClasses();
        if (ss == null || ss.size() <= 0) {
            showFinalTips();
            return;
        }

        hiddenTips();

        classes.addAll(ss);
        classAdapter.notifyDataSetChanged();

    }

    private void ok() {

        Contact contact = classAdapter.getCheckItem();
        contact.account = contact.id;
        Fragment target = getTargetFragment();
        if (target != null && contact != null) {
            Intent intent = new Intent();
            intent.putExtra(CTLConstant.EXTRA_CONTACT, contact);
            intent.putExtra(CTLConstant.EXTRA_DOC_ID, comeid);
            target.onActivityResult(CTLConstant.REQUEST_CHOOSE_CLASSES,
                    Activity.RESULT_OK, intent);
        }
        dismiss();

    }

}
