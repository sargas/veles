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
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import net.neoturbine.veles.databinding.EditTextWithUnitsViewBinding;

import java.io.Serializable;

@InverseBindingMethods({
        @InverseBindingMethod(type = Spinner.class,
                attribute = "selection",
                method = "getSelectedItemPosition"
        )
})
public class EditTextWithUnitsView extends LinearLayout {
    private final String STATE_SUPER = "SUPER_STATE";
    private final String STATE_VALUE = "STATE_VALUE";

    @SuppressWarnings("WeakerAccess")
    public static class Value extends BaseObservable implements Serializable {
        private static final long serialVersionUID = -5491155345942093530L;
        public final ObservableField<String> valueNumber = new ObservableField<>("");
        public final ObservableField<String> valueNumberHint = new ObservableField<>("");
        public final ObservableInt unitIdx = new ObservableInt();
    }

    private final Value mValue = new Value();
    private final CharSequence[] mUnitList;
    private final int mDefaultUnitPosition;

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
            mValue.valueNumberHint.set(
                    typedAttrs.getString(R.styleable.EditTextWithUnitsView_hint)
            );
            mUnitList = typedAttrs.getTextArray(R.styleable.EditTextWithUnitsView_units);
            mDefaultUnitPosition =
                    typedAttrs.getInteger(R.styleable.EditTextWithUnitsView_default_position, 0);
        } finally {
            typedAttrs.recycle();
        }

        mValue.unitIdx.set(mDefaultUnitPosition);


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        EditTextWithUnitsViewBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.edit_text_with_units_view, this, true
        );
        binding.setValue(mValue);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                getContext(), android.R.layout.simple_spinner_item,
                mUnitList);
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

            Value oldValue = (Value) savedState.getSerializable(STATE_VALUE);
            assert oldValue != null;
            mValue.valueNumber.set(oldValue.valueNumber.get());
            mValue.unitIdx.set(oldValue.unitIdx.get());

            state = savedState.getParcelable(STATE_SUPER);
        }
        super.onRestoreInstanceState(state);
    }

    void setValue(String value) {
        if (TextUtils.isEmpty(value)) {
            mValue.unitIdx.set(mDefaultUnitPosition);
            mValue.valueNumber.set("");
            return;
        }
        for (int i = 1; i < mUnitList.length; i++) {
            String suffix = mUnitList[i].toString();
            if (value.endsWith(" " + suffix)) {
                mValue.unitIdx.set(i);
                mValue.valueNumber.set(value.replaceAll(" ?" + suffix + "$", ""));
                return;
            }
        }
        mValue.unitIdx.set(0);
        mValue.valueNumber.set(value);
    }

    String getValueAsString() {
        if (TextUtils.isEmpty(mValue.valueNumber.get())) {
            return "";
        } else if (mValue.unitIdx.get() > 0) {
            return String.format("%s %s",
                    mValue.valueNumber.get(),
                    mUnitList[mValue.unitIdx.get()]);
        } else {
            return mValue.valueNumber.get();
        }
    }
}
