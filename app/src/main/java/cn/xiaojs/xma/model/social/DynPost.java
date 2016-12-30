package cn.xiaojs.xma.model.social;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.LiveLesson;

/**
 * Created by maxiaobao on 2016/12/27.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynPost {
    public String text;
    public String drawing;
    public Audience audience;
    public List<String> mentioned;



    @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Audience implements Parcelable{
        public int type;
        public ArrayList<Doc> chosen;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {

            dest.writeInt(type);
            dest.writeList(chosen);
            //dest.writeTypedArray(chosen,0);

        }

        public Audience() {

        }

        private Audience(Parcel in) {
            type = in.readInt();
            chosen = in.readArrayList(Doc.class.getClassLoader());
            //chosen = in.createTypedArray(Doc.CREATOR);
        }

        public static final Parcelable.Creator<Audience> CREATOR
                = new Parcelable.Creator<Audience>() {
            public Audience createFromParcel(Parcel in) {
                return new Audience(in);
            }

            public Audience[] newArray(int size) {
                return new Audience[size];
            }
        };
    }
}
