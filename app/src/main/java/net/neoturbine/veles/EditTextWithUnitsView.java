package net.neoturbine.veles;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.databinding.InverseBindingListener;
import android.databinding.InverseBindingMethod;
import android.databinding.InverseBindingMethods;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.neoturbine.veles.databinding.EditTextWithUnitsViewBinding;

@InverseBindingMethods({
        @InverseBindingMethod(type = Spinner.class,
                attribute = "selection",
                method = "getSelectedItemPosition"
        ),
        @InverseBindingMethod(
                type = EditTextWithUnitsView.class,
                attribute = "valueAsText"
        )
})
public class EditTextWithUnitsView extends LinearLayout {
    private final String STATE_SUPER = "SUPER_STATE";
    private final String STATE_VALUE = "STATE_VALUE";

    private final ValueWithUnit mValue;
    private InverseBindingListener mCallback;

    public EditTextWithUnitsView(Context context) {
        this(context, null);
    }

    public EditTextWithUnitsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
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

    public EditTextWithUnitsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedAttrs = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.EditTextWithUnitsView, 0, 0);
        try {
            mValue = new ValueWithUnit(
                    typedAttrs.getInteger(R.styleable.EditTextWithUnitsView_default_position, 0),
                    typedAttrs.getString(R.styleable.EditTextWithUnitsView_hint),
                    typedAttrs.getTextArray(R.styleable.EditTextWithUnitsView_units)
            );
        } finally {
            typedAttrs.recycle();
        }

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        EditTextWithUnitsViewBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.edit_text_with_units_view, this, true
        );
        binding.setValue(mValue);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item,
                mValue.unitList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.unit.setAdapter(adapter);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle savedState = new Bundle();
        savedState.putParcelable(STATE_SUPER, super.onSaveInstanceState());
        savedState.putSerializable(STATE_VALUE, mValue);
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle savedState = (Bundle) state;

            ValueWithUnit oldValue = (ValueWithUnit) savedState.getSerializable(STATE_VALUE);
            assert oldValue != null;
            mValue.fromValue(oldValue);

            state = savedState.getParcelable(STATE_SUPER);
        }
        super.onRestoreInstanceState(state);
    }

    public void setValueAsText(String value) {
        if (!getValueAsText().equals(value)) {
            mValue.fromString(value);
            if (mCallback != null)
                mCallback.onChange();
        }
    }

    @SuppressWarnings("WeakerAccess")
    @VisibleForTesting
    public String getValueAsText() {
        return mValue.toString();
    }

    private void setListener(InverseBindingListener listener) {
        mCallback = listener;
    }

    @BindingAdapter(value = "valueAsTextAttrChanged")
    public static void setListeners(EditTextWithUnitsView view,
                                    final InverseBindingListener inverseBindingListener) {
        view.setListener(inverseBindingListener);
    }
}
