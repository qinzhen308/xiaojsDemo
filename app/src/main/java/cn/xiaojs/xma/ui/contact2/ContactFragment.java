package cn.xiaojs.xma.ui.contact2;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.base.BaseDialogFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ContactFragment extends DialogFragment {


    @BindView(R.id.back_btn)
    ImageView backBtnView;
    @BindView(R.id.ok_btn)
    TextView okBtnView;

    @BindView(R.id.tab_bar)
    RadioGroup tabGroup;
    @BindView(R.id.tab_viewpager)
    ViewPager viewPager;


    private ArrayList<Fragment> fragmentList;
    private int checkMode;

    private FriendsFragment friendsFragment;
    private ClassroomsFragment classroomsFragment;

    private ChoiceCompletedListener choiceDataListener;

    public interface ChoiceCompletedListener {
        void onChoiceData(boolean person, ArrayList<AbsContactItem> data);
    }


    public static void invokeWithChoice(FragmentManager manager, String exId, int choiceMode, ChoiceCompletedListener listener) {
        ContactFragment contactFragment = new ContactFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CTLConstant.EXTRA_ID, exId);
        bundle.putInt(CTLConstant.EXTRA_CHOICE_MODE, choiceMode);
        contactFragment.setArguments(bundle);
        contactFragment.setChoiceCompletedListener(listener);
        contactFragment.show(manager, "choice_contact");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getDialog() != null) {
            getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        View view = inflater.inflate(R.layout.fragment_contact2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getDialog() != null) {
            final Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout(-1, WindowManager.LayoutParams.MATCH_PARENT);
        }

        checkMode = getArguments() == null ?
                ListView.CHOICE_MODE_NONE
                : getArguments().getInt(CTLConstant.EXTRA_CHOICE_MODE, ListView.CHOICE_MODE_NONE);


        if (checkMode == ListView.CHOICE_MODE_NONE) {
            okBtnView.setVisibility(View.GONE);
            backBtnView.setVisibility(View.GONE);
        }else {
            okBtnView.setVisibility(View.VISIBLE);
            backBtnView.setVisibility(View.VISIBLE);
        }

        fragmentList = new ArrayList<>(2);

        friendsFragment = new FriendsFragment();
        friendsFragment.setArguments(getArguments());
        fragmentList.add(friendsFragment);

        classroomsFragment = new ClassroomsFragment();
        classroomsFragment.setArguments(getArguments());
        fragmentList.add(classroomsFragment);

        FragmentManager fragmentManager = getDialog()==null? getFragmentManager() : getChildFragmentManager();

        FrgStatePageAdapter pageAdapter = new FrgStatePageAdapter(fragmentManager);
        pageAdapter.setList(fragmentList);
        viewPager.setAdapter(pageAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabGroup.check(R.id.tab_my);
                        break;
                    case 1:
                        tabGroup.check(R.id.tab_class);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @OnClick({R.id.tab_my, R.id.tab_class, R.id.ok_btn, R.id.back_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.tab_my:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab_class:
                viewPager.setCurrentItem(1);
                break;
            case R.id.ok_btn:
                collectChoiceData();
                dismiss();
                break;
            case R.id.back_btn:
                dismiss();
                break;
        }
    }

    public void setChoiceCompletedListener(ChoiceCompletedListener choiceDataListener) {
        this.choiceDataListener = choiceDataListener;
    }

    private void collectChoiceData() {

        if (choiceDataListener != null) {

            ArrayList<AbsContactItem> datas;

            if (tabGroup.getCheckedRadioButtonId() == R.id.tab_class) {
                datas = classroomsFragment.getChoiceItems();
                choiceDataListener.onChoiceData(false, datas);
            } else {
                datas = friendsFragment.getChoiceItems();
                choiceDataListener.onChoiceData(true, datas);
            }

        }

    }


    class FrgStatePageAdapter extends FragmentStatePagerAdapter {

        public FrgStatePageAdapter(FragmentManager fm) {
            super(fm);
        }

        private ArrayList<Fragment> listFrg = new ArrayList<Fragment>();

        public void setList(ArrayList<Fragment> listFrg) {
            this.listFrg = listFrg;
        }

        @Override
        public Fragment getItem(int arg0) {
            return listFrg.get(arg0);
        }

        @Override
        public int getCount() {
            return listFrg.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }
    }
}
