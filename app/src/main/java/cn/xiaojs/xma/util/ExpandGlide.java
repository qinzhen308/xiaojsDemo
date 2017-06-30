package cn.xiaojs.xma.util;
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
 * Date:2017/3/6
 * Desc:
 *
 * ======================================================================================== */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.common.xf_foundation.schemas.Account;

public class ExpandGlide {
    private int mPlaceHoldResId;
    private int mErrorResId;
    private Context mContext;
    private Uri mUri;

    public ExpandGlide with(Context context) {
        mContext = context;
        return this;
    }

    public ExpandGlide load(Uri uri) {
        mUri = uri;
        return this;
    }

    public void into(final ImageView imageView) {
        into(imageView, null);
    }

    public void into(final ImageView imageView, final OnBitmapLoaded bitmapLoaded) {
        if (mUri == null) {
            imageView.setImageResource(mPlaceHoldResId);
            return;
        }

        //load img
        new AsyncTask<Void, Integer, Bitmap>() {

            @Override
            protected void onPreExecute() {
                imageView.setImageResource(mPlaceHoldResId);
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    return Glide.with(mContext)
                            .load(mUri)
                            .asBitmap()
                            .signature(new StringSignature(DeviceUtil.getSignature()))
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                            .get();
                } catch (Exception e) {
                    Logger.i(e != null ? e.getLocalizedMessage() : "null");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(mContext==null||(mContext instanceof Activity&& ((Activity) mContext).isFinishing()))return;
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(mErrorResId);
                }

                if (bitmapLoaded != null) {
                    bitmapLoaded.onLoaded(bitmap);
                }
            }
        }.execute();
    }

    public ExpandGlide error(int resId) {
        mErrorResId = resId;
        return this;
    }


    public ExpandGlide placeHolder(int resId) {
        mPlaceHoldResId = resId;
        return this;
    }

    public interface OnBitmapLoaded {
        void onLoaded(Bitmap bmp);
    }
}
