package cn.xiaojs.xma.ui.classroom2.util;

import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.model.material.LibDoc;

/**
 * Created by maxiaobao on 2017/10/26.
 */

public class MaterialUtil {

    public static String getVideoUrl(LibDoc doc) {

        if (Collaboration.isVideo(doc.mimeType)) {

            StringBuilder url;

            if (Collaboration.TypeName.RECORDING_IN_LIBRARY.equals(doc.typeName)) {

                url = new StringBuilder(ApiManager.getLiveBucket());

                if (doc.key != null && doc.key.contains("H264")) {//录播课转码的(老版本)
                    url.append("/")
                            .append(doc.key)
                            .append(".mp4");
                } else {//录播课(新版本)
                    url.append("/")
                            .append(doc.key);
                }
            } else {//普通多媒体
                url = new StringBuilder(ApiManager.getFileBucket())
                        .append("/")
                        .append(doc.key);
            }

            return url.toString();

        } else if (Collaboration.isStreaming(doc.mimeType)) {
            StringBuilder url = new StringBuilder(ApiManager.getLiveBucket())
                    .append("/")
                    .append(doc.key)
                    .append(".m3u8");

            return url.toString();
        } else {
            return "";
        }
    }

}
