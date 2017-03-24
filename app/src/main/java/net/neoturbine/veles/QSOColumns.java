package net.neoturbine.veles;

import android.net.Uri;
import android.provider.BaseColumns;

interface QSOColumns extends BaseColumns {
    String TABLE_NAME = "QSO";
    String CREATE_TABLE_SQL = "CREATE  TABLE \"QSO\" (" +
            "\"_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , " +
            "\"start_time\" BLOB, \"end_time\" BLOB, " +
            "\"utc_start_time\" BLOB, " + // Used for sorting
            "\"my_station\" TEXT, " +
            "\"other_station\" TEXT, " +
            "\"mode\" TEXT, " +
            "\"tx_freq\" TEXT, " +
            "\"rx_freq\" TEXT, " +
            "\"power\" TEXT, " +
            "\"my_quality\" TEXT, " +
            "\"other_quality\" TEXT, " +
            "\"my_location\" BLOB, " +
            "\"other_location\" BLOB, " +
            "\"comment\" TEXT )";

    String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.neoturbine.veles.qso";
    String SINGLE_CONTENT_TYPE = "vnd.android.cursor.item/vnd.neoturbine.veles.qso";
    Uri CONTENT_URI = Uri.parse("content://"
            + VelesProvider.AUTHORITY + "/" + TABLE_NAME);

    String START_TIME = "start_time";
    String UTC_START_TIME = "utc_start_time";
    String END_TIME = "end_time";
    String MY_STATION = "my_station";
    String OTHER_STATION = "other_station";
    String MODE = "mode";
    String POWER = "power";
    String TRANSMISSION_FREQUENCY = "tx_freq";
    String RECEIVE_FREQUENCY = "rx_freq";
    String MY_QUALITY = "my_quality";
    String OTHER_QUALITY = "other_quality";
    String MY_LOCATION = "my_location";
    String OTHER_LOCATION = "other_location";
    String COMMENT = "comment";
}
