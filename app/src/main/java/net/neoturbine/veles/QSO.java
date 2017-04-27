package net.neoturbine.veles;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.neoturbine.veles.qso.model.VelesLocation;

import org.joda.time.DateTime;

public final class QSO {
    private final long mID;
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

    public QSO(long id, @NonNull String myStation, @NonNull String otherStation,
                @NonNull DateTime startTime, @NonNull DateTime endTime, @NonNull String mode,
                @NonNull String power, @NonNull String myQuality, @NonNull String otherQuality,
                @Nullable VelesLocation myLocation, @Nullable VelesLocation otherLocation,
                @NonNull String txFrequency,
                @NonNull String rxFrequency, @NonNull String comment) {
        mID = id;
        mMyStation = myStation;
        mOtherStation = otherStation;
        mStartTime = startTime;
        mEndTime = endTime;
        mMode = mode;
        mPower = power;
        mMyQuality = myQuality;
        mOtherQuality = otherQuality;
        mMyLocation = myLocation;
        mOtherLocation = otherLocation;
        mTxFrequency = txFrequency;
        mRxFrequency = rxFrequency;
        mComment = comment;
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

    public long getID() {
        return mID;
    }

    @Override
    public String toString() {
        return "QSO{" +
                "mMyStation='" + mMyStation + '\'' +
                ", mOtherStation='" + mOtherStation + '\'' +
                ", mStartTime=" + mStartTime +
                ", mEndTime=" + mEndTime +
                ", mMode='" + mMode + '\'' +
                ", mPower='" + mPower + '\'' +
                ", mTxFrequency='" + mTxFrequency + '\'' +
                ", mRxFrequency='" + mRxFrequency + '\'' +
                ", mMyQuality='" + mMyQuality + '\'' +
                ", mOtherQuality='" + mOtherQuality + '\'' +
                ", mMyLocation=" + mMyLocation +
                ", mOtherLocation=" + mOtherLocation +
                ", mComment='" + mComment + '\'' +
                '}';
    }
}
