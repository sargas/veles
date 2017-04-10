package net.neoturbine.veles.datetimepicker;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.R;
import net.neoturbine.veles.databinding.FragmentDateTimePickerBinding;
import net.neoturbine.veles.datetimepicker.DateTimePickerContract;
import net.neoturbine.veles.datetimepicker.DateTimePickerViewModel;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;


public class DateTimePicker extends Fragment implements DateTimePickerContract.View {
    private final List<Dialog> mDialogs = new ArrayList<>(2);
    private final DateTimePickerViewModel mViewModel = new DateTimePickerViewModel();

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
                inflater, R.layout.fragment_date_time_picker, container, true
        );

        binding.setViewmodel(mViewModel);
        mViewModel.attachView(this, savedInstanceState);

        return binding.getRoot();
    }

    @UiThread
    public void addDialogAndShow(final Dialog dialog) {
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

    public DateTime getDateTime() {
        return mViewModel.getDateTime();
    }
}