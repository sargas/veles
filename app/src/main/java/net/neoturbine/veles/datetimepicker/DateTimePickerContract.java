package net.neoturbine.veles.datetimepicker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.widget.SpinnerAdapter;

import org.joda.time.DateTime;

@SuppressWarnings("WeakerAccess")
final public class DateTimePickerContract {
    private DateTimePickerContract() {}

    interface View {
        void showDatePickerDialog(DatePickerDialog.OnDateSetListener callback, int year, int month, int day);
        void showTimePickerDialog(TimePickerDialog.OnTimeSetListener callback, int hour, int minute);
        Context getContext();
    }

    abstract public static class ViewModel extends BaseObservable {
        abstract void attachView(DateTimePickerContract.View view, Bundle bundle);
        abstract void setDateTime(DateTime dateTime);
        abstract DateTime getDateTime();
        @Bindable
        abstract public String getDate();
        @Bindable
        abstract public String getTime();
        @Bindable
        abstract public CharSequence getHint();
        @Bindable
        abstract public void setHint(CharSequence hint);
        @Bindable
        abstract public SpinnerAdapter getAdapter();
        @Bindable
        abstract public int getSelectedTimeZoneIndex();
        abstract public void setSelectedTimeZoneIndex(int index);
        abstract public void dateButtonOnClick();
        abstract public void timeButtonOnClick();
        abstract void onSaveInstanceState(final Bundle outState);
    }
}
