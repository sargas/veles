package net.neoturbine.veles;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import net.neoturbine.veles.databinding.FragmentDateTimePickerBinding;

import java.util.Calendar;

public class DateTimePicker extends Fragment {

    private Calendar mCalendar;
    private TextView mTimeButton;
    private TextView mDateButton;
    private CharSequence mHintText;

    public DateTimePicker() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentDateTimePickerBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_date_time_picker, container, true
        );

        mTimeButton = binding.dateTimePickerTime;
        mDateButton = binding.dateTimePickerDate;
        mCalendar = Calendar.getInstance();

        bindButtons();
        binding.dateTimePickerTextInputLayout.setHint(mHintText);

        updateTimes();

        return binding.getRoot();
    }

    @UiThread
    private void bindButtons() {
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                mCalendar.set(year, month, day);
                                updateTimes();
                            }
                        },
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                                mCalendar.set(Calendar.MINUTE, minute);
                                updateTimes();
                            }
                        },
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(getActivity())
                ).show();
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

    void setTimeInMillis(long timeInMillis) {
        mCalendar.setTimeInMillis(timeInMillis);
        updateTimes();
    }

    long getTimeInMillis() {
        return mCalendar.getTimeInMillis();
    }

    @UiThread
    private void updateTimes() {
        mDateButton.setText(
                DateFormat.getLongDateFormat(getActivity()).format(mCalendar.getTime()));
        mTimeButton.setText(
                DateFormat.getTimeFormat(getActivity()).format(mCalendar.getTime()));
    }
}
