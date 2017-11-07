package cn.xiaojs.xma.ui.classroom.page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.util.BitmapUtils;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class WhiteboardManagerItemView extends RelativeLayout implements IViewModel<WhiteboardModel> {

    WhiteboardModel mData;
    @BindView(R.id.iv_white_board)
    ImageView ivWhiteBoard;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_more)
    ImageView btnMore;
    @BindView(R.id.selector)
    View selector;


    public WhiteboardManagerItemView(Context context) {
        super(context);
        init();
    }

    public WhiteboardManagerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=(int)(9*width/16.0f);
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height,MeasureSpec.EXACTLY));
        setMeasuredDimension(width,height);
    }

    private void init() {
        inflate(getContext(), R.layout.layout_whiteboard_manager_item, this);
        ButterKnife.bind(this);
    }


    @Override
    public void bindData(int position, WhiteboardModel data) {
        mData = data;
        if(data.isSelected){
            selector.setVisibility(VISIBLE);
        }else {
            selector.setVisibility(GONE);
        }
        tvTitle.setText(mData.boardItem.title);
        ivWhiteBoard.setImageBitmap(BitmapUtils.base64ToBitmapWithPrefix(mData.boardItem.snapshot));
    }


    @OnClick({R.id.iv_white_board, R.id.btn_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_white_board:
                break;
            case R.id.btn_more:
                break;
        }
    }
}
