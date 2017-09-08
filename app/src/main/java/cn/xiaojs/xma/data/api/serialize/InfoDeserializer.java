package cn.xiaojs.xma.data.api.serialize;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncInfo;

/**
 * Created by maxiaobao on 2017/9/5.
 */

public class InfoDeserializer extends JsonDeserializer<SyncInfo> {

    @Override
    public SyncInfo deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        SyncInfo info = new SyncInfo();

        if (jp.isExpectedStartArrayToken()) {

            boolean goon = true;
            while (goon) {
                JsonToken t = jp.nextToken();
                switch (t.id()) {
                    case JsonTokenId.ID_START_OBJECT:
                        if (XiaojsConfig.DEBUG) {
                            //Logger.d("-node: start object ");
                            //System.out.println("-node: start object ");
                        }
                        //ingore
                        break;
                    case JsonTokenId.ID_START_ARRAY:
                        if (XiaojsConfig.DEBUG) {
                            //Logger.d("-node: start array ");
                            //System.out.println("-node: start array ");
                        }
                        break;
                    case JsonTokenId.ID_END_ARRAY:
                        if (XiaojsConfig.DEBUG) {
                            //Logger.d("-node: end array ");
                            //System.out.println("-node: end array ");
                        }
                        goon = false;
                        break;
                    case JsonTokenId.ID_STRING:
                        info.imgUrl = jp.getValueAsString();
                        break;
                    case JsonTokenId.ID_NUMBER_FLOAT:
                        info.size = jp.getFloatValue();
                        break;
                }

            }

        }

        return info;

    }
}
