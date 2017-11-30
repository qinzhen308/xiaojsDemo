package cn.xiaojs.xma.common.glideextend;

import android.text.TextUtils;
import android.util.Base64;

import com.bumptech.glide.load.data.ByteArrayFetcher;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.stream.StreamModelLoader;

import java.io.InputStream;

/**
 * Created by Paul Z on 2017/11/29.
 * glide加载图片的数据源为base64字符串
 * 注意：缓存没处理，glide默认缓存，导致所有以此方式的加载的图片都是重复的；因此，目前先去掉缓存。
 *       而且以此方式加载的图片，一般不需要缓存
 */

public class StreamBase64StringLoader implements StreamModelLoader<String> {
    private final String id;

    public StreamBase64StringLoader() {
        this("");
    }

    public StreamBase64StringLoader(String id) {
        this.id = id;
    }

    @Override
    public DataFetcher<InputStream> getResourceFetcher(String model, int width, int height) {
        byte[] bytes = null;

        if(!TextUtils.isEmpty(model)){
            model=model.substring(model.indexOf(",")+1);
            bytes= Base64.decode(model, Base64.DEFAULT);
        }
        return new ByteArrayFetcher(bytes, id);
    }

}