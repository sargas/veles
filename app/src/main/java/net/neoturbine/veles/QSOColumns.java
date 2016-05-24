package net.neoturbine.veles;

import android.net.Uri;
import android.provider.BaseColumns;

interface QSOColumns extends BaseColumns {
    String TABLE_NAME = "QSO";
    String CREATE_TABLE_SQL = "CREATE  TABLE \"QSO\" (" +
            "\"_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , " +
            "\"start_time\" DATETIME, \"end_time\" DATETIME, " +
            "\"other_station\" TEXT, " +
            "\"mode\" TEXT, " +
            "\"tx_freq\" TEXT, " +
            "\"rx_freq\" TEXT, " +
            "\"power\" TEXT, " +
            "\"my_qth\" TEXT, " +
            "\"my_locator\" TEXT, " +
            "\"other_qth\" TEXT, " +
            "\"other_locator\" TEXT, " +
            "\"comment\" TEXT )";

    String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.neoturbine.veles.qso";
    String SINGLE_CONTENT_TYPE = "vnd.android.cursor.item/vnd.neoturbine.veles.qso";
    Uri CONTENT_URI = Uri.parse("content://"
            + VelesProvider.AUTHORITY + "/" + TABLE_NAME);

    String START_TIME = "start_time";
    String END_TIME = "end_time";
    String OTHER_STATION = "other_station";
    String MODE = "mode";
    String TRANSMISSION_FREQUENCY = "tx_freq";
    String RECEIVE_FREQUENCY = "rx_freq";
    String COMMENT = "comment";
}
