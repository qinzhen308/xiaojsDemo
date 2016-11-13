package com.benyuan.xiaojs;

import com.benyuan.xiaojs.common.xf_foundation.schemas.Ctl;
import com.benyuan.xiaojs.data.api.ApiManager;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.Duration;
import com.benyuan.xiaojs.model.Fee;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.*;

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

        ServiceRequest serviceRequest = new ServiceRequest();

        String jj = serviceRequest.objectToJsonString(haha);


        System.out.println(jj);


        ObjectMapper objectMapper = new ObjectMapper();

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(Haha.class, Haha.No.class);

        Haha<Haha.No> haha1 = objectMapper.readValue(jj,javaType);

        System.out.println(haha);


    }


}