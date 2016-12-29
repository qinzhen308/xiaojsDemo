package cn.xiaojs.xma.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.xiaojs.xma.model.social.DynPost;

/**
 * Created by maxiaobao on 2016/11/13.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Doc implements Parcelable{

    public String id;
    public String subtype;
    public String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(id);
        dest.writeString(subtype);
    }

    public Doc() {

    }

    private Doc(Parcel in) {
        id = in.readString();
        subtype = in.readString();
    }


    public static final Parcelable.Creator<Doc> CREATOR
            = new Parcelable.Creator<Doc>() {
        public Doc createFromParcel(Parcel in) {

            return new Doc(in);
        }

        public Doc[] newArray(int size) {
            return new Doc[size];
        }
    };
}
