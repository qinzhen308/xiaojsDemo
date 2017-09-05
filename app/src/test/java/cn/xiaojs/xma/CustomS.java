package cn.xiaojs.xma;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by maxiaobao on 2017/9/5.
 */

public class CustomS extends JsonDeserializer<ArrayList<P>> {

    @Override
    public ArrayList<P> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        ArrayList<P> plist = new ArrayList<>();
        boolean goon = true;

        while (goon) {
            JsonToken t = jp.nextToken();
            switch (t.id()) {
                case JsonTokenId.ID_START_OBJECT:
                    System.out.println("-node: start object ");
                    try {
                        P pp = readObject(jp);
                        plist.add(pp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case JsonTokenId.ID_START_ARRAY:
                    System.out.println("-node: start array");
                    break;
                case JsonTokenId.ID_END_ARRAY:
                    System.out.println("-node: end array");
                    goon = false;
                    break;

                case JsonTokenId.ID_TRUE:
                    System.out.println("-node: true");
                    break;
                case JsonTokenId.ID_FALSE:
                    System.out.println("-node: false");

                    P p = new P();
                    p.temp = false;
                    plist.add(p);
                    break;
            }

        }

        System.out.println("-----xxxx----");

        return plist;
    }

    private P readObject(JsonParser p) throws Exception{

        P naP = p.getCodec().readValue(p, P.class);

        P xp = new P();

        String key;
        if (p.isExpectedStartObjectToken()) {
            key = p.nextFieldName();
        } else {
            JsonToken t = p.getCurrentToken();
            if (t == JsonToken.END_OBJECT || t != JsonToken.FIELD_NAME) {
                return xp;
            }
            key = p.getCurrentName();
        }
        for (; key != null; key = p.nextFieldName()) {
            JsonToken t = p.nextToken();
            switch (t.id()) {
                case JsonTokenId.ID_START_OBJECT:
                    System.out.println("xx start object");
                    break;
                case JsonTokenId.ID_END_OBJECT:
                    System.out.println("xx end object");
                    break;
                case JsonTokenId.ID_NUMBER_INT:
                    System.out.println("xx int value");
                    if ("y".equals(key)) {
                        xp.y = p.getIntValue();
                    }

                    if ("x".equals(key)) {
                        xp.x = p.getIntValue();
                    }

                    break;
            }
        }
        return xp;
    }
}
