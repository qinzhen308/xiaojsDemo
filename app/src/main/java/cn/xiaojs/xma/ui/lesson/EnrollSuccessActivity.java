package cn.xiaojs.xma.ui.lesson;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
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
                finish();
                break;
            case R.id.enter_btn:
                enterClass(ticket);
                finish();
                break;
            case R.id.look_btn:
                enterMyLesson();
                finish();
                break;
        }
    }


    //进入教室
    private void enterClass(String ticket) {

        if (TextUtils.isEmpty(ticket)) {
            Toast.makeText(this,"进入教室失败",Toast.LENGTH_SHORT).show();
        }

        Intent i = new Intent();
        i.putExtra(Constants.KEY_TICKET, ticket);
        i.setClass(this, ClassroomActivity.class);
        this.startActivity(i);
    }

    private void enterMyLesson() {
        startActivity(new Intent(this,ClassesListActivity.class));
    }
}
