package net.neoturbine.veles.qso.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import net.neoturbine.veles.QSO;

import org.apache.commons.lang3.SerializationUtils;

class QSODatabaseConverter {
    static QSO qsoFromCursor(@NonNull Cursor data) {
        return new QSO(
                data.getLong(data.getColumnIndexOrThrow(QSOColumns._ID)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.MY_STATION)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.OTHER_STATION)),
                SerializationUtils.deserialize(
                        data.getBlob(data.getColumnIndexOrThrow(QSOColumns.START_TIME))),
                SerializationUtils.deserialize(
                        data.getBlob(data.getColumnIndexOrThrow(QSOColumns.END_TIME))),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.MODE)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.POWER)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.MY_QUALITY)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.OTHER_QUALITY)),
                SerializationUtils.deserialize(
                        data.getBlob(data.getColumnIndexOrThrow(QSOColumns.MY_LOCATION))),
                SerializationUtils.deserialize(
                        data.getBlob(data.getColumnIndexOrThrow(QSOColumns.OTHER_LOCATION))),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.TRANSMISSION_FREQUENCY)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.RECEIVE_FREQUENCY)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.COMMENT))
        );
    }

    public static ContentValues valuesFromQSO(@NonNull QSO qso) {
        ContentValues values = new ContentValues();
        if (qso.getID() != -1)
            values.put(QSOColumns._ID, qso.getID());
        values.put(QSOColumns.MY_STATION, qso.getMyStation());
        values.put(QSOColumns.OTHER_STATION, qso.getOtherStation());
        values.put(QSOColumns.START_TIME, SerializationUtils.serialize(qso.getStartTime()));
        values.put(QSOColumns.END_TIME, SerializationUtils.serialize(qso.getEndTime()));
        values.put(QSOColumns.MODE, qso.getMode());
        values.put(QSOColumns.POWER, qso.getPower());
        values.put(QSOColumns.MY_QUALITY, qso.getMyQuality());
        values.put(QSOColumns.OTHER_QUALITY, qso.getOtherQuality());
        values.put(QSOColumns.MY_LOCATION, SerializationUtils.serialize(qso.getMyLocation()));
        values.put(QSOColumns.OTHER_LOCATION, SerializationUtils.serialize(qso.getOtherLocation()));
        values.put(QSOColumns.TRANSMISSION_FREQUENCY, qso.getTransmissionFrequency());
        values.put(QSOColumns.RECEIVE_FREQUENCY, qso.getReceivingFrequency());
        values.put(QSOColumns.COMMENT, qso.getComment());
        return values;
    }
}
