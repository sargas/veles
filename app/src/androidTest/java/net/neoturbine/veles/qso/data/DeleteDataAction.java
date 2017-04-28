package net.neoturbine.veles.qso.data;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import org.hamcrest.Matcher;

import static org.hamcrest.Matchers.isA;

public class DeleteDataAction implements ViewAction {
    private final FakeDataRepository mDataRepository = FakeDataRepository.getInstance();
    private final long mID;

    private DeleteDataAction(long id) {
        mID = id;
    }

    public static DeleteDataAction deleteID(long id) {
        return new DeleteDataAction(id);
    }

    @Override
    public Matcher<View> getConstraints() {
        return isA(View.class);
    }

    @Override
    public String getDescription() {
        return "Delete a QSO from the fake data repository";
    }

    @Override
    public void perform(UiController uiController, View view) {
        mDataRepository.deleteQSO(mID);
    }
}
