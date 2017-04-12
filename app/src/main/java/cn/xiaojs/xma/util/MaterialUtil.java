package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.ArrayList;

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

        }else if (Collaboration.isVideo(mimeType)) {

            String url = new StringBuilder(ApiManager.getFileBucket()).append("/").append(bean.key).toString();

            Uri data = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(data, mimeType);
            activity.startActivity(intent);

        }else if (Collaboration.isPPT(mimeType)) {

            LibDoc.ExportImg[] imgs = bean.exported.images;
            ArrayList<String> urls = new ArrayList<>();

            for (LibDoc.ExportImg img : imgs) {
                urls.add(img.name);
            }
            UIUtils.toImageViewActivity(activity, urls);

        }else {

        }


    }
}
