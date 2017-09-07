package cn.xiaojs.xma.ui.classroom.whiteboard.shape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Base64;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;

/**
 * Created by Administrator on 2017/9/7.
 */

public class SyncRemoteImgLayer extends SyncRemoteLayer {
    private Bitmap layerBm;

    public static final String IMG_SYMBOL_BASE64_2="base64,";
    public static final String IMG_SYMBOL_BASE64_1="data:image";
    public static final String IMG_SYMBOL_URL="http";


    public SyncRemoteImgLayer(Whiteboard whiteboard) {
        super(whiteboard);
    }

    public SyncRemoteImgLayer(Whiteboard whiteboard,String imgSrc) {
        super(whiteboard);
    }


    public void handleImgSource(String imgSrc){
        if(isBase64(imgSrc)){

        }

    }

    private boolean isBase64(String imgSrc){
        if(imgSrc.contains(IMG_SYMBOL_BASE64_1)&&imgSrc.contains(IMG_SYMBOL_BASE64_2)){
            return true;
        }
        return false;
    }

    private boolean isURL(String imgSrc){
        if(imgSrc.contains(IMG_SYMBOL_URL)){
            return true;
        }
        return false;
    }

    @Override
    protected void onDrawSelf(Canvas canvas) {

    }

    private Bitmap decodeBg(String src){
        try {
            byte[] bytes= Base64.decode(src.substring(src.indexOf(",")+1,src.length()),Base64.DEFAULT);
            Bitmap bg= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return bg;
        }catch (Exception e){
            Logger.d(e);
        }
        return null;
    }
}
