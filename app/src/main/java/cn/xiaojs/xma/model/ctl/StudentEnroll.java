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
public class StudentEnroll  implements Parcelable{

    public String id;
    public long mobile;
    public String name;

    public StudentEnroll() {

    }

    protected StudentEnroll(Parcel in) {
        id = in.readString();
        mobile = in.readLong();
        name = in.readString();
    }

    public static final Creator<StudentEnroll> CREATOR = new Creator<StudentEnroll>() {
        @Override
        public StudentEnroll createFromParcel(Parcel in) {
            return new StudentEnroll(in);
        }

        @Override
        public StudentEnroll[] newArray(int size) {
            return new StudentEnroll[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(mobile);
        dest.writeString(name);
    }
}
