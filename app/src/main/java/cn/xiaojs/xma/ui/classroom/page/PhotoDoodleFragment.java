package cn.xiaojs.xma.ui.classroom.page;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.main.AnimData;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.main.FadeAnimListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardAdapter;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardCollection;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardScrollerView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.CacheUtil;

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

public class PhotoDoodleFragment extends BaseFragment {
    public final static int TYPE_SINGLE_IMG = 1;
    public final static int TYPE_MULTI_IMG = 2;

    public final static int MODE_PREVIEW = 1; //预览模式
    public final static int MODE_EDIT = 2; //编辑模式


    @BindView(R.id.white_board_panel)
    View mWhiteBoardPanel;
    @BindView(R.id.white_board_scrollview)
    WhiteboardScrollerView mBoardScrollerView;
    @BindView(R.id.back_btn)
    ImageView mBackBtn;
    @BindView(R.id.share_doodle)
    TextView mShareBtn;
    @BindView(R.id.save_doodle)
    TextView mSaveBtn;
    @BindView(R.id.edit_done_btn)
    TextView mEditDodeBtn;

    private WhiteboardController mBoardController;
    private Bitmap mBitmap;
    private ShareDoodlePopWindow mSharePopWindow;
    private Constants.User mUser = Constants.User.LEAD;
    private OnPhotoDoodleShareListener mPhotoDoodleShareListener;
    private CommonDialog mSaveDoodleDialog;

    private int mDisplayType = TYPE_SINGLE_IMG;
    private int mMode = MODE_PREVIEW;
    private float mDoodleRatio = WhiteboardLayer.DOODLE_CANVAS_RATIO;
    private List<String> mImgList;
    private FadeAnimListener mFadeAnimListener;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_img_edit, null);
    }

    @Override
    protected void init() {
        mBoardController = new WhiteboardController(mContext, mContent, mUser, 0);
        mFadeAnimListener = new FadeAnimListener();

        Bundle data = getArguments();
        if (data != null) {
            mDisplayType = data.getInt(Constants.KEY_IMG_DISPLAY_TYPE, TYPE_SINGLE_IMG);
            mImgList = data.getStringArrayList(Constants.KEY_IMG_LIST);
            mDoodleRatio = data.getFloat(Constants.KEY_DOODLE_RATIO, WhiteboardLayer.DOODLE_CANVAS_RATIO);
        }

        if (mDisplayType == TYPE_SINGLE_IMG) {
            mBoardController.showWhiteboardLayout(mBitmap, mDoodleRatio);
        } else if (mDisplayType == TYPE_MULTI_IMG) {
            WhiteboardAdapter adapter = new WhiteboardAdapter(mContext);
            WhiteboardCollection wbColl = new WhiteboardCollection();
            List<WhiteboardLayer> whiteboardLayers = new ArrayList<WhiteboardLayer>();
            if (mImgList != null) {
                for (String img : mImgList) {
                    WhiteboardLayer layer = new WhiteboardLayer();
                    layer.setCoursePath(img);
                    whiteboardLayers.add(layer);
                }
            }
            wbColl.setWhiteboardLayer(whiteboardLayers);
            adapter.setData(wbColl, 0);
            mBoardController.showWhiteboardLayout(adapter);
        }

        mWhiteBoardPanel.setVisibility(View.GONE);
    }

    @OnClick({R.id.back_btn, R.id.share_doodle, R.id.save_doodle, R.id.select_btn, R.id.handwriting_btn,
            R.id.color_picker_btn, R.id.shape_btn, R.id.eraser_btn, R.id.text_btn, R.id.undo, R.id.redo, R.id.edit_done_btn})
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
            case R.id.back_btn:
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.share_doodle:
                selectShareContact(v);
                break;
            case R.id.save_doodle:
                saveEditedBmpToLibrary(mBoardController.getWhiteboardBitmap());
                break;
            case R.id.edit_done_btn:
                if (mMode == MODE_PREVIEW) {
                    enterEditMode();
                } else {
                    exitEditMode();
                }
                break;
            default:
                break;
        }
    }


    public void setBitmap(Bitmap bmp) {
        mBitmap = bmp;
    }

    public void setPhotoDoodleShareListener(OnPhotoDoodleShareListener listener) {
        mPhotoDoodleShareListener = listener;
    }

    /**
     * 选择分享联系人
     */
    private void selectShareContact(View anchor) {
        if (mSharePopWindow == null) {
            mSharePopWindow = new ShareDoodlePopWindow(mContext, mBoardController, mPhotoDoodleShareListener);
        }

        int offsetX = -mContext.getResources().getDimensionPixelSize(R.dimen.px246);
        int offsetY = mContext.getResources().getDimensionPixelSize(R.dimen.px20);
        mSharePopWindow.showAsDropDown(anchor, offsetX, offsetY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBoardController.hideWhiteboardLayout();
        if (mWhiteBoardPanel != null) {
            mWhiteBoardPanel.animate().cancel();
        }
    }

    private void saveEditedBmpToLibrary(final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }

        final String name = "edit_video";
        showProgress(true);

        //TODO 待上传资料库不需要从文件上传时，再做优化
        new AsyncTask<Integer, Integer, String>() {

            @Override
            protected String doInBackground(Integer... params) {
                return CacheUtil.saveWhiteboard(bitmap, name);
            }

            @Override
            protected void onPostExecute(String result) {
                if (!TextUtils.isEmpty(result)) {
                    File file = new File(result);
                    CollaManager manager = new CollaManager();
                    //FIXME 如果没有ticket或者ticket不合法，接口会返回参数错误
                    manager.addToLibrary(mContext, file.getPath(), file.getName(), new QiniuService() {
                        @Override
                        public void uploadSuccess(String key, UploadReponse reponse) {
                            cancelProgress();
                            Toast.makeText(mContext, R.string.save_edit_video_succ, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void uploadProgress(String key, double percent) {

                        }

                        @Override
                        public void uploadFailure(boolean cancel) {
                            cancelProgress();
                            Toast.makeText(mContext, R.string.save_edit_video_fail, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    cancelProgress();
                    Toast.makeText(mContext, R.string.save_edit_video_fail, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(0);
    }

    private void hideAnim(View view) {
        startAnimation(view,
                FadeAnimListener.MODE_ANIM_HIDE,
                FadeAnimListener.ANIM_ALPHA,
                new AnimData(0));
    }

    private void showAnim(View view) {
        startAnimation(view,
                FadeAnimListener.MODE_ANIM_SHOW,
                FadeAnimListener.ANIM_ALPHA,
                new AnimData(1));
    }

    protected void startAnimation(View view, int animMode, int animSets, AnimData data) {
        FadeAnimListener listener = mFadeAnimListener;
        ViewPropertyAnimator viewPropertyAnimator = view.animate();

        //alpha anim
        if ((FadeAnimListener.ANIM_ALPHA & animSets) != 0) {
            viewPropertyAnimator.alpha(data.alpha);
        }
        //translate anim
        if ((FadeAnimListener.ANIM_TRANSLATE & animSets) != 0) {
            viewPropertyAnimator.translationX(data.translateX);
            viewPropertyAnimator.translationY(data.translateY);
        }
        //scale anim
        if ((FadeAnimListener.ANIM_SCALE & animSets) != 0) {
            viewPropertyAnimator.scaleX(data.scaleX);
            viewPropertyAnimator.scaleY(data.scaleY);
        }

        viewPropertyAnimator.setListener(listener.with(view).play(animMode)).start();
    }


    private void enterEditMode() {
        mMode = MODE_EDIT;
        mEditDodeBtn.setText(R.string.finish);
        mSaveBtn.setVisibility(View.GONE);
        mShareBtn.setVisibility(View.GONE);
        mBackBtn.setVisibility(View.GONE);
        showAnim(mWhiteBoardPanel);
        mBoardController.setWhiteboardScrollMode(MODE_EDIT);
    }

    private void exitEditMode() {
        mMode = MODE_PREVIEW;
        mEditDodeBtn.setText(R.string.edit);
        mSaveBtn.setVisibility(View.VISIBLE);
        mShareBtn.setVisibility(View.VISIBLE);
        mBackBtn.setVisibility(View.VISIBLE);
        hideAnim(mWhiteBoardPanel);
        mBoardController.setWhiteboardScrollMode(MODE_PREVIEW);
        mBoardController.saveEdit();
    }

    public void exitEdiModeWhitTips() {
        if (mBoardController.hasEdit()) {
            isSave();
        } else {
            exitEditMode();
        }
    }

    /**
     * 检测是否需要保存编辑
     */
    private void isSave() {
        if (mSaveDoodleDialog == null) {
            mSaveDoodleDialog = new CommonDialog(mContext);
            int width;
            int height;
            mSaveDoodleDialog.setTitle(R.string.cr_save_edit);
            mSaveDoodleDialog.setDesc(R.string.cr_save_edit_desc);
            mSaveDoodleDialog.setLefBtnText(R.string.cancel);
            mSaveDoodleDialog.setRightBtnText(R.string.ok);
            mSaveDoodleDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mSaveDoodleDialog.dismiss();
                    exitEditMode();
                }
            });

            mSaveDoodleDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mBoardController.abandonEdit();
                    exitEditMode();
                }
            });
        }

        mSaveDoodleDialog.show();
    }

    public boolean isEditMode() {
        return mMode == MODE_EDIT;
    }
}
