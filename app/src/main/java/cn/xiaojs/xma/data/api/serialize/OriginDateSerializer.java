package cn.xiaojs.xma.data.api.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.xiaojs.xma.data.api.service.XiaojsService;

/**
 * Created by maxiaobao on 2017/6/2.
 */

public class OriginDateSerializer extends JsonSerializer<Date>{

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        SimpleDateFormat formatter = new SimpleDateFormat(XiaojsService.DATE_FORMAT);
        String formattedDate = formatter.format(value);
        gen.writeString(formattedDate);
    }
}
