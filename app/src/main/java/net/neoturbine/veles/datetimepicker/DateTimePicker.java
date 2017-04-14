package net.neoturbine.veles.datetimepicker;

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

import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.R;
import net.neoturbine.veles.databinding.FragmentDateTimePickerBinding;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


public class DateTimePicker extends Fragment implements DateTimePickerContract.View {
    private final List<Dialog> mDialogs = new ArrayList<>(2);
    private final DateTimePickerContract.ViewModel mViewModel =
            DaggerDateTimePickerComponent.create().getViewModel();

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
        FragmentDateTimePickerBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_date_time_picker, container, false
        );

        binding.setViewmodel(mViewModel);
        mViewModel.attachView(this, savedInstanceState);

        return binding.getRoot();
    }

    @UiThread
    private void addDialogAndShow(final Dialog dialog) {
        mDialogs.add(dialog);
        dialog.setOnDismissListener((d) -> mDialogs.remove(dialog));
        dialog.setOnCancelListener((d) -> mDialogs.remove(dialog));
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        for (Dialog dialog : mDialogs)
            if (dialog.isShowing())
                dialog.dismiss();
    }

    @Override
    public void onInflate(
            final Context context, final AttributeSet attrs, final Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DateTimePicker);

        try {
            mViewModel.setHint(a.getText(R.styleable.DateTimePicker_hint));
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewModel.onSaveInstanceState(outState);
    }

    public void setDateTime(final DateTime newTime) {
        mViewModel.setDateTime(newTime);
    }

    @Override
    public void showTimePickerDialog(TimePickerDialog.OnTimeSetListener callback, int hour, int minute) {
        addDialogAndShow(new TimePickerDialog(
                getContext(),
                callback,
                hour,
                minute,
                DateFormat.is24HourFormat(getContext())
        ));
    }

    @Override
    public void showDatePickerDialog(DatePickerDialog.OnDateSetListener callback,
                                     int year, int month, int day) {
        addDialogAndShow(new DatePickerDialog(
                getContext(),
                callback,
                year,
                month,
                day
        ));
    }

    public DateTime getDateTime() {
        return mViewModel.getDateTime();
    }
}