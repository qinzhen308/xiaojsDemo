package cn.xiaojs.xma.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.ui.account.LoginActivity;

/**
 * Created by Paul Z on 2017/11/24.
 */

public class GuideActivity extends Activity {

    int[] imgsRes = {R.drawable.img_guide1, R.drawable.img_guide2, R.drawable.img_guide3};
    int[] indicatorIds = {R.id.tab1, R.id.tab2, R.id.tab3};
    View[] pages = new View[imgsRes.length];

    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.btn_enter)
    TextView btnEnter;
    @BindView(R.id.indicator)
    RadioGroup indicatorView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initView();
    }


    private void initView() {
        for (int i = 0; i < imgsRes.length; i++) {
            ImageView page = new ImageView(this);
            page.setScaleType(ImageView.ScaleType.CENTER);
            page.setPadding(getResources().getDimensionPixelSize(R.dimen.px20),
                    getResources().getDimensionPixelSize(R.dimen.px150),
                    getResources().getDimensionPixelSize(R.dimen.px20),
                    getResources().getDimensionPixelSize(R.dimen.px250));
            page.setImageResource(imgsRes[i]);
            pages[i] = page;
        }
        indicatorView.check(indicatorIds[0]);
        viewPager.setAdapter(new Adapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indicatorView.check(indicatorIds[position]);
                if(position==imgsRes.length-1){
                    btnEnter.setVisibility(View.VISIBLE);
                    indicatorView.setVisibility(View.GONE);
                }else {
                    btnEnter.setVisibility(View.GONE);
                    indicatorView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.btn_enter, R.id.btn_skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_enter:
            case R.id.btn_skip:
                jump();
                break;
        }
    }


    class Adapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imgsRes.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = pages[position];
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(pages[position]);
        }
    }

    private void jump() {
        if (AccountDataManager.isLogin(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

}
