/*
 * Copyright (C) 2010 ZXing authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.kaola.qrcodescanner.qrcode.decode;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.kaola.qrcodescanner.R;
import com.kaola.qrcodescanner.qrcode.QrCodeActivity;
import com.kaola.qrcodescanner.qrcode.utils.ScreenUtils;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

final class DecodeHandler extends Handler {

    private final QrCodeActivity mActivity;
    private final QRCodeReader mQrCodeReader;
    private final Map<DecodeHintType, Object> mHints;
    private byte[] mRotatedData;

    Rect scanRect=new Rect();//预览图中，预计二维码所在的区域（也就是界面里扫描框所指区域，但偏大一点点）


    DecodeHandler(QrCodeActivity activity) {
        this.mActivity = activity;
        mQrCodeReader = new QRCodeReader();
        mHints = new Hashtable<>();
        mHints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        mHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        mHints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
    }

    @Override
    public void handleMessage(Message message) {

        if (message.what == R.id.decode) {
            decode((byte[]) message.obj, message.arg1, message.arg2);
        } else if (message.what == R.id.quit) {
            Looper looper = Looper.myLooper();
            if (null != looper) {
                looper.quit();
            }
        }

//        switch (message.what) {
//            case R.id.decode:
//                decode((byte[]) message.obj, message.arg1, message.arg2);
//                break;
//            case R.id.quit:
//                Looper looper = Looper.myLooper();
//                if (null != looper) {
//                    looper.quit();
//                }
//                break;
//        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader
     * objects from one decode to the next.
     *
     * @param data The YUV preview frame.
     * @param width The width of the preview frame.
     * @param height The height of the preview frame.
     */
    /*private void decode(byte[] data, int width, int height) {
        if (null == mRotatedData) {
            mRotatedData = new byte[width * height];
        } else {
            if (mRotatedData.length < width * height) {
                mRotatedData = new byte[width * height];
            }
        }
        Arrays.fill(mRotatedData, (byte) 0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x + y * width >= data.length) {
                    break;
                }
                mRotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        Result rawResult = null;
        try {
            PlanarYUVLuminanceSource source =
                    new PlanarYUVLuminanceSource(mRotatedData, width, height, 0, 0, width, height, false);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            rawResult = mQrCodeReader.decode(bitmap1, mHints);
        } catch (ReaderException e) {
        } finally {
            mQrCodeReader.reset();
        }

        if (rawResult != null) {
            Message message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_succeeded, rawResult);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_failed);
            message.sendToTarget();
        }
    }*/

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader
     * objects from one decode to the next.
     *
     * 对于选取图片有效扫描位置的优化，
     * 对于数据过多操作的优化，因为二维码是360°的，所以不需要对源数据进行旋转
     *
     * @param data The YUV preview frame.
     * @param width The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {

        scaleScanRectByPreview(width,height);

        Result rawResult = null;
        try {
            PlanarYUVLuminanceSource source =
                    new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            rawResult = mQrCodeReader.decode(bitmap1, mHints);
        } catch (ReaderException e) {
        } finally {
            mQrCodeReader.reset();
        }

        if (rawResult != null) {
            Message message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_succeeded, rawResult);
            message.sendToTarget();
        } else {
            Message message = Message.obtain(mActivity.getCaptureActivityHandler(), R.id.decode_failed);
            message.sendToTarget();
        }
    }

    /**
     *
     * 缩放扫描区域的比例，将屏幕中的该范围映射到预览图片的对应区域
     *
     * 这里只对竖直和逆时针90°横向这两种图片情况作了处理
     *
     * @param width  源数据的横向长度
     * @param height 源数据的纵向长度
     */
    private void scaleScanRectByPreview(int width,int height){
        float scaleW=0;
        float scaleH=0;
        if(width>height) {//width 实际上是height
            scaleH = height / (float) ScreenUtils.getScreenWidth(mActivity);
            scaleW = width / (float) ScreenUtils.getScreenHeight(mActivity);
        }else {
            scaleW = width / (float) ScreenUtils.getScreenWidth(mActivity);
            scaleH = height / (float) ScreenUtils.getScreenHeight(mActivity);
        }
        if(width>height){
            scanRect.bottom=mActivity.getPreviewRect().left;
            scanRect.top=mActivity.getPreviewRect().right;
            scanRect.left=mActivity.getPreviewRect().top;
            scanRect.right=mActivity.getPreviewRect().bottom;

        }else {
            scanRect.left=mActivity.getPreviewRect().left;
            scanRect.right=mActivity.getPreviewRect().right;
            scanRect.top=mActivity.getPreviewRect().top;
            scanRect.bottom=mActivity.getPreviewRect().bottom;
        }

        scanRect.left=(int)(scanRect.left*scaleW);
        scanRect.right=(int)(scanRect.right*scaleW);
        scanRect.top=(int)(scanRect.top*scaleH);
        scanRect.bottom=(int)(scanRect.bottom*scaleH);

        if(width>height){
            scanRect.top=height-scanRect.top;
            scanRect.bottom=height-scanRect.bottom;
        }
    }
}
