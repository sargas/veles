package net.neoturbine.veles.datetimepicker;

import android.support.annotation.NonNull;

import org.joda.time.DateTime;

class DateTimePickerModel {
    @NonNull
    private DateTime mTime;
    @NonNull
    private CharSequence mHintText;

    DateTimePickerModel() {
        this.mTime = new DateTime();
        this.mHintText = "";
    }

    public DateTime getTime() {
        return mTime;
    }

    CharSequence getHintText() {
        return mHintText;
    }

    void setTime(@NonNull DateTime mTime) {
        this.mTime = mTime;
    }

    void setHintText(@NonNull CharSequence mHintText) {
        this.mHintText = mHintText;
    }
}
