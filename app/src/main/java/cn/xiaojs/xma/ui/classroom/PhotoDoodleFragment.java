package cn.xiaojs.xma.ui.classroom;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.fasterxml.jackson.core.io.NumberInput;

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
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.whiteboard.ShareDoodlePopWindow;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardAdapter;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardCollection;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardScrollerView;
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
    private final static int ANIM_SHOW = 1 << 1;
    private final static int ANIM_HIDE = 1 << 2;

    public final static int MODE_SINGLE_IMG = 1;
    public final static int MODE_MULTI_IMG = 2;

    @BindView(R.id.white_board_panel)
    View mWhiteBoardPanel;
    @BindView(R.id.white_board_scrollview)
    WhiteboardScrollerView mBoardScrollerView;

    private WhiteboardController mBoardController;
    private Bitmap mBitmap;
    private ShareDoodlePopWindow mSharePopWindow;
    private Constants.User mUser = Constants.User.TEACHER;
    private boolean mAnimating;
    private String mTicket;
    private OnPhotoDoodleShareListener mPhotoDoodleShareListener;

    private int mDisplayMode = MODE_SINGLE_IMG;
    private float mDoodleRatio = WhiteboardLayer.DOODLE_CANVAS_RATIO;
    private List<String> mImgList;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_video_editing, null);
    }

    @Override
    protected void init() {
        if (mContext instanceof ClassroomActivity) {
            mTicket = ((ClassroomActivity)mContext).getTicket();
        }

        mBoardController = new WhiteboardController(mContext, mContent, mUser, 0);

        Bundle data = getArguments();
        if (data != null) {
            mDisplayMode = data.getInt(Constants.KEY_IMG_DISPLAY_MODE, MODE_SINGLE_IMG);
            mImgList = data.getStringArrayList(Constants.KEY_IMG_LIST);
            mDoodleRatio = data.getFloat(Constants.KEY_DOODLE_RATIO, WhiteboardLayer.DOODLE_CANVAS_RATIO);
        }

        if (mDisplayMode == MODE_SINGLE_IMG) {
            mBoardController.showWhiteboardLayout(mBitmap, mDoodleRatio);
        } else if (mDisplayMode == MODE_MULTI_IMG){
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
    }

    @OnClick({R.id.back_in_doodle, R.id.share_doodle, R.id.save_doodle, R.id.select_btn, R.id.handwriting_btn,
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
                getFragmentManager().popBackStackImmediate();
                break;
            case R.id.share_doodle:
                selectShareContact(v);
                break;
            case R.id.save_doodle:
                saveEditedBmpToLibrary(mBoardController.getWhiteboardBitmap());
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
            mSharePopWindow = new ShareDoodlePopWindow(mContext, mTicket, mBoardController, mPhotoDoodleShareListener);
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
                    manager.addToLibrary(mContext, file.getPath(), file.getName(), mTicket, new QiniuService() {
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

    private void switchWhiteBoardToolbar() {
        if (mAnimating) {
            return;
        }
    }
}
