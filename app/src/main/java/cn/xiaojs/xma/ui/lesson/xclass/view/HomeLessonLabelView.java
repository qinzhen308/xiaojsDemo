package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class HomeLessonLabelView extends LinearLayout implements IViewModel<Object> {


    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_total_lessons)
    TextView tvTotalLessons;

    View noData;

    @BindColor(R.color.main_orange)
    int c_main_orange;
    public HomeLessonLabelView(Context context) {
        super(context);
        init();
    }

    public HomeLessonLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.item_lesson_schedule_date, this);
        noData=findViewById(R.id.no_data);
        ButterKnife.bind(this);
    }


    @Override
    public void bindData(Object data) {
        SpannableString ss=new SpannableString("共"+12+"节课");
//        ss.setSpan(new RelativeSizeSpan(0.5f),1,ss.length()-2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(c_main_orange),1,ss.length()-2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvTotalLessons.setText(ss);
        //test
        showNoLesson(new Random().nextInt(2)==0);
    }


    public void showNoLesson(boolean isShow){
        if(isShow){
            noData.setVisibility(View.VISIBLE);
        }else {
            noData.setVisibility(View.GONE);
        }
    }

}
