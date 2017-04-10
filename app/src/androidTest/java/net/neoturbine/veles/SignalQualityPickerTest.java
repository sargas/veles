package net.neoturbine.veles;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.neoturbine.veles.testUtils.FragmentOrViewUtilActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class SignalQualityPickerTest {
    @Rule
    public final ActivityTestRule<FragmentOrViewUtilActivity> mActivityRule =
            new ActivityTestRule<>(FragmentOrViewUtilActivity.class);
    private SignalQualityPicker mPicker;

    @Before
    public void addSignalQualityPicker() {
        mPicker = new SignalQualityPicker();
        mActivityRule.getActivity().addFragment(mPicker);
    }

    @Test
    public void testNotNull() {
        onView(withId(R.id.rst_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testSetWithPickerWithoutTone() {
        setWithPicker("2 — Barely readable, occasional words distinguishable",
                "7 — Moderately strong signals",
                "Skip");

        assertEquals("27", mPicker.getQuality());
    }

    @Test
    public void testSetWithPickerWithTone() {
        setWithPicker("5 — Perfectly readable",
                "1 — Faint signals, barely perceptible",
                "7 — Near pure tone, trace of ripple modulation");

        assertEquals("517", mPicker.getQuality());
    }

    @Test
    public void testSetWithPickerDefaults() {
        setWithPicker(null, null, null);
        assertEquals("59", mPicker.getQuality());
    }

    @Test
    public void testSetManually() {
        mPicker.setQuality("55");
        assertEquals("55", mPicker.getQuality());

        setWithPicker(null, null, null);
        assertEquals("55", mPicker.getQuality());
    }

    @Test
    public void testSetManuallyWithTone() {
        mPicker.setQuality("554");
        assertEquals("554", mPicker.getQuality());

        setWithPicker(null, null, null);
        assertEquals("554", mPicker.getQuality());
    }

    private void setWithPicker(final String readability, final String signal, final String tone) {
        onView(withId(R.id.rst_button))
                .perform(click());
        onView(withText(R.string.rst_readability_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        if (readability != null) {
            onView(withText(readability))
                    .inRoot(isDialog())
                    .perform(click());
        }
        onView(withText(R.string.rst_positive_button))
                .inRoot(isDialog())
                .perform(click());


        onView(withText(R.string.rst_signal_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        if (signal != null) {
            onView(withText(signal))
                    .inRoot(isDialog())
                    .perform(click());
        }
        onView(withText(R.string.rst_positive_button))
                .inRoot(isDialog())
                .perform(click());

        onView(withText(R.string.rst_tone_title))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        if (tone != null) {
            onView(withText(tone))
                    .inRoot(isDialog())
                    .perform(click());
        }
        onView(withText(R.string.rst_positive_button))
                .inRoot(isDialog())
                .perform(click());
    }
}
