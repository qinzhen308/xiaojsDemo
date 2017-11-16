package cn.xiaojs.xma.ui.search.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.ui.CommonWebActivity;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.StringUtil;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class SRClassView extends RelativeLayout implements IViewModel<SearchResultV2> {

    SearchResultV2 mData;
    @BindView(R.id.tv_label)
    TextView tvLabel;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;

    private CircleTransform circleTransform;

    @BindColor(R.color.orange_point)
    int heightlightColor;

    public SRClassView(Context context) {
        super(context);
        init();
    }

    public SRClassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_search_result_class, this);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());

    }


    @Override
    public void bindData(int position, SearchResultV2 data) {
        mData = data;
        String tag=((Activity)getContext()).getIntent().getStringExtra("extra_search_keywords");
        if(!ArrayUtil.isEmpty(mData.advisers)&&mData.advisers[0]!=null){
            tvName.setVisibility(VISIBLE);
            ivAvatar.setVisibility(VISIBLE);
            tvName.setText(mData.advisers[0].name);
//            tvName.setText(StringUtil.setHighlightText2(mData.advisers[0].name,mData._name,heightlightColor));
            Glide.with(getContext())
                    .load(Account.getAvatar(mData.advisers[0].getId(), 300))
                    .bitmapTransform(circleTransform)
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(ivAvatar);
        }else {
            tvName.setVisibility(INVISIBLE);
            ivAvatar.setVisibility(INVISIBLE);
        }

        tvTitle.setText(StringUtil.setHighlightText2(mData.title,mData._title,heightlightColor));
        if(mData.createdOn==null){
            tvDate.setVisibility(INVISIBLE);
        }else {
            tvDate.setVisibility(VISIBLE);
            tvDate.setText("创建时间 "+ScheduleUtil.getDateYMD(mData.createdOn));
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=ApiManager.getShareLessonUrl(mData.id,Account.TypeName.CLASS_LESSON);
                if(url.contains("?")){
                    url+="&app=android";
                }else {
                    url+="?app=android";
                }
                CommonWebActivity.invoke(getContext(),"",url);
            }
        });
    }




}
