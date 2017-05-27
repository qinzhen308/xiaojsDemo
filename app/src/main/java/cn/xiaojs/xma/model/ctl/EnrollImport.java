package cn.xiaojs.xma.model.ctl;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Created by maxiaobao on 2017/5/25.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrollImport implements Parcelable{

    public String id;
    public String subtype;

    public EnrollImport() {

    }

    protected EnrollImport(Parcel in) {
        id = in.readString();
        subtype = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(subtype);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EnrollImport> CREATOR = new Creator<EnrollImport>() {
        @Override
        public EnrollImport createFromParcel(Parcel in) {
            return new EnrollImport(in);
        }

        @Override
        public EnrollImport[] newArray(int size) {
            return new EnrollImport[size];
        }
    };
}
