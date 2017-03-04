package net.neoturbine.veles;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.text.TextUtils;

import java.io.Serializable;

@SuppressWarnings("WeakerAccess")
public class ValueWithUnit extends BaseObservable implements Serializable {
    private static final long serialVersionUID = -5491155345942093530L;
    public final ObservableField<String> valueNumber = new ObservableField<>("");
    public final ObservableField<String> valueNumberHint;
    public final ObservableInt unitIdx;
    public final CharSequence[] unitList;
    private final int mDefaultUnitPosition;

    public ValueWithUnit(int defaultPosition, String numberHint, CharSequence[] unitList) {
        unitIdx = new ObservableInt(defaultPosition);
        mDefaultUnitPosition = defaultPosition;
        valueNumberHint = new ObservableField<>(numberHint);
        this.unitList = unitList;
    }

    public String toString() {
        if (TextUtils.isEmpty(valueNumber.get())) {
            return "";
        } else if (unitIdx.get() > 0) {
            return String.format("%s %s",
                    valueNumber.get(),
                    unitList[unitIdx.get()]);
        } else {
            return valueNumber.get();
        }
    }

    public void fromString(String value) {
        if (TextUtils.isEmpty(value)) {
            unitIdx.set(mDefaultUnitPosition);
            valueNumber.set("");
            return;
        }
        for (int i = 1; i < unitList.length; i++) {
            String suffix = unitList[i].toString();
            if (value.endsWith(" " + suffix)) {
                unitIdx.set(i);
                valueNumber.set(value.replaceAll(" ?" + suffix + "$", ""));
                return;
            }
        }
        unitIdx.set(0);
        valueNumber.set(value);
    }

    public void fromValue(ValueWithUnit other) {
        valueNumber.set(other.valueNumber.get());
        unitIdx.set(other.unitIdx.get());
    }
}
