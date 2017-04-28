package net.neoturbine.veles.qso.data;

import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.qso.model.QSOBuilder;

import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.isA;

public class ReplaceDataAction implements ViewAction {
    private final List<QSO> mList;
    private final FakeDataRepository dataRepository = FakeDataRepository.getInstance();

    private ReplaceDataAction(QSO... items) {
        mList = Arrays.asList(items);
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

    public static ReplaceDataAction emptyData() {
        return new ReplaceDataAction();
    }

    public static ReplaceDataAction dataWithOneItem() {
        return new ReplaceDataAction(firstQSO());
    }

    public static ReplaceDataAction dataWithTwoItems() {
        return new ReplaceDataAction(firstQSO(), secondQSO());
    }

    public static QSO firstQSO() {
        return new QSOBuilder()
                .setId(1234L)
                .setMyStation("COM")
                .setMode("FM")
                .setTxFrequency("101.1 MHz")
                .setOtherStation("WWW")
                .createQSO();
    }

    public static QSO secondQSO() {
        return new QSOBuilder()
                .setId(1111L)
                .setMyStation("HIYA")
                .setOtherStation("VVV")
                .setMode("CW")
                .setTxFrequency("111.4 kHz")
                .createQSO();
    }
}
