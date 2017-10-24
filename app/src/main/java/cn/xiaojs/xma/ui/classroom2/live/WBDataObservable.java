package cn.xiaojs.xma.ui.classroom2.live;

import android.graphics.Bitmap;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/10/24.
 */

public class WBDataObservable extends Observable<WBDataObservable.WBData> {

    private WbDataWachter wachter;


    public static WBDataObservable createObservable() {
        WBDataObservable observable = new WBDataObservable();
        observable.subscribeOn(Schedulers.io());
        observable.observeOn(Schedulers.io());
        return observable;
    }


    @Override
    protected void subscribeActual(Observer<? super WBData> observer) {
        wachter = new WbDataWachter(observer);
        observer.onSubscribe(wachter);

    }

    public WbDataWachter getObserver() {
        return wachter;
    }

    public void onDataReceived(final Bitmap targetBitmap, final int inputWidth, final int inputHeight) {
        if (wachter !=null) {
            wachter.receivedData(targetBitmap, inputWidth, inputHeight);
        }
    }


    public static class WbDataWachter extends MainThreadDisposable {

        private Observer<? super WBData> observer;

        public WbDataWachter(Observer observer) {
            this.observer = observer;
        }

        @Override
        protected void onDispose() {
            //TODO
        }

        public void receivedData(final Bitmap targetBitmap,
                                 final int inputWidth, final int inputHeight) {
            byte[] data = getNV21(inputWidth, inputHeight, targetBitmap);
            observer.onNext(new WBData(data, inputWidth, inputHeight));
        }


        // untested function
        byte[] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {

            int[] argb = new int[inputWidth * inputHeight];

            scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

            byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
            encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

            scaled.recycle();

            return yuv;
        }

        void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
            final int frameSize = width * height;

            int yIndex = 0;
            int uvIndex = frameSize;

            int a, R, G, B, Y, U, V;
            int index = 0;
            for (int j = 0; j < height; j++) {
                for (int i = 0; i < width; i++) {

                    a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                    R = (argb[index] & 0xff0000) >> 16;
                    G = (argb[index] & 0xff00) >> 8;
                    B = (argb[index] & 0xff) >> 0;

                    // well known RGB to YUV algorithm
                    Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                    U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                    V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                    // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                    //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                    //    pixel AND every other scanline.
                    yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                    if (j % 2 == 0 && index % 2 == 0) {
                        yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                        yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                    }

                    index++;
                }
            }
        }

    }

    public static class WBData {

        public byte[] data;
        public int inputWidth;
        public int inputHeight;

        public WBData(byte[] data, int inputWidth, int inputHeight) {
            this.data = data;
            this.inputWidth = inputWidth;
            this.inputHeight = inputHeight;
        }
    }
}
