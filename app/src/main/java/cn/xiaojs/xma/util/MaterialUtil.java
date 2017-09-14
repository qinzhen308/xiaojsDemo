package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.common.PlayStreamingActivity;
import cn.xiaojs.xma.ui.common.PlayActivity;

/**
 * Created by maxiaobao on 2017/4/12.
 */

public class MaterialUtil {

    public static void openFileBySystem(Context context, String path, String mimeType) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("the file mime type: %s, and path: %s", mimeType, path);
        }

        Uri data = Uri.parse("file://" + path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(data, mimeType);

        List activities = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        if (activities != null && activities.size() > 0) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "系统不支持打开此格式的文件", Toast.LENGTH_SHORT).show();
        }

    }

    public static String getDownloadUrl(LibDoc libDoc) {

        String key = libDoc.key;
        String mimeType = libDoc.mimeType;
//        return "https://file.vipkid.com.cn/apps/vipkid_v1.5.5_17106180_default_defaultChannel.apk";

        if (Collaboration.isImage(mimeType)) {

            return Social.getDrawing(key, false);

        } else if (Collaboration.isVideo(mimeType)
                || Collaboration.isPPT(mimeType)
                || Collaboration.isPDF(mimeType)
                || Collaboration.isDoc(mimeType)) {
            //要判断是不是转码的
            String url = "";
            if (Collaboration.TypeName.RECORDING_IN_LIBRARY.equals(libDoc.typeName)) {//录播课转码的
                if(key!=null&&key.contains("H264")){//录播课转码的(老版本)
                    url = new StringBuilder(ApiManager.getLiveBucket()).append("/").append(key).append(".mp4").toString();

                }else {//录播课(新版本)
                    url = new StringBuilder(ApiManager.getLiveBucket()).append("/").append(key).toString();
                }
            } else {
                url = new StringBuilder(ApiManager.getFileBucket()).append("/").append(key).toString();
            }

            return url;

        } else if (Collaboration.isStreaming(mimeType)) {

            //下不了，可以忽略
            return new StringBuilder(ApiManager.getLiveBucket())
                    .append("/")
                    .append(key)
                    .append(".m3u8")
                    .toString();
        }

        return "";
    }

    public static void openMaterial(Activity activity, LibDoc bean) {

        String mimeType = bean.mimeType;

        if (Collaboration.isImage(mimeType)) {

            ArrayList<String> urls = new ArrayList<>(1);
            urls.add(bean.key);

            UIUtils.toImageViewActivity(activity, urls, bean.name);

        } else if (Collaboration.isVideo(mimeType)) {
            String url = "";

            if (Collaboration.TypeName.RECORDING_IN_LIBRARY.equals(bean.typeName)) {
                if(bean.key!=null&&bean.key.contains("H264")){//录播课转码的(老版本)
                    url = new StringBuilder(ApiManager.getLiveBucket()).append("/").append(bean.key).append(".mp4").toString();
                }else {//录播课(新版本)
                    url = new StringBuilder(ApiManager.getLiveBucket()).append("/").append(bean.key).toString();
                }
            } else {//普通多媒体
                url = new StringBuilder(ApiManager.getFileBucket()).append("/").append(bean.key).toString();
            }

            if (Build.VERSION.SDK_INT >= 16) {
                lanuchPlay(activity, bean.name, url);
            } else {
                Uri data = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(data, mimeType);
                activity.startActivity(intent);
            }


        } else if (Collaboration.isPPT(mimeType) || Collaboration.isPDF(mimeType) || Collaboration.isDoc(mimeType)) {

            //LibDoc.ExportImg[] imgs = bean.exported.images;
            ArrayList<LibDoc.ExportImg> imgs = getSortImgs(bean.exported.images);
            if (imgs != null) {
                ArrayList<String> urls = new ArrayList<>();

                for (LibDoc.ExportImg img : imgs) {
                    urls.add(img.name);
                }
                UIUtils.toImageViewActivity(activity, urls, bean.name);
            }

        } else if (Collaboration.isStreaming(mimeType)) {
//
//            Intent intent = new Intent(activity, PlayStreamingActivity.class);
//            intent.putExtra(PlayStreamingActivity.EXTRA_KEY, bean.key);
//
//            activity.startActivity(intent);


            String url = new StringBuilder(ApiManager.getLiveBucket())
                    .append("/")
                    .append(bean.key)
                    .append(".m3u8")
                    .toString();

            if (Build.VERSION.SDK_INT >= 16) {
                lanuchPlay(activity, bean.name, url);
            } else {
                Uri data = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(data, mimeType);

                List activities = activity.getPackageManager().queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                if (activities != null && activities.size() > 0) {
                    activity.startActivity(intent);
                } else {
                    Intent i = new Intent(activity, PlayStreamingActivity.class);
                    i.putExtra(Constants.KEY_LIB_DOC, bean);
                    activity.startActivity(i);
                }
            }
        } else {

            Toast.makeText(activity, "暂不支持打开此格式的文件", Toast.LENGTH_SHORT).show();
//            String url = new StringBuilder(ApiManager.getFileBucket()).append("/").append(bean.key).toString();
//
//            Uri data = Uri.parse(url);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(data, mimeType);
//            activity.startActivity(intent);

        }


    }

    public static ArrayList<LibDoc.ExportImg> getSortImgs(LibDoc.ExportImg[] imgs) {
        if (imgs == null) {
            return null;
        }

        ArrayList<LibDoc.ExportImg> sortImgs = new ArrayList<LibDoc.ExportImg>();
        try {
            for (LibDoc.ExportImg exportImg : imgs) {
                //get index
                String[] args = exportImg.name.split("-");
                exportImg.index = Integer.parseInt(args[args.length - 2]);

                sortImgs.add(exportImg);
            }

            Collections.sort(sortImgs, new ExportImgComparator());
            return sortImgs;
        } catch (Exception e) {
            for (LibDoc.ExportImg exportImg : imgs) {
                sortImgs.add(exportImg);
            }
        }

        return sortImgs;
    }

    private static class ExportImgComparator implements Comparator<LibDoc.ExportImg> {
        @Override
        public int compare(LibDoc.ExportImg img1, LibDoc.ExportImg img2) {
            return img1.index > img2.index ? 1 : (img1.index == img2.index ? 0 : -1);
        }
    }


    public static void lanuchPlay(Context context, String name, String uri) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("play target name: %s, uri: %s", name, uri);
        }

        Intent i = new Intent(context, PlayActivity.class);
        i.putExtra(PlayActivity.PREFER_EXTENSION_DECODERS, false)
                .putExtra(PlayActivity.EXTRA_PLAY_NAME, name)
                .setData(Uri.parse(uri))
                .setAction(PlayActivity.ACTION_VIEW);

        context.startActivity(i);
    }
}
