package cn.xiaojs.xma.ui.classroom2;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.page.BoardCollaborateFragment;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;

/**
 * Created by maxiaobao on 2017/9/25.
 */

public class IdleFragment extends MovieFragment {


    @BindView(R.id.p_bottom_class_name)
    TextView classNameView;
    @BindView(R.id.iv_whiteboard_preview)
    ImageView ivWhiteboardPreview;

    BoardCollaborateFragment whiteboardFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whiteboardFragment=BoardCollaborateFragment.createInstance("");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classroom2_idle, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controlLand.setVisibility(View.GONE);
<<<<<<< HEAD
        initDefaultBoard();
=======
        pBottomClassnameView.setText(classroomEngine.getRoomTitle());
        startOrStopLiveView.setText("开始直播");
>>>>>>> 5ede3e477a9094db8b033e8f0793f51c8995e1f6
    }


    @Override
    public void closeMovie() {
        //do nothing
    }

    @Override
    public void onRotate(int orientation) {
        controlHandleOnRotate(orientation);
        if(orientation== Configuration.ORIENTATION_LANDSCAPE){
            ivWhiteboardPreview.setImageBitmap(null);
            ivWhiteboardPreview.setVisibility(View.GONE);
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_idle_container, whiteboardFragment)
                    .addToBackStack("board_default")
                    .commit();
        }else {
            if(whiteboardFragment.isAdded()&&whiteboardFragment.isInLayout()){
                getChildFragmentManager().popBackStack();
            }
            ivWhiteboardPreview.setImageBitmap(whiteboardFragment.preview());
            ivWhiteboardPreview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClosed() {
        exitSlidePanel();
    }

    @Override
    public void onOpened() {

    }

    @Override
    public void onTopbackClick(View view, boolean land) {
        back();
    }

    @Override
    public void onStartLiveClick(View view) {
        requestLive();
    }

    @Override
    public void onNewboardClick(View view) {

    }
    public void onStartOrStopLiveClick(View view) {
        requestLive();
    }

    private void initDefaultBoard(){
        /*getChildFragmentManager()
                .beginTransaction()
                .add(R.id.layout_idle_container, BoardCollaborateFragment.createInstance(""))
                .addToBackStack("board_default")
                .commit();*/
        ivWhiteboardPreview.setImageDrawable(new ColorDrawable());
    }


    @Override
    public void back() {
        super.back();
    }
}
