package cn.xiaojs.xma.ui.classroom.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;

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


    public WhiteboardManagerItemView(Context context) {
        super(context);
        init();
    }

    public WhiteboardManagerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_whiteboard_manager_item, this);
        ButterKnife.bind(this);
    }


    @Override
    public void bindData(int position, WhiteboardModel data) {
        mData = data;

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
