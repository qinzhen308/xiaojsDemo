package cn.xiaojs.xma.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2016/10/31.
 */

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Upgrade implements Parcelable{

    public int app;
    public int verNum;
    public String verStr;
    public String uri;
    public String remarks;
    public int action;

    public Upgrade() {

    }

    protected Upgrade(Parcel in) {
        app = in.readInt();
        verNum = in.readInt();
        verStr = in.readString();
        uri = in.readString();
        remarks = in.readString();
        action = in.readInt();
    }

    public static final Creator<Upgrade> CREATOR = new Creator<Upgrade>() {
        @Override
        public Upgrade createFromParcel(Parcel in) {
            return new Upgrade(in);
        }

        @Override
        public Upgrade[] newArray(int size) {
            return new Upgrade[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(app);
        dest.writeInt(verNum);
        dest.writeString(verStr);
        dest.writeString(uri);
        dest.writeString(remarks);
        dest.writeInt(action);
    }
}
