package cn.xiaojs.xma.data.api.serialize;

import android.graphics.PointF;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;

import cn.xiaojs.xma.XiaojsConfig;

/**
 * Created by maxiaobao on 2017/9/5.
 */

public class PointDataDeserializer extends JsonDeserializer<ArrayList<PointF>> {

    @Override
    public ArrayList<PointF> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        ArrayList<PointF> points = new ArrayList<>();
        boolean goon = true;

        while (goon) {
            JsonToken t = jp.nextToken();
            switch (t.id()) {
                case JsonTokenId.ID_START_OBJECT:
                    if (XiaojsConfig.DEBUG) {
                        //Logger.d("-node: start object ");
                    }
                    PointF point = jp.getCodec().readValue(jp, PointF.class);
                    points.add(point);
                    break;
                case JsonTokenId.ID_START_ARRAY:
                    if (XiaojsConfig.DEBUG) {
                        //Logger.d("-node: start array ");
                    }
                    break;
                case JsonTokenId.ID_END_ARRAY:
                    if (XiaojsConfig.DEBUG) {
                        //Logger.d("-node: end array ");
                    }
                    goon = false;
                    break;
                case JsonTokenId.ID_TRUE:
                    if (XiaojsConfig.DEBUG) {
                        //Logger.d("-node: true ");
                    }
                    break;
                case JsonTokenId.ID_FALSE:
                    if (XiaojsConfig.DEBUG) {
                        //Logger.d("-node: false ");
                    }
                    points.add(null);
                    break;
            }

        }
        return points;

    }
}
