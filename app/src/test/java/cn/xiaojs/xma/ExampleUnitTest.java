package cn.xiaojs.xma;

import android.text.TextUtils;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Error;
import cn.xiaojs.xma.model.ctl.Price;
import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(4, 2 + 2);

//        String url = "http://192.168.100.3/live/590ad80c25558febef0f6957/1";
//
//        String[] items = url.split("/");
//
//        for (String item: items) {
//            System.out.println(item);
//        }


//        String v = "1.0.7.35";
//
//        int pos = v.lastIndexOf(".");
//        System.out.println("pos:" + pos);
//        String newstr = v.substring(0,pos);
//
//        System.out.println("new v:" + newstr);

//        TEST test = null;
//
//        switch (test){
//            case NO:
//                break;
//        }


//        Duration duration = new Duration();
//        duration.setStart(new Date(System.currentTimeMillis()-(3600*1000*24)));
//        duration.setEnd(new Date(System.currentTimeMillis()));
//
//        Criteria criteria = new Criteria();
//        criteria.setSource(Ctl.LessonSource.ALL);
//        criteria.setDuration(duration);
//
//        String jsonstr = ApiManager.objectToJsonString(criteria);
//        System.out.println(jsonstr);

//        Fee fee = new Fee();
//        fee.setCharge(BigDecimal.valueOf(199.11234567));
//        fee.setFree(true);
//        fee.setType(1);
//
//        ServiceRequest serviceRequest = new ServiceRequest();
//
//        String jsonstr = serviceRequest.objectToJsonString(fee);
//        System.out.println(jsonstr);
//
//
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        Fee fee1 = objectMapper.readValue(jsonstr,Fee.class);
//
//        fee1.setType(0);

//        Haha.No h = new Haha.No();
//        h.setNum(100);
//
//        Haha<Haha.No> haha = new Haha<>();
//        haha.setId("123456");
//        haha.setT(h);

//        ServiceRequest serviceRequest = new ServiceRequest();
//
//        String jj = serviceRequest.objectToJsonString(haha);
//
//
//        System.out.println(jj);
//
//
 //       ObjectMapper objectMapper = new ObjectMapper();

        //JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Haha.class, Haha.No.class);

        // Haha<Haha.No> haha1 = objectMapper.readValue(jj,javaType);

//        System.out.println(haha);

//        String j = "{\"ec\": \"0x01000001\", \"details\": \"non-sensitive only, e.g. bad param on SharedErrs.BadParameter error, requires XF 0.3.0 or later\"}";
//
//        ObjectMapper mapper = new ObjectMapper();
//        Error error = mapper.readValue(j, Error.class);
//
//        System.out.println("error details:"+error.details);
//        System.out.println("error ec:"+error.ec);

//        String j = "{\n" +
//                "  \"page\": 1,\n" +
//                "  \"numOfPages\": 1,\n" +
//                "  \"objectsOfPage\": [\n" +
//                "    {\n" +
//                "      \"createdBy\": {\n" +
//                "        \"basic\": {\n" +
//                "          \"name\": \"Ringo\"\n" +
//                "        },\n" +
//                "        \"isPerson\": false,\n" +
//                "        \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
//                "      },\n" +
//                "      \"createdOn\": \"2016-12-26T06:52:42.481Z\",\n" +
//                "      \"body\": {\n" +
//                "        \"text\": \"Well done, buddy\"\n" +
//                "      },\n" +
//                "      \"id\": \"5860be3a11e48a4c27feff13\"\n" +
//                "    },\n" +
//                "    {\n" +
//                "      \"createdBy\": {\n" +
//                "        \"basic\": {\n" +
//                "          \"name\": \"Ringo\"\n" +
//                "        },\n" +
//                "        \"isPerson\": false,\n" +
//                "        \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
//                "      },\n" +
//                "      \"createdOn\": \"2016-12-26T06:22:36.944Z\",\n" +
//                "      \"body\": {\n" +
//                "        \"text\": \"Good, buddy\"\n" +
//                "      },\n" +
//                "      \"id\": \"5860b72cce9f2c4a23dc09ae\",\n" +
//                "      \"replies\": [\n" +
//                "        {\n" +
//                "          \"createdBy\": {\n" +
//                "            \"basic\": {\n" +
//                "              \"name\": \"Ringo\"\n" +
//                "            },\n" +
//                "            \"isPerson\": false,\n" +
//                "            \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
//                "          },\n" +
//                "          \"createdOn\": \"2016-12-26T06:22:57.174Z\",\n" +
//                "          \"body\": {\n" +
//                "            \"text\": \":)\"\n" +
//                "          },\n" +
//                "          \"id\": \"5860b741ce9f2c4a23dc09b0\"\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"createdBy\": {\n" +
//                "            \"basic\": {\n" +
//                "              \"name\": \"Ringo\"\n" +
//                "            },\n" +
//                "            \"isPerson\": false,\n" +
//                "            \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
//                "          },\n" +
//                "          \"createdOn\": \"2016-12-26T06:23:34.242Z\",\n" +
//                "          \"body\": {\n" +
//                "            \"text\": \":p\"\n" +
//                "          },\n" +
//                "          \"id\": \"5860b766ce9f2c4a23dc09b4\",\n" +
//                "          \"replies\": [\n" +
//                "            {\n" +
//                "              \"createdBy\": {\n" +
//                "                \"basic\": {\n" +
//                "                  \"name\": \"Ringo\"\n" +
//                "                },\n" +
//                "                \"isPerson\": false,\n" +
//                "                \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
//                "              },\n" +
//                "              \"createdOn\": \"2016-12-26T07:03:32.945Z\",\n" +
//                "              \"body\": {\n" +
//                "                \"text\": \"I dont's know how to express this\"\n" +
//                "              },\n" +
//                "              \"id\": \"5860c0c40e2191912babeed8\",\n" +
//                "              \"replies\": [\n" +
//                "                {\n" +
//                "                  \"createdBy\": {\n" +
//                "                    \"basic\": {\n" +
//                "                      \"name\": \"Ringo\"\n" +
//                "                    },\n" +
//                "                    \"isPerson\": false,\n" +
//                "                    \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
//                "                  },\n" +
//                "                  \"createdOn\": \"2016-12-26T07:03:42.432Z\",\n" +
//                "                  \"body\": {\n" +
//                "                    \"text\": \"You bet?\"\n" +
//                "                  },\n" +
//                "                  \"id\": \"5860c0ce0e2191912babeeda\"\n" +
//                "                }\n" +
//                "              ]\n" +
//                "            }\n" +
//                "          ]\n" +
//                "        }\n" +
//                "      ]\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";
//
//
//        ObjectMapper mapper = new ObjectMapper();
//        CollectionPage<Comment> comms = mapper.readValue(j, CollectionPage.class);
//
//
//
//        {
//            "fee": {
//            "free": false,
//                    "type": 2,
//                    "charge": 200,
//                    "total": 400,
//                    "discounted": {
//                "applied": [
//                {
//                    "discount": 9.8,
//                        "before": 3
//                }
//                ],
//                "subtotal": 392,
//                        "ratio": 9.8,
//                        "saved": 8
//            }
//        }
//        }
//    }

        //String j = "{\"temp\":\"nihao\", \"data\":[{\"x\":1,\"y\":11}, false, {\"x\":2,\"y\":22}]}";
//String j = "{\"datax\":{\"layer\":{\"id\":\"59ae4c9e36102905adca6998\",\"shape\":{\"data\":[{\"x\":-73,\"y\":-49},false,{\"x\":-73,\"y\":49},{\"x\":73,\"y\":49},{\"x\":73,\"y\":-49},{\"x\":-73,\"y\":-49}],\"type\":\"drawContinuous\",\"width\":146,\"height\":98,\"left\":1172,\"top\":1195},\"index\":0,\"visible\":true,\"owner\":\"59377e80b14a46b8911cde59\"}}}";
//
//


//        String j = "{\"board\":\"59af5af914b94394f9791a88\",\"evt\":5,\"stg\":3,\"datax\":[{\"grouped\":false,\"id\":\"59b0badd3610291e9f51d757\"},{\"grouped\":false,\"id\":\"59b0bade3610291e9f51d75a\"}],\"time\":1504754461883,\"from\":\"59377e80b14a46b8911cde59\"}";
//String j = "{\"board\": \"59af5af914b94394f9791a88\",\"evt\": 1,\"stg\": 1,\"ctx\": {\"viewport\": {\"width\": 1440,\"height\": 810}},\"datax\": {\"direction\": 0},\"time\": 1504686463708,\"from\": \"59377e80b14a46b8911cde59\"}";

        //String j = "{\"board\":\"59af5af914b94394f9791a88\",\"evt\":7,\"stg\":3,\"data\":{\"layer\":{\"id\":\"59b1154a3610299de53e8e7c\",\"pos1\":{\"x\":-89.5,\"y\":-70},\"pos2\":{\"x\":89.5,\"y\":-70},\"pos3\":{\"x\":-89.5,\"y\":70},\"pos4\":{\"x\":89.5,\"y\":70},\"selDot1\":{\"x\":-109.5,\"y\":-90},\"selDot2\":{\"x\":109.5,\"y\":-90},\"selDot3\":{\"x\":-109.5,\"y\":90},\"selDot4\":{\"x\":109.5,\"y\":90},\"rotateDot\":{\"x\":0,\"y\":-130},\"moveDot\":{\"x\":293.5,\"y\":202},\"angle\":0,\"info\":[\"http:\\/\\/omjsoavuo.bkt.clouddn.com\\/4-591c29dfb380bd6710347a101504776064938?imageView2\\/2\\/w\\/1440\",{}],\"grouped\":false,\"groupId\":null,\"left\":204,\"top\":132,\"width\":179,\"height\":140,\"size\":{\"w\":179,\"h\":140},\"lineWidth\":1,\"lineColor\":\"rgba(0,0,0,1)\",\"position\":{\"x\":204,\"y\":132},\"bounds\":{\"leftBottom\":{\"x\":204,\"y\":272},\"rigthTop\":{\"x\":383,\"y\":132},\"leftTop\":{\"x\":204,\"y\":132},\"rightBottom\":{\"x\":383,\"y\":272},\"left\":204,\"right\":383,\"bottom\":272,\"top\":132},\"shape\":{\"data\":[{\"x\":-89.5,\"y\":-70},{\"x\":89.5,\"y\":70}],\"type\":\"drawImage\",\"width\":179,\"height\":140,\"left\":204,\"top\":132},\"index\":0,\"visible\":true,\"owner\":\"59377e80b14a46b8911cde59\"}},\"time\":1504777551141,\"from\":\"59377e80b14a46b8911cde59\"}";

//        String j = "{\"board\":\"59af5af914b94394f9791a88\",\"evt\":7,\"stg\":3,\"data\":{\"layer\":{\"id\":\"59b1154a3610299de53e8e7c\",\"pos1\":{\"x\":-89.5,\"y\":-70},\"pos2\":{\"x\":89.5,\"y\":-70},\"pos3\":{\"x\":-89.5,\"y\":70},\"pos4\":{\"x\":89.5,\"y\":70},\"selDot1\":{\"x\":-109.5,\"y\":-90},\"selDot2\":{\"x\":109.5,\"y\":-90},\"selDot3\":{\"x\":-109.5,\"y\":90},\"selDot4\":{\"x\":109.5,\"y\":90},\"rotateDot\":{\"x\":0,\"y\":-130},\"moveDot\":{\"x\":293.5,\"y\":202},\"angle\":0,\"infox\":[\"http:\\/\\/omjsoavuo.bkt.clouddn.com\\/4-591c29dfb380bd6710347a101504776064938?imageView2\\/2\\/w\\/1440\",{}],\"grouped\":false,\"groupId\":null,\"left\":204,\"top\":132,\"width\":179,\"height\":140,\"size\":{\"w\":179,\"h\":140},\"lineWidth\":1,\"lineColor\":\"rgba(0,0,0,1)\",\"position\":{\"x\":204,\"y\":132},\"bounds\":{\"leftBottom\":{\"x\":204,\"y\":272},\"rigthTop\":{\"x\":383,\"y\":132},\"leftTop\":{\"x\":204,\"y\":132},\"rightBottom\":{\"x\":383,\"y\":272},\"left\":204,\"right\":383,\"bottom\":272,\"top\":132},\"shape\":{\"data\":[{\"x\":-89.5,\"y\":-70},{\"x\":89.5,\"y\":70}],\"type\":\"drawImage\",\"width\":179,\"height\":140,\"left\":204,\"top\":132},\"index\":0,\"visible\":true,\"owner\":\"59377e80b14a46b8911cde59\"}},\"time\":1504777551141,\"from\":\"59377e80b14a46b8911cde59\"}";
//
//        ObjectMapper mapper = new ObjectMapper();
//        SyncBoardReceive data = mapper.readValue(j, SyncBoardReceive.class);
//
//        System.out.println("=============="+data.data);
////        ObjectMapper mapper = new ObjectMapper();
//        String e = "{\"discount\":9.8,\"quota\":2,\"before\":3}";
////        Price.Applied p = mapper.readValue(e, Price.Applied.class);
////        System.out.println("=============="+p.quota);


//        int a = 5;
//        int b = ++a;

//        String v = "0.1.4.7";
//        String[] s = v.split("\\.");
//
//        System.out.println("=============="+s);


//        long tt = System.currentTimeMillis();
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//
//        long time= sdf.parse("2017-03-03T08:30:00.000Z").getTime();

//        String md = "7E:64:4C:AB:E3:FE:25:60:4E:B1:E4:6E:8D:EB:19:95";
//        String r = md.replaceAll(":","").toLowerCase();
//        System.out.println(r);　

//        String dd = "2017-03-05T18:30:30.000Z";
//        String datew = distanceDay(dd);
//
//
//        System.out.println(datew);

    }




//    public static String formatDate(String date, String format) {
////        if (TextUtils.isEmpty(date)) {
////            return "";
////        }
//        String str = date;
//        if (str.contains("T")) {
//            str = str.replace("T", " ");
//        }
//        if (str.contains("/")) {
//            str = str.replaceAll("/", "-");
//        }
//        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
//        Date d = null;
//        try {
//            d = sdf.parse(str);
//            return sdf.format(d);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return "";
//        }
//    }


    public enum TEST {
        NO,
        YES
    }


}