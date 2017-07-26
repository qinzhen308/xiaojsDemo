package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.LiveLesson;

/**
 * Created by maxiaobao on 2017/7/21.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CRecordLesson {

    public String title;
    public String subject;
    public EnrollMode enroll;
    public int mode;
    public TeachLead teaching;
    public String cover;
    public LiveLesson.TeachersIntro teachersIntro;
    public LiveLesson.Overview overview;
    public String[] tags;
    public boolean accessible;
    public boolean autoOnShelves;
    public Long effective;
    public CChapter[] chapters;


    public static class EnrollMode{
        public int mode;
        public int max;
    }

}
