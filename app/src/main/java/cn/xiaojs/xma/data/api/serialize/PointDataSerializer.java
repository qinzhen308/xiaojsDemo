package cn.xiaojs.xma.data.api.serialize;

import android.graphics.PointF;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.xiaojs.xma.data.api.service.XiaojsService;

/**
 * Created by maxiaobao on 2017/6/2.
 */

public class PointDataSerializer extends JsonSerializer<ArrayList<PointF>>{

    @Override
    public void serialize(ArrayList<PointF> value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartArray();
        for (PointF p:value){
            if(p==null){
                gen.writeBoolean(false);
            }else {
//                gen.writeObject(p);
                gen.writeObject(p);
            }
        }
        gen.writeEndArray();
    }
}
