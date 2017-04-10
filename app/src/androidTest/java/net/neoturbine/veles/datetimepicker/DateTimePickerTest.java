package net.neoturbine.veles.datetimepicker;

import android.support.annotation.IdRes;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.R;
import net.neoturbine.veles.testUtils.FragmentOrViewUtilActivity;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
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
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DateTimePickerTest {
    @Rule
    public final ActivityTestRule<FragmentOrViewUtilActivity> mActivityRule =
            new ActivityTestRule<>(FragmentOrViewUtilActivity.class);
    private DateTimePicker mPicker;
    private DateTime mTestDateTime;

    @Before
    public void addDateTimePicker() {
        mPicker = new DateTimePicker();
        mActivityRule.getActivity().addFragment(mPicker);
    }

    @Before
    public void initTestDateTime() {
        JodaTimeAndroid.init(mActivityRule.getActivity());
        mTestDateTime = new DateTime(2004, 10, 3, 12, 30,
                DateTimeZone.forID("America/New_York"));
    }

    @Test
    public void testAllFieldsExist() {
        for (@IdRes int id : new int[]{R.id.date_time_picker_date,
                R.id.date_time_picker_time, R.id.date_time_picker_timezone})
            onView(withId(id)).check(matches(isDisplayed()));
    }

    @Test
    public void testAllFieldsShowCorrectInfo() {
        mPicker.setDateTime(mTestDateTime);

        onView(withId(R.id.date_time_picker_date)).check(matches(withText("October 3, 2004")));
        onView(withId(R.id.date_time_picker_time)).check(matches(withText("12:30 PM")));
        onView(withId(R.id.date_time_picker_timezone)).check(matches(withSpinnerText("America/New_York")));
    }

    @Test
    public void testPickersDefaultToCurrentInfo() {
        mPicker.setDateTime(mTestDateTime);

        onView(withId(R.id.date_time_picker_date)).perform(click());
        onView(withId(android.R.id.button1))
                .inRoot(isDialog())
                .perform(click());

        onView(withId(R.id.date_time_picker_time)).perform(click());
        onView(withId(android.R.id.button1))
                .inRoot(isDialog())
                .perform(click());

        assertEquals(mTestDateTime, mPicker.getDateTime());
    }
}
