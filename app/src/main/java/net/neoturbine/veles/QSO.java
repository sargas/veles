package net.neoturbine.veles;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.neoturbine.veles.qso.model.VelesLocation;

import org.apache.commons.lang3.SerializationUtils;
import org.joda.time.DateTime;

@SuppressWarnings("unused")
public final class QSO {
    @NonNull
    private final String mMyStation;
    @NonNull
    private final String mOtherStation;
    @NonNull
    private final DateTime mStartTime;
    @NonNull
    private final DateTime mEndTime;
    @NonNull
    private final String mMode;
    @NonNull
    private final String mPower;
    @NonNull
    private final String mTxFrequency;
    @NonNull
    private final String mRxFrequency;
    @NonNull
    private final String mMyQuality;
    @NonNull
    private final String mOtherQuality;
    @Nullable
    private final VelesLocation mMyLocation;
    @Nullable
    private final VelesLocation mOtherLocation;
    @NonNull
    private final String mComment;

    private QSO(@NonNull String myStation, @NonNull String otherStation,
                @NonNull DateTime startTime, @NonNull DateTime endTime, @NonNull String mode,
                @NonNull String power, @NonNull String myQuality, @NonNull String otherQuality,
                @Nullable VelesLocation myLocation, @Nullable VelesLocation otherLocation,
                @NonNull String txFrequency,
                @NonNull String rxFrequency, @NonNull String comment) {
        this.mMyStation = myStation;
        this.mOtherStation = otherStation;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mMode = mode;
        this.mPower = power;
        this.mMyQuality = myQuality;
        this.mOtherQuality = otherQuality;
        this.mMyLocation = myLocation;
        this.mOtherLocation = otherLocation;
        this.mTxFrequency = txFrequency;
        this.mRxFrequency = rxFrequency;
        this.mComment = comment;
    }

    public QSO(@NonNull Cursor data) {
        this(
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

    @Nullable
    public VelesLocation getMyLocation() {
        return mMyLocation;
    }

    @Nullable
    public VelesLocation getOtherLocation() {
        return mOtherLocation;
    }

    @NonNull
    public String getPower() {
        return mPower;
    }

    @NonNull
    public String getOtherStation() {
        return mOtherStation;
    }

    @NonNull
    public DateTime getStartTime() {
        return mStartTime;
    }

    @NonNull
    public DateTime getEndTime() {
        return mEndTime;
    }

    @NonNull
    public String getMode() {
        return mMode;
    }

    @NonNull
    public String getTransmissionFrequency() {
        return mTxFrequency;
    }

    @NonNull
    public String getReceivingFrequency() {
        return mRxFrequency;
    }

    @NonNull
    public String getComment() {
        return mComment;
    }

    @NonNull
    public String getMyStation() {
        return mMyStation;
    }

    @NonNull
    public String getMyQuality() {
        return mMyQuality;
    }

    @NonNull
    public String getOtherQuality() {
        return mOtherQuality;
    }
}
