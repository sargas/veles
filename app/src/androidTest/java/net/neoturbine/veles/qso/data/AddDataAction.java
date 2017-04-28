package net.neoturbine.veles.qso.data;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import net.neoturbine.veles.QSO;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.isA;

public class AddDataAction implements ViewAction {
    private final FakeDataRepository mDataRepository = FakeDataRepository.getInstance();
    private final QSO mNewQSO;

    private AddDataAction(QSO qso) {
        mNewQSO = qso;
    }

    public static AddDataAction addQSO(QSO qso) {
        return new AddDataAction(qso);
    }

    @Override
    public Matcher<View> getConstraints() {
        return isA(View.class);
    }

    @Override
    public String getDescription() {
        return "Add a QSO from the fake data repository";
    }

    @Override
    public void perform(UiController uiController, View view) {
        mDataRepository.addQSO(mNewQSO);
    }
}
