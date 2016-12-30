package cn.xiaojs.xma.model.social;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/12/12.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact implements Parcelable{

    public String name;
    public String avatar;

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAvatar() {
        return this.avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    /////////////////////////////////////////////////////////////////

    public String account;
    public String unread;
    public String lastMessage;
    public long group;
    public String alias;
    public String subtype;
    public int followType;
    public String id;
    public String subject;
    public String title;
    public String state;
    public String startedOn;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(account);
        dest.writeString(alias);
        dest.writeString(subtype);
    }

    public Contact() {

    }

    private Contact(Parcel in){
        account = in.readString();
        alias = in.readString();
        subtype = in.readString();
    }

    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
}
