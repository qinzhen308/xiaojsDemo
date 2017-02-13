package cn.xiaojs.xma.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/2/10.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrivateHome {

    public PriProfile profile;
    //public competencies;
    public int countOfUnreadN;
    public PriCourse courses;
    //public classes;



    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriProfile {
        public String sex;
        public String title;
        public String typeName;
        public Follow stats;
        public boolean isPerson;
        public String id;
        public String name;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriCourse {
        public int total;
        public PriLesson[] lessons;

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PriLesson {
        public String id;
        public String title;
        public String type;
        public String state;
        public String teacher;
        public String[] assistants;
        public String[] students;
        //public schedule;
        //public enroll;

    }

}
