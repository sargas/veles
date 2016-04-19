package net.neoturbine.veles;

import android.net.Uri;
import android.provider.BaseColumns;

class QSOColumns implements BaseColumns {
    private QSOColumns() {
    }

    static final String TABLE_NAME = "QSO";
    static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.neoturbine.veles.qso";
    static final Uri CONTENT_URI = Uri.parse("content://"
            + VelesProvider.AUTHORITY + "/" + TABLE_NAME);

    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String OTHER_STATION = "other_station";
    public static final String MODE = "mode";
    public static final String COMMENT = "comment";
}
