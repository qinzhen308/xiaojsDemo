package cn.xiaojs.xma.ui.classroom.whiteboard.shape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;

/**
 * Created by Paul Z on 2017/9/7.
 */

public class SyncRemoteImgLayer extends SyncRemoteLayer {
    private Bitmap layerBm;

    public static final String IMG_SYMBOL_BASE64_2="base64,";
    public static final String IMG_SYMBOL_BASE64_1="data:image";
    public static final String IMG_SYMBOL_URL="http";

    private boolean isLoading=false;

    private float angle=0;

    public SyncRemoteImgLayer(Whiteboard whiteboard) {
        super(whiteboard,STYLE_SYNC_LAYER_IMG);
    }

    public SyncRemoteImgLayer(Whiteboard whiteboard,String imgSrc) {
        super(whiteboard,STYLE_SYNC_LAYER_IMG);
        handleImgSource(imgSrc,0);
    }


    public void handleImgSource(String imgSrc,float angle){
        this.angle=angle;
        if(isBase64(imgSrc)){
            layerBm=decodeBg(imgSrc);
        }else {
            loadImageByUrl(imgSrc);
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
        if(layerBm==null&&!isLoading){
            return;
        }

        canvas.save();
        mTransRect.set(mDoodleRect);
        if(isLoading){
            mDrawingPath.reset();
            mDrawingPath.addRect(mTransRect, Path.Direction.CCW);
            mDrawingMatrix.postConcat(mTransformMatrix);
            mDrawingPath.transform(mDrawingMatrix);
            canvas.drawPath(mDrawingPath,getPaint());
        }else {
            RectF rectF=new RectF();
            mDrawingMatrix.mapRect(rectF,mTransRect);
            canvas.rotate((float)( 180*angle/Math.PI),rectF.centerX(),rectF.centerY());
            canvas.drawBitmap(layerBm,new Rect(0,0,layerBm.getWidth(),layerBm.getHeight()),rectF,getPaint());
        }

        canvas.restore();
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

    private void loadImageByUrl(String url){
        isLoading=true;
        RectF rectF=new RectF();
        mDrawingMatrix.mapRect(rectF,mDoodleRect);
        Glide.with(getWhiteboard().getContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>((int)rectF.width(),(int)rectF.height()) {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                //这里我们拿到回掉回来的bitmap，可以加载到我们想使用到的地方
                Logger.d("---qz---sync---load image----size"+bitmap.getWidth()+","+bitmap.getHeight());
                layerBm=bitmap;
                isLoading=false;
                getWhiteboard().drawAllDoodlesCanvas();
            }
        });
    }
}
