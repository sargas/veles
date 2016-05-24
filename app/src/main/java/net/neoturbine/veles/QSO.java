package net.neoturbine.veles;

import android.database.Cursor;

import com.google.repacked.antlr.v4.runtime.misc.NotNull;

@SuppressWarnings("unused")
public final class QSO {
    @NotNull
    private final String mOtherStation;
    private final long mStartTime;
    private final long mEndTime;
    @NotNull
    private final String mMode;
    @NotNull
    private final String mComment;

    private QSO(@NotNull String otherStation,
                long startTime, long endTime, @NotNull String mode, @NotNull String comment) {
        this.mOtherStation = otherStation;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mMode = mode;
        this.mComment = comment;
    }

    public QSO(Cursor data) {
        this(
                data.getString(data.getColumnIndexOrThrow(QSOColumns.OTHER_STATION)),
                data.getLong(data.getColumnIndexOrThrow(QSOColumns.START_TIME)),
                data.getLong(data.getColumnIndexOrThrow(QSOColumns.END_TIME)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.MODE)),
                data.getString(data.getColumnIndexOrThrow(QSOColumns.COMMENT))
        );
    }

    public String getOtherStation() {
        return mOtherStation;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public String getMode() {
        return mMode;
    }

    public String getComment() {
        return mComment;
    }
}
