package cn.xiaojs.xma.ui.classroom2;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class PlayFragment extends MovieFragment {

    @BindView(R.id.video_view)
    PLVideoTextureView videoView;
    @BindView(R.id.loading_View)
    LinearLayout loadingView;
    @BindView(R.id.top_back)
    ImageView nameView;

    //port
    @BindView(R.id.top_more)
    ImageView topMoreView;
    @BindView(R.id.top_live)
    TextView topLiveView;
    @BindView(R.id.bottom_bg)
    View bottomBgView;
    @BindView(R.id.bottom_orient)
    ImageView bottomOrientView;
    @BindView(R.id.bottom_class_name)
    TextView bottomNameView;
    @BindView(R.id.bottom_class_state)
    TextView bottomStateView;


    //land
    @BindView(R.id.top_photo)
    ImageView topPhotoView;
    @BindView(R.id.top_roominfo)
    TextView topRoominfoView;
    @BindView(R.id.top_whiteboard)
    ImageView topWhiteboardView;
    @BindView(R.id.top_screenshot)
    ImageView topScreenshotView;
    @BindView(R.id.bottom_chat)
    ImageView bottomChatView;
    @BindView(R.id.bottom_more)
    ImageView bottomMoreView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classroom2_play, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        configVideoView(videoView);
        videoView.setVideoPath(classroomEngine.getPlayUrl());
        videoView.start();


        bindPlayinfo();
        handleRotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @OnClick({R.id.bottom_orient, R.id.top_back})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.top_back:                    //返回
                back();
                break;
            case R.id.bottom_orient:               //改变屏幕方向
                changeOrientation();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }

    @Override
    public void onRotate(int orientation) {
//        mVideoView.setDisplayOrientation(mRotation);
        handleRotate(orientation);
        onRotateToInitBoard(orientation);

    }

    @Override
    public void closeMovie() {

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

    }

    private void bindPlayinfo() {

        bottomNameView.setText("XXX的教室");
        bottomStateView.setText(classroomEngine.getRoomTitle());
        topRoominfoView.setText(classroomEngine.getRoomTitle());
    }

    private void handleRotate(int orientation) {

        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                //port
                topMoreView.setVisibility(View.GONE);
                topLiveView.setVisibility(View.GONE);
                bottomBgView.setVisibility(View.GONE);
                bottomOrientView.setVisibility(View.GONE);
                bottomNameView.setVisibility(View.GONE);
                bottomStateView.setVisibility(View.GONE);
                //land
                topPhotoView.setVisibility(View.VISIBLE);
                topRoominfoView.setVisibility(View.VISIBLE);
                topWhiteboardView.setVisibility(View.VISIBLE);
                topScreenshotView.setVisibility(View.VISIBLE);
                bottomChatView.setVisibility(View.VISIBLE);
                bottomMoreView.setVisibility(View.VISIBLE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                //port
                topMoreView.setVisibility(View.VISIBLE);
                topLiveView.setVisibility(View.VISIBLE);
                bottomBgView.setVisibility(View.VISIBLE);
                bottomOrientView.setVisibility(View.VISIBLE);
                bottomNameView.setVisibility(View.VISIBLE);
                bottomStateView.setVisibility(View.VISIBLE);
                //land
                topPhotoView.setVisibility(View.GONE);
                topRoominfoView.setVisibility(View.GONE);
                topWhiteboardView.setVisibility(View.GONE);
                topScreenshotView.setVisibility(View.GONE);
                bottomChatView.setVisibility(View.GONE);
                bottomMoreView.setVisibility(View.GONE);
                break;
        }


    }



}
