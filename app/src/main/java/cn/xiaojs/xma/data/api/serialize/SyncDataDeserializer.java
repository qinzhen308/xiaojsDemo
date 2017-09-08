package cn.xiaojs.xma.data.api.serialize;


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
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;

/**
 * Created by maxiaobao on 2017/9/5.
 */

public class SyncDataDeserializer extends JsonDeserializer<ArrayList<SyncData>> {

    @Override
    public ArrayList<SyncData> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        ArrayList<SyncData> syncDatas = new ArrayList<>();

        if (jp.isExpectedStartObjectToken()) {
            if (XiaojsConfig.DEBUG) {
                //Logger.d("isExpectedStartObjectToken");
            }
            //System.out.println("isExpectedStartObjectToken");
            SyncData syncData = jp.getCodec().readValue(jp, SyncData.class);
            syncDatas.add(syncData);
            return syncDatas;
        }

        boolean goon = true;
        while (goon) {
            JsonToken t = jp.nextToken();
            switch (t.id()) {
                case JsonTokenId.ID_START_OBJECT:
                    if (XiaojsConfig.DEBUG) {
                        //Logger.d("-node: start object ");
                        //System.out.println("-node: start object ");
                    }
                    SyncData syncData = jp.getCodec().readValue(jp, SyncData.class);
                    syncDatas.add(syncData);
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
            }

        }
        return syncDatas;

    }
}
