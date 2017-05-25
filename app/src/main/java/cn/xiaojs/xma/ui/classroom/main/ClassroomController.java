package cn.xiaojs.xma.ui.classroom.main;
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
 * Date:2017/5/3
 * Desc:
 *
 * ======================================================================================== */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.document.DocumentFragment;
import cn.xiaojs.xma.ui.classroom.page.MsgInputFragment;
import cn.xiaojs.xma.ui.classroom.page.OnPhotoDoodleShareListener;
import cn.xiaojs.xma.ui.classroom.page.OnSettingChangedListener;
import cn.xiaojs.xma.ui.classroom.page.PhotoDoodleFragment;
import cn.xiaojs.xma.ui.classroom.page.SettingFragment;
import cn.xiaojs.xma.ui.classroom.page.VideoPlayFragment;
import cn.xiaojs.xma.ui.classroom.talk.ContactFragment;
import cn.xiaojs.xma.ui.classroom.talk.SlideTalkFragment;
import cn.xiaojs.xma.ui.classroom.talk.SlidingTalkDialogFragment;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayer;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.SheetFragment;
import cn.xiaojs.xma.util.MaterialUtil;

public class ClassroomController {
    public final static int REQUEST_PIC_CODE = 1;
    public final static int REQUEST_INPUT = 2;
    public final static int REQUEST_CONTACT = 3;
    public final static int REQUEST_DOC = 4;
    public final static int REQUEST_TALK = 5;
    public final static int REQUEST_SETTING = 6;

    public final static int PAGE_TOP = 1 << 0;
    public final static int PAGE_PHOTO_DOODLE = 1 << 1;
    public final static int PAGE_DOCUMENT = 1 << 2;
    public final static int PAGE_VIDEO_PLAY = 1 << 3;

    public static int MODE_FRAGMENT_PLAY_TALK = 0; //播放端交流模式
    public static int MODE_FRAGMENT_PLAY_FULL_SCREEN = 1; //播放全屏模式

    private List<BackPressListener> mBackPressListeners;
    private static ClassroomController mInstance;

    private CommonDialog mExitDialog;

    private Context mContext;
    private int mPlayFragmentMode = MODE_FRAGMENT_PLAY_TALK;
    private ClassroomLiveFragment mCurrStackFragment;


    private ClassroomController(Context context) {
        mContext = context;
        mBackPressListeners = new ArrayList<BackPressListener>();
    }

    public synchronized static void init(Context context) {
        mInstance = new ClassroomController(context);
    }

    public void release() {
        mInstance = null;
        mBackPressListeners.clear();
        mBackPressListeners = null;
    }

    public static synchronized ClassroomController getInstance() {
        return mInstance;
    }

    /**
     * 打开文档库
     */
    public void openDocument(Fragment target, CtlSession session) {
        Fragment documentFragment = new DocumentFragment();
        documentFragment.setTargetFragment(target, REQUEST_DOC);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_CTL_SESSION, session);
        documentFragment.setArguments(bundle);
        ((ClassroomActivity) mContext).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.document_layout, documentFragment)
                .addToBackStack("doc")
                .commit();
    }

    /**
     * 打开可滑动聊天
     */
    public void openSlideTalk(Fragment target, Attendee attendee, CtlSession session, int viewSize) {
        SlideTalkFragment fragment = new SlideTalkFragment();
        fragment.setTargetFragment(target, REQUEST_TALK);
        Bundle data = new Bundle();
        data.putSerializable(Constants.KEY_CTL_SESSION, session);
        data.putSerializable(Constants.KEY_TALK_ATTEND, attendee);
        data.putInt(Constants.KEY_PAGE_HEIGHT, viewSize);
        fragment.setArguments(data);
        ((ClassroomActivity) mContext).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fc_slide_talk_layout, fragment)
                .addToBackStack("slide_talk")
                .commit();
    }

    /**
     * 打开可滑动聊天(DialogFragment)
     */
    public void openSlideTalk(Fragment target, Attendee attendee, CtlSession session, int gravity, int viewSize) {
        SlidingTalkDialogFragment fragment = new SlidingTalkDialogFragment();
        fragment.setTargetFragment(target, REQUEST_TALK);
        Bundle data = new Bundle();
        data.putSerializable(Constants.KEY_CTL_SESSION, session);
        data.putInt(SheetFragment.KEY_SHEET_GRAVITY, gravity);
        if (SheetFragment.SHEET_GRAVITY_RIGHT == gravity) {
            data.putInt(SheetFragment.KEY_FRAGMENT_W, viewSize);
        } else {
            data.putInt(SheetFragment.KEY_FRAGMENT_H, viewSize);
        }
        data.putSerializable(Constants.KEY_TALK_ATTEND, attendee);
        fragment.setArguments(data);
        fragment.show(((ClassroomActivity) mContext).getSupportFragmentManager(), "slide_dialog_talk");
    }


    /**
     * 打开联系人(DialogFragment)
     */
    public void openContact(Fragment target, CtlSession session, int gravity, int viewSize) {
        ContactFragment fragment = new ContactFragment();
        fragment.setTargetFragment(target, REQUEST_CONTACT);
        Bundle data = new Bundle();
        data.putSerializable(Constants.KEY_CTL_SESSION, session);
        data.putInt(SheetFragment.KEY_SHEET_GRAVITY, gravity);
        if (SettingFragment.SHEET_GRAVITY_RIGHT == gravity) {
            data.putInt(SheetFragment.KEY_FRAGMENT_W, viewSize);
        } else {
            data.putInt(SheetFragment.KEY_FRAGMENT_H, viewSize);
        }
        fragment.setArguments(data);
        fragment.show(((ClassroomActivity) mContext).getSupportFragmentManager(), "contact");
    }

    /**
     * 输入文字(DialogFragment)
     */
    public void openInputText(Fragment target, int from) {
        MsgInputFragment fragment = new MsgInputFragment();
        Bundle data = new Bundle();
        data.putInt(Constants.KEY_MSG_INPUT_FROM, from);
        fragment.setArguments(data);
        fragment.setTargetFragment(target, REQUEST_INPUT);
        fragment.show(((ClassroomActivity) mContext).getSupportFragmentManager(), "input");
    }

    /**
     * 打开设置(DialogFragment)
     */
    public void openSetting(Fragment target, int gravity, int viewSize, OnSettingChangedListener listener) {
        SettingFragment fragment = new SettingFragment();
        fragment.setOnSettingChangedListener(listener);
        Bundle data = new Bundle();
        fragment.setArguments(data);
        data.putInt(SheetFragment.KEY_SHEET_GRAVITY, gravity);
        if (SheetFragment.SHEET_GRAVITY_BOTTOM == gravity) {
            data.putInt(SheetFragment.KEY_FRAGMENT_H, viewSize);
        }
        fragment.setTargetFragment(target, REQUEST_SETTING);
        fragment.show(((ClassroomActivity) mContext).getSupportFragmentManager(), "setting");
    }

    /**
     * 选择图片
     */
    public void selectPic(Fragment fragment) {
        Intent i = new Intent(mContext, CropImageMainActivity.class);
        i.putExtra(CropImagePath.CROP_NEVER, true);
        fragment.startActivityForResult(i, REQUEST_PIC_CODE);
    }

    /**
     * 进入图片编辑，图片可以左右滑动
     */
    public void enterPhotoDoodle(ArrayList<String> imgUrlList, OnPhotoDoodleShareListener listener) {
        PhotoDoodleFragment photoDoodleFragment = new PhotoDoodleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_IMG_DISPLAY_TYPE, PhotoDoodleFragment.TYPE_MULTI_IMG);
        bundle.putStringArrayList(Constants.KEY_IMG_LIST, imgUrlList);
        photoDoodleFragment.setArguments(bundle);
        photoDoodleFragment.setPhotoDoodleShareListener(listener);
        commitPhotoDoodleFragment(photoDoodleFragment);
    }

    /**
     * 进入图片编辑
     */
    public void enterPhotoDoodleByBitmap(Bitmap bitmap, OnPhotoDoodleShareListener listener) {
        PhotoDoodleFragment photoDoodleFragment = new PhotoDoodleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_IMG_DISPLAY_TYPE, PhotoDoodleFragment.TYPE_SINGLE_IMG);
        float ratio = WhiteboardLayer.DOODLE_CANVAS_RATIO;
        if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
            ratio = bitmap.getWidth() / (float) bitmap.getHeight();
        } else {
            if (isPortrait()) {
                ratio = 1.0f / WhiteboardLayer.DOODLE_CANVAS_RATIO;
            } else {
                ratio = WhiteboardLayer.DOODLE_CANVAS_RATIO;
            }
        }
        bundle.putFloat(Constants.KEY_DOODLE_RATIO, ratio);
        photoDoodleFragment.setArguments(bundle);
        photoDoodleFragment.setBitmap(bitmap);
        photoDoodleFragment.setPhotoDoodleShareListener(listener);
        commitPhotoDoodleFragment(photoDoodleFragment);
    }

    /**
     * 提交图片编辑对应的fragment
     */
    private void commitPhotoDoodleFragment(Fragment fragment) {
        if (fragment != null) {
            ((ClassroomActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.photo_doodle_layout, fragment)
                    .addToBackStack("photo_doodle")
                    .commit();
        }
    }

    /**
     * 进入图片编辑
     */
    public void enterPhotoDoodle(final String url, final OnPhotoDoodleShareListener listener) {
        //load course img
        new AsyncTask<Void, Integer, Bitmap>() {

            @Override
            protected void onPreExecute() {
                ((ClassroomActivity) mContext).showProgress(true);
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return Glide.with(mContext)
                            .load(url)
                            .asBitmap()
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    Logger.i(e != null ? e.getLocalizedMessage() : "null");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                ((ClassroomActivity) mContext).cancelProgress();
                if (bitmap != null) {
                    enterPhotoDoodleByBitmap(bitmap, listener);
                } else {
                    Toast.makeText(mContext, R.string.cls_pic_load_fail, Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    /**
     * 进入视频播放页面
     */
    public void enterVideoPlayPage(LibDoc doc) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.KEY_LIB_DOC, doc);
        VideoPlayFragment videoPlayFragment = new VideoPlayFragment();
        videoPlayFragment.setArguments(bundle);
        ((ClassroomActivity) mContext).getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.video_play_layout, videoPlayFragment)
                .addToBackStack("video_player")
                .commit();
    }

    public void exitVideoPlayPage() {
        if (mContext instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) mContext;
            Fragment fragment = activity.getSupportFragmentManager()
                    .findFragmentById(R.id.video_play_layout);
            if (fragment instanceof VideoPlayFragment) {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment);
            }
        }
    }

    /**
     * 退出文档库并且打开多媒体
     */
    public void exitDocFragmentWhitOpenMime(LibDoc doc, OnPhotoDoodleShareListener listener) {
        if (doc == null) {
            return;
        }
        String mimeType = doc.mimeType != null ? doc.mimeType.toLowerCase() : "";
        String url = ClassroomBusiness.getFileUrl(doc.key);
        if (mimeType.startsWith(Collaboration.PictureMimeTypes.ALL)) {
            enterPhotoDoodle(url, listener);
        } else if (Collaboration.isStreaming(mimeType)
                || mimeType.startsWith(Collaboration.VideoMimeTypes.ALL)) {
            enterVideoPlayPage(doc);
        } else if (mimeType.startsWith(Collaboration.OfficeMimeTypes.PPT)
                || mimeType.startsWith(Collaboration.OfficeMimeTypes.PPTX)) {
            ArrayList<LibDoc.ExportImg> images = MaterialUtil.getSortImgs(doc.exported != null ? doc.exported.images : null);
            if (images != null) {
                ArrayList<String> imgUrls = new ArrayList<String>();
                for (LibDoc.ExportImg img : images) {
                    imgUrls.add(ClassroomBusiness.getFileUrl(img.name));
                }
                enterPhotoDoodle(imgUrls, listener);
            } else {
                Toast.makeText(mContext, R.string.cr_ppt_open_fail, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, R.string.cr_not_support_doc, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 进入播放fragment
     */
    public void enterPlayFragment(Bundle data, boolean needExitCurr) {
        if (mCurrStackFragment instanceof PlayFragment) {
            return;
        }

        if (mContext instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) mContext;
            if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            if (needExitCurr && mCurrStackFragment instanceof PublishFragment) {
                ((ClassroomActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .remove(mCurrStackFragment)
                        .commit();
            }

            PlayFragment fragment = new PlayFragment();
            fragment.setArguments(data);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, 0)
                    .add(R.id.play_mode_layout, fragment)
                    .commit();
        }
    }

    /**
     * 进入推流fragment
     */
    public void enterPublishFragment(Bundle data, boolean needExitCurr) {
        if (mCurrStackFragment instanceof PublishFragment) {
            return;
        }

        if (mContext instanceof ClassroomActivity) {
            FragmentActivity activity = (FragmentActivity) mContext;
            if (activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            if (needExitCurr && mCurrStackFragment instanceof PlayFragment) {
                ((ClassroomActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .remove(mCurrStackFragment)
                        .commit();
            }
            PublishFragment fragment = new PublishFragment();
            fragment.setArguments(data);
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    //.addToBackStack("publish_fragment")
                    .add(R.id.publish_mode_layout, fragment)
                    .commit();
        }
    }

    /**
     * 退出横屏状态下的全屏
     */
    public void enterLandFullScreen(boolean isPortrait, Activity activity) {
        if (isPortrait) {
            mPlayFragmentMode = MODE_FRAGMENT_PLAY_FULL_SCREEN;
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * 退出全屏
     */
    public void exitFullScreen(Activity activity, boolean requestOrientation) {
        mPlayFragmentMode = MODE_FRAGMENT_PLAY_TALK;
        if (requestOrientation) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * 是否是竖屏
     * @return
     */
    public boolean isPortrait() {
        if (mContext instanceof ClassroomActivity) {
            FragmentActivity activity = (FragmentActivity) mContext;
            return activity.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        }

        return false;
    }

    public int getPlayFragmentMode() {
        return mPlayFragmentMode;
    }

    public boolean isFragmentPlayFullScreen() {
        return mPlayFragmentMode == MODE_FRAGMENT_PLAY_FULL_SCREEN;
    }

    public void onActivityBackPressed(int backStackEntryCount) {
        if (mBackPressListeners != null) {
            for (BackPressListener listener : mBackPressListeners) {
                listener.onBackPressed();
            }
        }
    }

    /**
     * 显示是否退出教室对话框
     */
    public void showExitClassroomDialog() {
        if (mExitDialog == null) {
            mExitDialog = new CommonDialog(mContext);
            mExitDialog.setTitle(R.string.exit_classroom);
            mExitDialog.setDesc(R.string.exit_classroom_tips);
            mExitDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    if (mContext instanceof ClassroomActivity) {
                        ((ClassroomActivity) mContext).finish();
                    }
                    mExitDialog.dismiss();
                }
            });
        }

        mExitDialog.show();
    }

    /**
     * 注册back键回调监听器
     */
    public void registerBackPressListener(BackPressListener listener) {
        if (listener != null) {
            mBackPressListeners.add(listener);
        }
    }

    /**
     * 解注back键回调监听器
     */
    public void unregisterBackPressListener(BackPressListener listener) {
        if (listener != null) {
            mBackPressListeners.remove(listener);
        }
    }

    /**
     * 退出栈中的fragment
     */
    public void exitStackFragment() {
        if (mCurrStackFragment != null) {
            ((ClassroomActivity) mContext).getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mCurrStackFragment)
                    .commit();
        }
    }

    public void setStackFragment(ClassroomLiveFragment fragment) {
        mCurrStackFragment = fragment;
    }

    public ClassroomLiveFragment getStackFragment() {
        return mCurrStackFragment;
    }

}
