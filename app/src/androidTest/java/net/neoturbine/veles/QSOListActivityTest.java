package net.neoturbine.veles;


import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class QSOListActivityTest {
    @Rule
    public ActivityTestRule<QSOListActivity> mActivityRule =
            new ActivityTestRule<>(QSOListActivity.class);

    @Test
    public void QSOListActivity_empty_messages() {
        onView(isRoot()).perform(ChangeAdapterAction.emptyCursor());
        onView(withId(R.id.qso_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
    }

    @Test
    public void QSOListActivity_hide_empty_messages() {
        onView(isRoot()).perform(ChangeAdapterAction.cursorWithItems());
        onView(withId(R.id.qso_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.GONE))));
    }

    private static class ChangeAdapterAction implements ViewAction {
        final Cursor mCursor;

        ChangeAdapterAction(Cursor c) {
            mCursor = c;
        }

        @Override
        public void perform(UiController uiController, View view) {
            uiController.loopMainThreadUntilIdle();
            QSOListActivity activity = (QSOListActivity) view.getContext();
            activity.mAdapter.changeCursor(mCursor);
        }

        @Override
        public String getDescription() {
            return "Change adapter to use given cursor";
        }

        @Override
        public Matcher<View> getConstraints() {
            return isA(View.class);
        }

        static ChangeAdapterAction emptyCursor() {
            return new ChangeAdapterAction(new MatrixCursor(new String[]{"_ID"}));
        }

        static ChangeAdapterAction cursorWithItems() {
            MatrixCursor c = new MatrixCursor(new String[]{
                    QSOColumns._ID, QSOColumns.MODE, QSOColumns.START_TIME,
                    QSOColumns.TRANSMISSION_FREQUENCY, QSOColumns.OTHER_STATION
            });
            c.addRow(new Object[]{1, "FM", 1464804014L, "101.1 MHz", "WWW"});
            return new ChangeAdapterAction(c);
        }
    }
}
