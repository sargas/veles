package net.neoturbine.veles.datetimepicker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import net.neoturbine.veles.BR;
import net.neoturbine.veles.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

@SuppressWarnings("WeakerAccess")
public class DateTimePickerViewModel extends BaseObservable {
    private static final String STATE_TIME = "STATE_TIME";
    @NonNull
    private final DateTimePickerModel mModel = new DateTimePickerModel();
    private ArrayAdapter<String> mZoneAdapter;
    private DateTimePickerContract.View mView;

    void attachView(DateTimePickerContract.View view, Bundle bundle) {
        mView = view;
        if (bundle != null) onRestoreInstanceState(bundle);
    }

    void setDateTime(DateTime dateTime) {
        mModel.setTime(dateTime);
        notifyPropertyChanged(BR.time);
        notifyPropertyChanged(BR.date);
        notifyPropertyChanged(BR.selectedTimeZoneIndex);
    }

    DateTime getDateTime() {
        return mModel.getTime();
    }

    @Bindable
    public String getDate() {
        return DateTimeFormat.longDate().print(mModel.getTime());
    }

    @Bindable
    public String getTime() {
        return DateTimeFormat.shortTime().print(mModel.getTime());
    }

    @Bindable
    public CharSequence getHint() {
        return mModel.getHintText();
    }

    public void setHint(CharSequence hint) {
        mModel.setHintText(hint);
        notifyPropertyChanged(BR.hint);
    }

    @SuppressWarnings("WeakerAccess")
    @Bindable
    public SpinnerAdapter getAdapter() {
        fillZoneAdapterIfNull(mView.getContext());
        return mZoneAdapter;
    }

    private void fillZoneAdapterIfNull(Context context) {
        if (mZoneAdapter == null) {
            synchronized (DateTimePickerViewModel.class) {
                if (mZoneAdapter == null) {
                    fillZoneAdapter(context);
                    notifyPropertyChanged(BR.selectedTimeZoneIndex);
                }
            }
        }
    }

    private void fillZoneAdapter(Context context) {
        // partially from http://stackoverflow.com/a/23740502/239003
        final String[] idArray = DateTimeZone.getAvailableIDs().toArray(new String[0]);
        mZoneAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_dropdown_item_aligned,
                idArray);
    }

    @Bindable
    public int getSelectedTimeZoneIndex() {
        // partially from http://stackoverflow.com/a/23740502/239003
        for (int i = 0; i < getAdapter().getCount(); i++) {
            String timezone = mZoneAdapter.getItem(i);
            assert timezone != null;

            if (timezone.equals(mModel.getTime().getZone().getID())) {
                return i;
            }
        }
        throw new RuntimeException("Unknown time zone");
    }

    public void setSelectedTimeZoneIndex(int index) {
        setDateTime(
                mModel.getTime().withZoneRetainFields(
                        DateTimeZone.forID(mZoneAdapter.getItem(index)))
        );
        notifyPropertyChanged(BR.selectedTimeZoneIndex);
    }

    public void dateButtonOnClick(Context context) {
        mView.addDialogAndShow(new DatePickerDialog(
                context,
                (datePicker, year, month, day) ->
                    setDateTime(
                            mModel.getTime().withDate(year, month, day))
                ,
                mModel.getTime().getYear(),
                mModel.getTime().getMonthOfYear(),
                mModel.getTime().getDayOfMonth()
        ));
    }

    public void timeButtonOnClick(Context context) {
        mView.addDialogAndShow(new TimePickerDialog(
                context,
                (timePicker, hour, minute) ->
                    setDateTime(
                            mModel.getTime().withHourOfDay(hour).withMinuteOfHour(minute))
                ,
                mModel.getTime().getHourOfDay(),
                mModel.getTime().getMinuteOfHour(),
                DateFormat.is24HourFormat(context)
        ));
    }

    private void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null)
            setDateTime((DateTime) savedInstanceState.getSerializable(STATE_TIME));
    }

    void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable(STATE_TIME, getDateTime());
    }
}
