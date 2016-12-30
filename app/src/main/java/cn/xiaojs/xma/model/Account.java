package cn.xiaojs.xma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by maxiaobao on 2016/10/31.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Account implements Serializable{

    private String id;
    private Basic basic;
    private Phone phone;

    //public boolean isPerson;

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

//    public boolean isPerson() {
//        return isPerson;
//    }
//
//    public void setPerson(boolean person) {
//        isPerson = person;
//    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Basic implements Serializable{

        private String title;

        // true means man, otherwise woman
        private String sex;

        private String name;

        private String avatar;

        private Date birthday;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Phone {
        public String subsNum;
    }


    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SimpleAccount {
        public String id;
        public String name;
    }
}
