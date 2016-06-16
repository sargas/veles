package net.neoturbine.veles;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.lang3.SerializationUtils;

@SuppressWarnings("unused")
public final class QSO {
    @NonNull
    private final String mOtherStation;
    private final long mStartTime;
    private final long mEndTime;
    @NonNull
    private final String mMode;
    @NonNull
    private final String mPower;
    @NonNull
    private final String mTxFrequency;
    @NonNull
    private final String mRxFrequency;
    @Nullable
    private final VelesLocation mMyLocation;
    @Nullable
    private final VelesLocation mOtherLocation;
    @NonNull
    private final String mComment;

    private QSO(@NonNull String otherStation,
                long startTime, long endTime, @NonNull String mode, @NonNull String power,
                @Nullable VelesLocation myLocation, @Nullable VelesLocation otherLocation,
                @NonNull String txFrequency,
                @NonNull String rxFrequency, @NonNull String comment) {
        this.mOtherStation = otherStation;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mMode = mode;
        this.mPower = power;
        this.mMyLocation = myLocation;
        this.mOtherLocation = otherLocation;
        this.mTxFrequency = txFrequency;
        this.mRxFrequency = rxFrequency;
        this.mComment = comment;
    }

    QSO(@NonNull Cursor data) {
        this(
                data.getString(data.getColumnIndexOrThrow(QSOColumns.OTHER_STATION)),
                data.getLong(data.getColumnIndexOrThrow(QSOColumns.START_TIME)),
                data.getLong(data.getColumnIndexOrThrow(QSOColumns.END_TIME)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.MODE)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.POWER)),
                SerializationUtils.<VelesLocation>deserialize(
                        data.getBlob(data.getColumnIndexOrThrow(QSOColumns.MY_LOCATION))),
                SerializationUtils.<VelesLocation>deserialize(
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

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
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
}
