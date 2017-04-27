package net.neoturbine.veles.edittextwithunitsview;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ValueWithUnitTest {
    private ValueWithUnit mValue;

    @Before
    public void setUp() {
        mValue = new ValueWithUnit(
                3,
                "My hint",
                new String[] {"", "m", "km", "millimeter", "μm"}
        );
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidUnits() {
        new ValueWithUnit(0, "my hint", new String[] {"unit1", "unit2"});
    }

    @Test
    public void testToString() {
        assertEquals("", mValue.toString());

        mValue.valueNumber.set("hello");
        assertEquals("hello millimeter", mValue.toString());

        mValue.unitIdx.set(1);
        assertEquals("hello m", mValue.toString());

        mValue.unitIdx.set(0);
        assertEquals("hello", mValue.toString());
    }

    @Test
    public void fromString() {
        mValue.fromString("123m");
        assertEquals("123m", mValue.valueNumber.get());
        assertEquals(0, mValue.unitIdx.get());

        mValue.fromString("123 m");
        assertEquals("123", mValue.valueNumber.get());
        assertEquals(1, mValue.unitIdx.get());

        mValue.fromString("");
        assertEquals("", mValue.valueNumber.get());
        assertEquals(3, mValue.unitIdx.get());
    }

    @Test
    public void fromValue() {
        ValueWithUnit newValue = new ValueWithUnit(3, "My hint",
                new String[] {"", "m", "km", "millimeter", "μm"});
        newValue.valueNumber.set("123");
        newValue.unitIdx.set(2);

        mValue.fromValue(newValue);
        assertEquals("123", mValue.valueNumber.get());
        assertEquals(2, mValue.unitIdx.get());
    }

    @Test(expected=IllegalArgumentException.class)
    public void fromValueInconsistentUnit() {
        ValueWithUnit newValue = new ValueWithUnit(3, "My hint",
                new String[] {"", "millimeter", "μm"});
        newValue.valueNumber.set("123");
        newValue.unitIdx.set(2);

        mValue.fromValue(newValue);
    }

}