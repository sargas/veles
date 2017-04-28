package net.neoturbine.veles.qso.data;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import net.neoturbine.veles.QSO;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.isA;

public class UpdateDataAction implements ViewAction {
    private final FakeDataRepository mDataRepository = FakeDataRepository.getInstance();
    private final QSO mNewQSO;

    private UpdateDataAction(QSO qso) {
        mNewQSO = qso;
    }

    public static UpdateDataAction updateQSO(QSO qso) {
        return new UpdateDataAction(qso);
    }

    @Override
    public Matcher<View> getConstraints() {
        return isA(View.class);
    }

    @Override
    public String getDescription() {
        return "Update a QSO in the fake data repository";
    }

    @Override
    public void perform(UiController uiController, View view) {
        mDataRepository.updateQSO(mNewQSO);
    }
}
