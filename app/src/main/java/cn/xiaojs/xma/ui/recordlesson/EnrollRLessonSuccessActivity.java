package cn.xiaojs.xma.ui.recordlesson;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.base.BaseActivity;

/**
 * Created by Paul Z on 2017/8/4.
 * 加录播课成功页面
 */

public class EnrollRLessonSuccessActivity extends BaseActivity {

    public static final String EXTRA_KEY_ID = "extra_key_id";
    public static final String EXTRA_FROM = "from";

    public static final int FROM_JION = 0x2;
    public static final int FROM_ENROLL = 0x1;

    @BindView(R.id.success_tips_view)
    TextView tipsView;

    private String id;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_enroll_rlesson_success);
        setMiddleTitle(getString(R.string.enroll_result));

        id = getIntent().getStringExtra(EXTRA_KEY_ID);

        int from = getIntent().getIntExtra(EXTRA_FROM, FROM_ENROLL);

        if (from == FROM_JION) {
            tipsView.setText(R.string.join_result_ok);
        } else {
            tipsView.setText(R.string.enroll_result_ok);
        }

    }


    @OnClick({R.id.left_image, R.id.enter_btn,R.id.look_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                MainActivity.invokeWithAction(this,null);
                break;
            case R.id.enter_btn:
                enterDir(id);
                break;
            case R.id.look_btn:
                MainActivity.invokeWithAction(this,null);
                break;
        }
    }


    private void enterDir(String id) {
        MainActivity.invokeWithAction(this, MainActivity.ACTION_TO_RECORDED_LESSONS_DIR,id);
    }

    public static void invoke(Context context,String id){
        Intent intent=new Intent(context,EnrollRLessonSuccessActivity.class);
        intent.putExtra(EXTRA_KEY_ID,id);
        context.startActivity(intent);
    }

}
