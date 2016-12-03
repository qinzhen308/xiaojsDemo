package com.benyuan.xiaojs;

import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

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

        Haha.No h = new Haha.No();
        h.setNum(100);

        Haha<Haha.No> haha = new Haha<>();
        haha.setId("123456");
        haha.setT(h);

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


    }


}