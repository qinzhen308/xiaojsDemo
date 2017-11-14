package cn.xiaojs.xma.model.social;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;

/**
 * Created by maxiaobao on 2016/12/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact implements Serializable {

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
    public int unread;
    public String lastMessage;
    public long lastTalked;
    public long group;

    public String cover;

    public String chatId;

    public boolean unfollowable;
    public String ticket;

    public String owner;
    public String ownerId;
    public String total;
    public String current;
    public boolean retainOnTalk;
    public MetIn metIn;
    public boolean silent;
    public String signature;

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Contact) {
            Contact otact = (Contact) obj;
            return id.equals(otact.id);
        }

        return super.equals(obj);
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetIn {
        public String id;
        public String title;
        public String subtype;
    }

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
