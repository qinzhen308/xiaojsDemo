package cn.xiaojs.xma.model.social;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created by maxiaobao on 2016/12/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact implements Serializable{

    public String id;
    public String title;
    public String subject;
    public String subtype;
    public String state;
    public String startedOn;

    public String avatar;
    public String account;
    public String alias;
    public int followType;

    public String name;
    public String unread;
    public String lastMessage;
    public long group;

    public String cover;

    public boolean unfollowable;

//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
////        dest.writeString(id);
////        dest.writeString(title);
////        dest.writeString(subject);
////        dest.writeString(subtype);
////        dest.writeString(state);
////        dest.writeString(startedOn);
////
////        dest.writeString(avatar);
////        dest.writeString(account);
//        dest.writeString(alias);
//        dest.writeInt(followType);
//    }
//
//    public Contact() {
//
//    }
//
//    private Contact(Parcel in){
//        id = in.readString();
//        title = in.readString();
//        subject = in.readString();
//        subtype = in.readString();
//        state = in.readString();
//        startedOn = in.readString();
//
//        avatar = in.readString();
//        account = in.readString();
//        alias = in.readString();
//        followType = in.readInt();
//    }
//
//    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
//        public Contact createFromParcel(Parcel in) {
//            return new Contact(in);
//        }
//
//        public Contact[] newArray(int size) {
//            return new Contact[size];
//        }
//    };
}
