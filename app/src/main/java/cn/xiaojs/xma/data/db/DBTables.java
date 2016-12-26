package cn.xiaojs.xma.data.db;

import android.provider.BaseColumns;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class DBTables {

    public static final class TContact implements BaseColumns {
        //Table name
        public static final String TABLE_NAME = "contact";

        ////////////////////////////////////////////////////////////////////////////////////////////
        //Table columns
        //

        //Update log:
        //*Version: 1
        //*Init columns: name, avator

        //contact name
        public static final String NAME = "name";
        //contact avator's url
        public static final String AVATOR = "avator";

    }
}
