package com.kwonterry.fliptimer.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Terry Kwon on 12/31/2015.
 */
public class TimeContract {
    public static final String CONTENT_AUTHORITY = "com.kwonterry.fliptimer";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TIME = "time";

    public TimeContract() {
        //empty constructor
    }

    public static abstract class TimeEntry implements BaseColumns {
        public static final String TABLE_NAME = "time";
        public static final String COLUMN_TIME = "time_milliseconds";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_WORKTIME = "worktime";
    }

}
