package cn.xiaojs.xma.ui.classroom;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.whiteboard.ShareDoodlePopWindow;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/3/7
 * Desc:
 *
 * ======================================================================================== */

public class VideoEditingFragment extends BaseFragment {
    private final static int ANIM_SHOW = 1 << 1;
    private final static int ANIM_HIDE = 1 << 2;

    @BindView(R.id.white_board_panel)
    View mWhiteBoardPanel;

    private WhiteboardController mBoardController;
    private Bitmap mBitmap;
    private ShareDoodlePopWindow mSharePopWindow;
    private Constants.User mUser = Constants.User.TEACHER;
    private boolean mAnimating;
    private PanelAnimListener mPanelAnimListener;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_video_editing, null);
    }

    @Override
    protected void init() {
        mPanelAnimListener = new PanelAnimListener();
        mBoardController = new WhiteboardController(mContext, mContent, mUser, 0);
        mBoardController.onGeometryChange(GeometryShape.RECTANGLE);
        mBoardController.onColorChanged(WhiteboardConfigs.DEFAULT_PAINT_COLOR);
        mBoardController.showWhiteboardLayout(mBitmap);
    }

    @OnClick({R.id.wb_toolbar_btn, R.id.back_in_doodle, R.id.share_doodle, R.id.save_doodle, R.id.select_btn, R.id.handwriting_btn,
            R.id.color_picker_btn, R.id.shape_btn, R.id.eraser_btn, R.id.text_btn, R.id.undo, R.id.redo})
    public void onPanelItemClick(View v) {
        switch (v.getId()) {
            case R.id.select_btn:
            case R.id.handwriting_btn:
            case R.id.shape_btn:
            case R.id.eraser_btn:
            case R.id.text_btn:
            case R.id.color_picker_btn:
            case R.id.undo:
            case R.id.redo:
                mBoardController.handlePanelItemClick(v);
                break;
            case R.id.back_in_doodle:
                if (mContext instanceof ClassroomActivity) {
                    ((ClassroomActivity)mContext).exitVideoEditing();
                }
                break;
            case R.id.share_doodle:
                selectShareContact(v);
                break;
            case R.id.save_doodle:
                break;
            case R.id.wb_toolbar_btn:
                if (mWhiteBoardPanel.getVisibility() == View.VISIBLE) {
                    hideWhiteBoardPanel();
                } else {
                    showWhiteBoardPanel(true);
                }
                break;
            default:
                break;
        }
    }


    public void setBitmap(Bitmap bmp) {
        mBitmap = bmp;
    }

    /**
     * 选择分享联系人
     */
    private void selectShareContact(View anchor) {
        if (mSharePopWindow == null) {
            String ticket = null;
            if (mContext instanceof ClassroomActivity) {
                ticket = ((ClassroomActivity)mContext).getTicket();
            }
            mSharePopWindow = new ShareDoodlePopWindow(mContext, ticket);
        }

        int offsetX = -mContext.getResources().getDimensionPixelSize(R.dimen.px370);
        int offsetY = -mContext.getResources().getDimensionPixelSize(R.dimen.px58);
        mSharePopWindow.showAsDropDown(anchor, offsetX, offsetY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBoardController.hideWhiteboardLayout();
    }


    /**
     * 隐藏白板操作面板
     */
    private void hideWhiteBoardPanel() {
        if (mAnimating) {
            return;
        }
        mWhiteBoardPanel.animate()
                .alpha(0.0f)
                .setListener(mPanelAnimListener.with(mWhiteBoardPanel).play(ANIM_HIDE))
                .start();

    }

    /**
     * 显示白板操作面板
     */
    private void showWhiteBoardPanel(boolean needAnim) {
        if (mAnimating) {
            return;
        }

        if (needAnim) {
            mWhiteBoardPanel.animate()
                    .alpha(1.0f)
                    .setListener(mPanelAnimListener.with(mWhiteBoardPanel).play(ANIM_SHOW))
                    .start();
        } else {
            mWhiteBoardPanel.setAlpha(1.0f);
            mWhiteBoardPanel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 动画监听器
     */
    private class PanelAnimListener implements Animator.AnimatorListener {
        private View mV;
        private int mAnimType;

        public PanelAnimListener with(View v) {
            mV = v;
            return this;
        }

        public PanelAnimListener play(int type) {
            mAnimType = type;
            return this;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mAnimating = true;
            if (mAnimType == ANIM_SHOW && mV != null) {
                mV.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mAnimating = false;
            if (mAnimType == ANIM_HIDE && mV != null) {
                mV.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mAnimating = false;
            mV = null;
            if (mAnimType == ANIM_SHOW && mV != null) {
                mV.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }
}
