package cn.xiaojs.xma;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Error;
import cn.xiaojs.xma.model.social.Comment;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(4, 2 + 2);


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
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Haha.class, Haha.No.class);
//
//        Haha<Haha.No> haha1 = objectMapper.readValue(jj,javaType);
//
//        System.out.println(haha);

//        String j = "{\"ec\": \"0x01000001\", \"details\": \"non-sensitive only, e.g. bad param on SharedErrs.BadParameter error, requires XF 0.3.0 or later\"}";
//
//        ObjectMapper mapper = new ObjectMapper();
//        Error error = mapper.readValue(j, Error.class);
//
//        System.out.println("error details:"+error.details);
//        System.out.println("error ec:"+error.ec);

        String j = "{\n" +
                "  \"page\": 1,\n" +
                "  \"numOfPages\": 1,\n" +
                "  \"objectsOfPage\": [\n" +
                "    {\n" +
                "      \"createdBy\": {\n" +
                "        \"basic\": {\n" +
                "          \"name\": \"Ringo\"\n" +
                "        },\n" +
                "        \"isPerson\": false,\n" +
                "        \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
                "      },\n" +
                "      \"createdOn\": \"2016-12-26T06:52:42.481Z\",\n" +
                "      \"body\": {\n" +
                "        \"text\": \"Well done, buddy\"\n" +
                "      },\n" +
                "      \"id\": \"5860be3a11e48a4c27feff13\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"createdBy\": {\n" +
                "        \"basic\": {\n" +
                "          \"name\": \"Ringo\"\n" +
                "        },\n" +
                "        \"isPerson\": false,\n" +
                "        \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
                "      },\n" +
                "      \"createdOn\": \"2016-12-26T06:22:36.944Z\",\n" +
                "      \"body\": {\n" +
                "        \"text\": \"Good, buddy\"\n" +
                "      },\n" +
                "      \"id\": \"5860b72cce9f2c4a23dc09ae\",\n" +
                "      \"replies\": [\n" +
                "        {\n" +
                "          \"createdBy\": {\n" +
                "            \"basic\": {\n" +
                "              \"name\": \"Ringo\"\n" +
                "            },\n" +
                "            \"isPerson\": false,\n" +
                "            \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
                "          },\n" +
                "          \"createdOn\": \"2016-12-26T06:22:57.174Z\",\n" +
                "          \"body\": {\n" +
                "            \"text\": \":)\"\n" +
                "          },\n" +
                "          \"id\": \"5860b741ce9f2c4a23dc09b0\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"createdBy\": {\n" +
                "            \"basic\": {\n" +
                "              \"name\": \"Ringo\"\n" +
                "            },\n" +
                "            \"isPerson\": false,\n" +
                "            \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
                "          },\n" +
                "          \"createdOn\": \"2016-12-26T06:23:34.242Z\",\n" +
                "          \"body\": {\n" +
                "            \"text\": \":p\"\n" +
                "          },\n" +
                "          \"id\": \"5860b766ce9f2c4a23dc09b4\",\n" +
                "          \"replies\": [\n" +
                "            {\n" +
                "              \"createdBy\": {\n" +
                "                \"basic\": {\n" +
                "                  \"name\": \"Ringo\"\n" +
                "                },\n" +
                "                \"isPerson\": false,\n" +
                "                \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
                "              },\n" +
                "              \"createdOn\": \"2016-12-26T07:03:32.945Z\",\n" +
                "              \"body\": {\n" +
                "                \"text\": \"I dont's know how to express this\"\n" +
                "              },\n" +
                "              \"id\": \"5860c0c40e2191912babeed8\",\n" +
                "              \"replies\": [\n" +
                "                {\n" +
                "                  \"createdBy\": {\n" +
                "                    \"basic\": {\n" +
                "                      \"name\": \"Ringo\"\n" +
                "                    },\n" +
                "                    \"isPerson\": false,\n" +
                "                    \"id\": \"5860a0a6f127486b0b1a6bf6\"\n" +
                "                  },\n" +
                "                  \"createdOn\": \"2016-12-26T07:03:42.432Z\",\n" +
                "                  \"body\": {\n" +
                "                    \"text\": \"You bet?\"\n" +
                "                  },\n" +
                "                  \"id\": \"5860c0ce0e2191912babeeda\"\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";


        ObjectMapper mapper = new ObjectMapper();
        CollectionPage<Comment> comms = mapper.readValue(j, CollectionPage.class);


    }


}