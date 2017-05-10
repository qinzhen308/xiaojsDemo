package cn.xiaojs.xma.ui.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/10.
 */

public class MessageCenterActivity extends FragmentActivity {


    @BindView(R.id.middle_view)
    TextView middleView;
    Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_fragment_container);
        mUnbinder=ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    private void initView() {
        middleView.setText("消息");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.base_content, new MessageFragment()).commit();
    }

    @OnClick({R.id.left_image})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.left_image:
                finish();
                break;
        }
    }


}
