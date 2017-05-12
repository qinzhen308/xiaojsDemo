package cn.xiaojs.xma.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/4/17.
 */

public class GuideBrowseActivity extends Activity {

    public static final String EXTRA_TEACHER = "tea";


    @BindView(R.id.image_usage)
    ImageView imageView;


    private int[] images;

    private int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_browse);
        ButterKnife.bind(this);


//        boolean teacher = getIntent().getBooleanExtra(EXTRA_TEACHER, false);
//        if (teacher) {
//            images = new int[]{R.drawable.usage_teacher_1,
//                    R.drawable.usage_teacher_2,
//                    R.drawable.usage_teacher_3,
//                    R.drawable.usage_teacher_4};
//        } else {
//            images = new int[]{R.drawable.usage_student_1,
//                    R.drawable.usage_student_2,
//                    R.drawable.usage_student_3,
//                    R.drawable.usage_student_4,
//                    R.drawable.usage_student_5
//            };
//        }
//
//
//
//
//        setImageShow(images[index]);

    }

    @OnClick({R.id.btn_close, R.id.root_layout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                finish();
                break;
            case R.id.root_layout:
                showNext();
                break;
        }

    }

    public void showNext() {

        index++;

        if (index >= images.length) {
            finish();
            return;
        }

        setImageShow(images[index]);

    }

    public void setImageShow(@DrawableRes int res) {

        Glide.with(this).load(res).into(imageView);

        //imageView.setBackgroundResource(images[index]);
    }

    @Override
    public void onBackPressed() {

    }


}
