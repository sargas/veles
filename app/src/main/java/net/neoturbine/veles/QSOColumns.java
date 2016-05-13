package net.neoturbine.veles;

import android.net.Uri;
import android.provider.BaseColumns;

interface QSOColumns extends BaseColumns {
    String TABLE_NAME = "QSO";
    String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.neoturbine.veles.qso";
    String SINGLE_CONTENT_TYPE = "vnd.android.cursor.item/vnd.neoturbine.veles.qso";
    Uri CONTENT_URI = Uri.parse("content://"
            + VelesProvider.AUTHORITY + "/" + TABLE_NAME);

    String START_TIME = "start_time";
    String END_TIME = "end_time";
    String OTHER_STATION = "other_station";
    String MODE = "mode";
    String COMMENT = "comment";
}
