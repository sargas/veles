package net.neoturbine.veles.edittextwithunitsview;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import java.io.Serializable;
import java.util.Arrays;

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

        if (unitList == null || unitList.length == 0 || !"".equals(unitList[0]))
            throw new IllegalArgumentException("Passed a list of units ("
                    + Arrays.toString(unitList) + ") not starting with an empty unit");
        this.unitList = unitList;
    }

    public String toString() {
        if ("".equals(valueNumber.get())) {
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
        if ("".equals(value)) {
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
        if (!Arrays.equals(unitList, other.unitList))
            throw new IllegalArgumentException("Passed inconsistent unit lists");

        valueNumber.set(other.valueNumber.get());
        unitIdx.set(other.unitIdx.get());
    }
}
