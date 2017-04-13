package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.material.LibDoc;

/**
 * Created by maxiaobao on 2017/4/12.
 */

public class MaterialUtil {

    public static void openMaterial(Activity activity, LibDoc bean) {

        String mimeType = bean.mimeType;

        if (Collaboration.isImage(mimeType)) {

            ArrayList<String> urls = new ArrayList<>(1);
            urls.add(bean.key);

            UIUtils.toImageViewActivity(activity, urls);

        } else if (Collaboration.isVideo(mimeType)) {

            String url = new StringBuilder(ApiManager.getFileBucket()).append("/").append(bean.key).toString();

            Uri data = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(data, mimeType);
            activity.startActivity(intent);

        } else if (Collaboration.isPPT(mimeType)) {

            //LibDoc.ExportImg[] imgs = bean.exported.images;
            ArrayList<LibDoc.ExportImg> imgs = getSortImgs(bean.exported.images);
            if (imgs != null) {
                ArrayList<String> urls = new ArrayList<>();

                for (LibDoc.ExportImg img : imgs) {
                    urls.add(img.name);
                }
                UIUtils.toImageViewActivity(activity, urls);
            }

        } else {

            Toast.makeText(activity,"暂不支持打开此格式的文件",Toast.LENGTH_SHORT).show();
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
                exportImg.index = Integer.parseInt(args[3]);

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
}
