package com.benyuan.xiaojs;

import com.benyuan.xiaojs.common.xf_foundation.schemas.Ctl;
import com.benyuan.xiaojs.data.api.ApiManager;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import org.junit.Test;

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


        Duration duration = new Duration();
        duration.setStart(new Date(System.currentTimeMillis()-(3600*1000*24)));
        duration.setEnd(new Date(System.currentTimeMillis()));

        Criteria criteria = new Criteria();
        criteria.setSource(Ctl.LessonSource.ALL);
        criteria.setDuration(duration);

        String jsonstr = ApiManager.objectToJsonString(criteria);
        System.out.println(jsonstr);




    }


}