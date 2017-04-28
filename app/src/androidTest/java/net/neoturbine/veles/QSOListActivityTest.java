package net.neoturbine.veles;


import android.content.ComponentName;
import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.neoturbine.veles.qso.data.AddDataAction;
import net.neoturbine.veles.qso.data.DeleteDataAction;
import net.neoturbine.veles.qso.data.ReplaceDataAction;
import net.neoturbine.veles.qso.data.UpdateDataAction;
import net.neoturbine.veles.qso.detail.QSODetailActivity;
import net.neoturbine.veles.qso.edit.QSOEditActivity;
import net.neoturbine.veles.qso.list.QSOListActivity;
import net.neoturbine.veles.qso.model.QSOBuilder;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
public class QSOListActivityTest {
    @Rule
    public final ActivityTestRule<QSOListActivity> mActivityRule =
            new IntentsTestRule<>(QSOListActivity.class);
    private ComponentName mQSOEditActivityComponentName;
    private ComponentName mQSODetailActivityComponentName;

    @Before
    public void setConstants() {
        String packageName = mActivityRule.getActivity().getPackageName();
        mQSOEditActivityComponentName = new ComponentName(packageName,
                QSOEditActivity.class.getName());
        mQSODetailActivityComponentName = new ComponentName(packageName,
                QSODetailActivity.class.getName());
    }

    @Test
    public void show_empty_messages() {
        onView(isRoot()).perform(ReplaceDataAction.emptyData());
        onView(withId(R.id.qso_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.empty_list_message))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.empty_list_link))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @Test
    public void hide_empty_messages() {
        onView(isRoot()).perform(ReplaceDataAction.dataWithOneItem());
        onView(withId(R.id.qso_list))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.empty_list_message))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.empty_list_link))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void fab_new_qso() {
        onView(withId(R.id.fab))
                .perform(click());

        if (isWideActivity()) {
            onView(withId(R.id.qso_station)).check(matches(isDisplayed()));
        } else {
            intended(allOf(
                    hasComponent(mQSOEditActivityComponentName),
                    not(hasQSOEditID(anything()))
            ));
        }
    }

    private Matcher<Intent> hasQSOEditID(Matcher<Object> id) {
        return hasExtras(hasEntry(equalTo(QSOEditActivity.ARG_QSO_ID), id));
    }

    @Test
    public void new_qso_link() {
        onView(isRoot())
                .perform(ReplaceDataAction.emptyData());
        onView(withId(R.id.empty_list_link))
                .perform(click());

        if (isWideActivity()) {
            onView(withId(R.id.qso_station)).check(matches(isDisplayed()));
        } else {
            intended(allOf(
                    hasComponent(mQSOEditActivityComponentName),
                    not(hasQSOEditID(anything()))
            ));
        }
    }

    private boolean isWideActivity() {
        return mActivityRule.getActivity().findViewById(R.id.qso_detail_container) != null;
    }

    @Test
    public void open_qso_activity() {
        onView(isRoot()).perform(ReplaceDataAction.dataWithOneItem());
        onView(withId(R.id.qso_list)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        if (isWideActivity()) {
            onView(withText(ReplaceDataAction.firstQSO().getOtherStation())).check(matches(isDisplayed()));
        } else {
            intended(allOf(
                    hasComponent(mQSODetailActivityComponentName),
                    hasExtra(QSODetailActivity.ARG_QSO_ID, ReplaceDataAction.firstQSO().getID())
            ));
        }
    }

    @Test
    public void deleting_qso_updates_list() {
        final QSO firstQSO = ReplaceDataAction.firstQSO();
        final QSO secondQSO = ReplaceDataAction.secondQSO();

        onView(isRoot())
                .perform(ReplaceDataAction.dataWithTwoItems());

        onView(withText(firstQSO.getOtherStation())).check(matches(isDisplayed()));
        onView(withText(secondQSO.getOtherStation())).check(matches(isDisplayed()));

        onView(isRoot())
                .perform(DeleteDataAction.deleteID(firstQSO.getID()));

        onView(withText(secondQSO.getOtherStation())).check(matches(isDisplayed()));
        onView(withText(firstQSO.getOtherStation())).check(doesNotExist());
    }

    @Test
    public void adding_qso_updates_list() {
        final QSO firstQSO = ReplaceDataAction.firstQSO();
        final QSO secondQSO = ReplaceDataAction.secondQSO();

        onView(isRoot())
                .perform(ReplaceDataAction.dataWithOneItem());

        onView(withText(firstQSO.getOtherStation())).check(matches(isDisplayed()));
        onView(withText(secondQSO.getOtherStation())).check(doesNotExist());

        onView(isRoot())
                .perform(AddDataAction.addQSO(secondQSO));

        onView(withText(firstQSO.getOtherStation())).check(matches(isDisplayed()));
        onView(withText(secondQSO.getOtherStation())).check(matches(isDisplayed()));
    }

    @Test
    public void editing_qso_updates_list() {
        QSO qso = ReplaceDataAction.firstQSO();
        String newStation = "ABC";
        QSO newQSO = QSOBuilder.fromQSO(qso).setOtherStation(newStation).createQSO();

        onView(isRoot())
                .perform(ReplaceDataAction.dataWithOneItem());

        onView(withText(qso.getOtherStation())).check(matches(isDisplayed()));

        onView(isRoot())
                .perform(UpdateDataAction.updateQSO(newQSO));

        onView(withText(newQSO.getOtherStation())).check(matches(isDisplayed()));
        onView(withText(qso.getOtherStation())).check(doesNotExist());
    }
}
