package net.neoturbine.veles;


import android.content.ComponentName;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import net.neoturbine.veles.qso.data.FakeDataRepository;
import net.neoturbine.veles.qso.detail.QSODetailActivity;
import net.neoturbine.veles.qso.list.QSOListActivity;
import net.neoturbine.veles.qso.model.QSOBuilder;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

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
        onView(isRoot()).perform(ChangeDataAction.emptyData());
        onView(withId(R.id.qso_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
        onView(withId(R.id.qso_list))
                .check(matches(not(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))));
    }

    @Test
    public void QSOListActivity_hide_empty_messages() {
        onView(isRoot()).perform(ChangeDataAction.dataWithOneItem());
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
                .perform(ChangeDataAction.emptyData());
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

        onView(isRoot()).perform(ChangeDataAction.dataWithOneItem());
        onView(withId(R.id.qso_list)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(allOf(
                hasComponent(
                        allOf(hasClassName(QSODetailActivity.class.getName()),
                                hasPackageName(mActivityRule.getActivity().getPackageName()))
                ),
                hasExtra(QSODetailActivity.ARG_QSO_ID, ChangeDataAction.ID_OF_ITEM)
        ));
    }

    private static class ChangeDataAction implements ViewAction {
        private final List<QSO> mList;
        final FakeDataRepository dataRepository = new FakeDataRepository();
        static final long ID_OF_ITEM = 1234L;

        ChangeDataAction(List<QSO> list) {
            mList = list;
        }

        @Override
        public void perform(UiController uiController, View view) {
            dataRepository.setQSOs(mList);
        }

        @Override
        public String getDescription() {
            return "Change data repository to use given list of QSOs";
        }

        @Override
        public Matcher<View> getConstraints() {
            return isA(View.class);
        }

        static ChangeDataAction emptyData() {
            return new ChangeDataAction(Collections.emptyList());
        }

        static ChangeDataAction dataWithOneItem() {
            return new ChangeDataAction(Collections.singletonList(new QSOBuilder()
                    .setId(ID_OF_ITEM)
                    .setMode("FM")
                    .setTxFrequency("101.1 MHz")
                    .setOtherStation("WWW")
                    .createQSO()));
        }
    }
}
