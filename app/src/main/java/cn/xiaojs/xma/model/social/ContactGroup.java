package cn.xiaojs.xma.model.social;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;

import cn.xiaojs.xma.model.account.Account;

/**
 * Created by maxiaobao on 2016/12/12.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactGroup implements Serializable{


    //public ArrayList<Contact> contacts;
    //////////////////////////////////////////////////
    public String name;
    public String set;
    public long group;
    public ArrayList<Contact> collection;

    public String id;
    public Account.SimpleAccount subject;


//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//
//        dest.writeString(name);
//        dest.writeString(set);
//        dest.writeLong(group);
//        dest.writeTypedList(collection);
//    }
//
//    public ContactGroup() {
//
//    }
//
//    private ContactGroup(Parcel in){
//        name = in.readString();
//        set = in.readString();
//        group = in.readLong();
//        collection = in.readArrayList(Contact.class.getClassLoader());
//    }
//
//    public static final Parcelable.Creator<ContactGroup> CREATOR = new Parcelable.Creator<ContactGroup>() {
//        public ContactGroup createFromParcel(Parcel in) {
//            return new ContactGroup(in);
//        }
//
//        public ContactGroup[] newArray(int size) {
//            return new ContactGroup[size];
//        }
//    };
}
