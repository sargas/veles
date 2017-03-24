package net.neoturbine.veles;


import android.content.ComponentName;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.apache.commons.lang3.SerializationUtils;
import org.hamcrest.Matcher;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasClassName;
import static android.support.test.espresso.intent.matcher.ComponentNameMatchers.hasPackageName;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class QSOListActivityTest {
    @Rule
    public final ActivityTestRule<QSOListActivity> mActivityRule =
            new IntentsTestRule<>(QSOListActivity.class);

    @Test
    public void QSOListActivity_empty_messages() {
        onView(isRoot()).perform(ChangeAdapterAction.emptyCursor(mActivityRule.getActivity()));
        onView(withId(R.id.qso_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
    }

    @Test
    public void QSOListActivity_hide_empty_messages() {
        onView(isRoot()).perform(ChangeAdapterAction.cursorWithItems(mActivityRule.getActivity()));
        onView(withId(R.id.qso_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
    }

    @Test
    public void QSOListActivity_fab_new_qso() {
        onView(withId(R.id.fab))
                .perform(click());

        if (mActivityRule.getActivity().findViewById(R.id.qso_detail_container) != null) {
            onView(withId(R.id.qso_station)).check(matches(isDisplayed()));
        } else {
            intended(allOf(hasComponent(
                    new ComponentName(mActivityRule.getActivity().getPackageName(),
                            QSOEditActivity.class.getName())),
                    not(hasExtras(hasEntry(equalTo(QSOEditActivity.ARG_QSO_ID), anything())))
            ));
        }
    }

    @Test
    public void QSOListActivity_new_qso_link() {
        onView(isRoot())
                .perform(ChangeAdapterAction.emptyCursor(mActivityRule.getActivity()));
        onView(withId(R.id.empty_list_link))
                .perform(click());

        if (mActivityRule.getActivity().findViewById(R.id.qso_detail_container) != null) {
            onView(withId(R.id.qso_station)).check(matches(isDisplayed()));
        } else {
            intended(allOf(hasComponent(
                    new ComponentName(mActivityRule.getActivity().getPackageName(),
                            QSOEditActivity.class.getName())),
                    not(hasExtras(hasEntry(equalTo(QSOEditActivity.ARG_QSO_ID), anything())))
            ));
        }
    }

    @Test
    public void QSOListActivity_open_qso_activity() {
        if (mActivityRule.getActivity().findViewById(R.id.qso_detail_container) != null)
            return; // skip on big screens

        onView(isRoot()).perform(ChangeAdapterAction.cursorWithItems(mActivityRule.getActivity()));
        onView(withId(R.id.qso_list)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(allOf(
                hasComponent(
                        allOf(hasClassName(QSODetailActivity.class.getName()),
                                hasPackageName(mActivityRule.getActivity().getPackageName()))
                ),
                hasExtra(QSODetailActivity.ARG_QSO_ID, ChangeAdapterAction.ID_OF_ITEM)
        ));
    }

    private static class ChangeAdapterAction implements ViewAction {
        final Cursor mCursor;
        final QSOListActivity mActivity;
        static final long ID_OF_ITEM = 1234L;

        ChangeAdapterAction(Cursor c, QSOListActivity activity) {
            mCursor = c;
            mActivity = activity;
        }

        @Override
        public void perform(UiController uiController, View view) {
            mActivity.mAdapter.changeCursor(mCursor);
        }

        @Override
        public String getDescription() {
            return "Change adapter to use given cursor";
        }

        @Override
        public Matcher<View> getConstraints() {
            return isA(View.class);
        }

        static ChangeAdapterAction emptyCursor(QSOListActivity activity) {
            return new ChangeAdapterAction(new MatrixCursor(new String[]{"_ID"}), activity);
        }

        static ChangeAdapterAction cursorWithItems(QSOListActivity activity) {
            MatrixCursor c = new MatrixCursor(new String[]{
                    QSOColumns._ID, QSOColumns.MODE, QSOColumns.START_TIME, QSOColumns.UTC_START_TIME,
                    QSOColumns.TRANSMISSION_FREQUENCY, QSOColumns.OTHER_STATION
            });
            c.addRow(new Object[]{ID_OF_ITEM, "FM", SerializationUtils.serialize(DateTime.now()),
                    1464804014L, "101.1 MHz", "WWW"});
            return new ChangeAdapterAction(c, activity);
        }
    }
}
