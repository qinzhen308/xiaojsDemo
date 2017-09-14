package cn.xiaojs.xma.data.api.serialize;



import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.URLDecoder;

import cn.xiaojs.xma.model.socket.room.whiteboard.BoardLayerSet;

/**
 * Created by maxiaobao on 2017/9/5.
 */

public class BoardLayerSetDeserializer extends JsonDeserializer<BoardLayerSet> {

    @Override
    public BoardLayerSet deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

//        String dest= URLDecoder.decode(jp.getValueAsString());
//        Logger.d("-----qz---decode----layers="+jp.getValueAsString());
        ObjectMapper om=new ObjectMapper();
        BoardLayerSet set=om.readValue(jp.getValueAsString(),BoardLayerSet.class);
        return set;

    }
}
