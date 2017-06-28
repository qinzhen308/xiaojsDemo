package cn.xiaojs.xma.ui.lesson;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.haha.perflib.Main;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.IntentFlags;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.lesson.xclass.ClassesListActivity;

/**
 * Created by maxiaobao on 2017/5/25.
 */

public class EnrollSuccessActivity extends BaseActivity {

    public static final String EXTRA_TICKET = "ticket";
    public static final String EXTRA_FROM = "from";

    public static final int FROM_JION = 0x2;
    public static final int FROM_ENROLL = 0x1;

    @BindView(R.id.success_tips_view)
    TextView tipsView;

    private String ticket;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_enroll_success);
        setMiddleTitle(getString(R.string.enroll_result));

        ticket = getIntent().getStringExtra(EXTRA_TICKET);

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
                enterClass(ticket);
                break;
            case R.id.look_btn:
                enterMyLesson();
                break;
        }
    }


    //进入教室
    private void enterClass(String ticket) {
        MainActivity.invokeWithAction(this, MainActivity.ACTION_TO_CLASSROOM,ticket);
    }

    private void enterMyLesson() {
        MainActivity.invokeWithAction(this,MainActivity.ACTION_TO_MY_CLASSES);
    }
}
