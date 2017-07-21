package cn.xiaojs.xma.model.ctl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.Teacher;

/**
 * Created by maxiaobao on 2017/7/21.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CRecordLesson {

    public String title;
    public String subject;
    public Enroll enroll;
    public int mode;
    public TeachLead teaching;
    public String cover;
    public LiveLesson.TeachersIntro teachersIntro;
    public String[] tags;
    public boolean accessible;
    public boolean autoOnShelves;
    public long effective;
    public CChapter[] chapters;


}
