package com.benyuan.xiaojs;

import com.benyuan.xiaojs.common.xf_foundation.schemas.Ctl;
import com.benyuan.xiaojs.data.api.ApiManager;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.Duration;
import com.benyuan.xiaojs.model.Fee;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.junit.Test;

import java.math.BigDecimal;
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

        Fee fee = new Fee();
        fee.setCharge(BigDecimal.valueOf(199.11234567));
        fee.setFree(true);
        fee.setType(1);


        String jsonstr = ApiManager.objectToJsonString(fee);
        System.out.println(jsonstr);



        ObjectMapper objectMapper = new ObjectMapper();
        Fee fee1 = objectMapper.readValue(jsonstr,Fee.class);

        fee1.setType(0);



    }


}