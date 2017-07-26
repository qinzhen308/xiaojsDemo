package cn.xiaojs.xma.ui.recordlesson.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.recordedlesson.Section;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.recordlesson.model.RLDirectory;

/**
 * Created by Paul Z on 2017/7/26.
 */

public class RLSectionView extends LinearLayout implements IViewModel<Section> {



    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_lesson_count)
    TextView tvLessonCount;

    Section mData;

    @BindColor(R.color.main_orange)
    int main_orange;

    public RLSectionView(Context context) {
        super(context);
        init();
    }

    public RLSectionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RLSectionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutInflater.from(getContext()).inflate(R.layout.item_recorded_lesson_directory, this);
        ButterKnife.bind(this);
    }

    @Override
    public void bindData(int position, Section data) {
        mData=data;
        tvName.setText(data.title);
        SpannableString ss=new SpannableString("共" + mData.getChildrenCount() + "节课");
        ss.setSpan(new ForegroundColorSpan(main_orange),1,ss.length()-2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLessonCount.setText(ss);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
