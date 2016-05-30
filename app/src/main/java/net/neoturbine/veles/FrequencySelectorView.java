package net.neoturbine.veles;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.neoturbine.veles.databinding.FrequencySelectorViewBinding;

@InverseBindingMethods({
        @InverseBindingMethod(type = Spinner.class,
                attribute = "selection",
                method = "getSelectedItemPosition"
        )
})
public class FrequencySelectorView extends LinearLayout {
    @SuppressWarnings("WeakerAccess")
    public static class Frequency extends BaseObservable {
        public final ObservableField<String> frequencyNumber = new ObservableField<>("");
        public final ObservableField<String> frequencyNumberHint = new ObservableField<>("");
        public final ObservableInt frequencySuffixIdx = new ObservableInt(2);

    }

    private final Frequency mFrequency = new Frequency();
    private final String[] mFrequencyUnitList =
            getResources().getStringArray(R.array.frequency_units);

    public FrequencySelectorView(Context context) {
        super(context);
        init(context, null);
    }

    public FrequencySelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FrequencySelectorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    @BindingAdapter("selectionAttrChanged")
    public static void setSelectSuffixListener(final Spinner view,
                                               final InverseBindingListener suffixChange) {
        if (suffixChange == null) {
            view.setOnItemSelectedListener(null);
        } else {
            view.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(final AdapterView<?> parent,
                                           final View view, final int position, final long id) {
                    suffixChange.onChange();
                }

                @Override
                public void onNothingSelected(final AdapterView<?> parent) {
                }
            });
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedAttrs = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.FrequencySelectorView, 0, 0);
        try {
            mFrequency.frequencyNumberHint.set(
                    typedAttrs.getString(R.styleable.FrequencySelectorView_hint)
            );
        } finally {
            typedAttrs.recycle();
        }


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Spinner frequencyUnits;

        FrequencySelectorViewBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.frequency_selector_view, this, true);
        binding.setFrequency(mFrequency);
        frequencyUnits = binding.frequencyUnits;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item,
                mFrequencyUnitList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencyUnits.setAdapter(adapter);
    }

    void setFrequency(String frequency) {
        for (int i = 1; i < mFrequencyUnitList.length; i++) {
            String suffix = mFrequencyUnitList[i];
            if (frequency.endsWith(suffix)) {
                mFrequency.frequencySuffixIdx.set(i);
                mFrequency.frequencyNumber.set(frequency.replaceAll(" ?" + suffix + "?$", ""));
                return;
            }
        }
        mFrequency.frequencySuffixIdx.set(0);
        mFrequency.frequencyNumber.set(frequency);
    }

    String getFrequencyAsString() {
        if (TextUtils.isEmpty(mFrequency.frequencyNumber.get())) {
            return "";
        } else if (mFrequency.frequencySuffixIdx.get() > 0) {
            return String.format("%s %s",
                    mFrequency.frequencyNumber.get(),
                    mFrequencyUnitList[mFrequency.frequencySuffixIdx.get()]);
        } else {
            return mFrequency.frequencyNumber.get();
        }
    }
}
