package net.neoturbine.veles;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.databinding.FragmentDateTimePickerBinding;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;


public class DateTimePicker extends Fragment {

    private static final String STATE_TIME = "STATE_TIME";
    private DateTime mTime;
    private CharSequence mHintText;
    private ArrayAdapter<String> mTimeZoneAdapter;
    private final List<Dialog> mDialogs = new ArrayList<>(2);
    private FragmentDateTimePickerBinding mBinding;

    public DateTimePicker() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_date_time_picker, container, true
        );

        if (savedInstanceState == null) {
            mTime = new DateTime();
        } else {
            mTime = (DateTime) savedInstanceState.getSerializable(STATE_TIME);
        }

        setupButtonListeners();
        setupTimeZoneSpinnerAndAdapter();
        updateTimes();

        mBinding.dateTimePickerTextInputLayout.setHint(mHintText);

        return mBinding.getRoot();
    }

    @UiThread
    private void addDialogAndShow(Dialog dialog) {
        mDialogs.add(dialog);
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        for (Dialog dialog : mDialogs)
            if (dialog.isShowing())
                dialog.dismiss();
    }

    @UiThread
    private void setupButtonListeners() {
        mBinding.dateTimePickerDate.setOnClickListener(view ->
                addDialogAndShow(new DatePickerDialog(
                        getActivity(),
                        (datePicker, year, month, day) -> {
                            mTime = mTime.withDate(year, month, day);
                            updateTimes();
                        },
                        mTime.getYear(),
                        mTime.getMonthOfYear(),
                        mTime.getDayOfMonth()
                ))
        );

        mBinding.dateTimePickerTime.setOnClickListener(view ->
                addDialogAndShow(new TimePickerDialog(
                        getActivity(),
                        (timePicker, hour, minute) -> {
                            mTime = mTime.withHourOfDay(hour).withMinuteOfHour(minute);
                            updateTimes();
                        },
                        mTime.getHourOfDay(),
                        mTime.getMinuteOfHour(),
                        DateFormat.is24HourFormat(getActivity())
                ))
        );
    }

    private void setupTimeZoneSpinnerAndAdapter() {
        // from http://stackoverflow.com/a/23740502/239003
        final String[] idArray = DateTimeZone.getAvailableIDs().toArray(new String[0]);
        mTimeZoneAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_spinner_dropdown_item_aligned,
                idArray);
        mBinding.dateTimePickerTimezone.setAdapter(mTimeZoneAdapter);
        mBinding.dateTimePickerTimezone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int pos, final long id) {
                mTime = mTime.withZoneRetainFields(DateTimeZone.forID(mTimeZoneAdapter.getItem(pos)));
                updateTimes();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onInflate(
            final Context context, final AttributeSet attrs, final Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DateTimePicker);

        mHintText = a.getText(R.styleable.DateTimePicker_hint);

        a.recycle();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_TIME, mTime);
    }

    void setDateTime(final DateTime newTime) {
        mTime = newTime;
        updateTimes();
    }

    DateTime getDateTime() {
        return mTime;
    }

    @UiThread
    private void updateTimes() {
        mBinding.dateTimePickerDate.setText(DateTimeFormat.longDate().print(mTime));
        mBinding.dateTimePickerTime.setText(DateTimeFormat.shortTime().print(mTime));

        // from http://stackoverflow.com/a/23740502/239003
        for (int i = 0; i < mTimeZoneAdapter.getCount(); i++) {
            String timezone = mTimeZoneAdapter.getItem(i);
            assert timezone != null;
            if (timezone.equals(mTime.getZone().getID())) {
                mBinding.dateTimePickerTimezone.setSelection(i);
            }
        }
    }
}