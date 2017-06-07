package cn.xiaojs.xma.model.ctl;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.account.Account;

/**
 * Created by maxiaobao on 2017/5/26.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CLesson implements Parcelable{
    public String id;
    public String title;
    public String type;
    public String ticket;
    public Schedule schedule;
    public Enroll enroll;
    public String state;
    public Account owner;
    public Account teacher;
    public Account[] assistants;
    @JsonProperty("class")
    public ClassInfo classInfo;
    public Adviser[] advisers;



    protected CLesson(Parcel in) {
        id = in.readString();
        title = in.readString();
        type = in.readString();
        ticket = in.readString();
        state = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(ticket);
        dest.writeString(state);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CLesson> CREATOR = new Creator<CLesson>() {
        @Override
        public CLesson createFromParcel(Parcel in) {
            return new CLesson(in);
        }

        @Override
        public CLesson[] newArray(int size) {
            return new CLesson[size];
        }
    };
}
