package net.neoturbine.veles.qso.detail;

import net.danlew.android.joda.DateUtils;
import net.neoturbine.veles.QSO;

public class ViewModelImpl extends DetailsContracts.ViewModel {
    private DetailsContracts.DetailView mView;
    private QSO mQSO;

    @Override
    public void attachView(DetailsContracts.DetailView view) {
        mView = view;
    }

    @Override
    public void setQSO(QSO qso) {
        mQSO = qso;
    }

    @Override
    public String getOtherStation() {
        return mQSO.getOtherStation();
    }

    @Override
    public String getMyStation() {
        return mQSO.getMyStation();
    }

    @Override
    public String getStartTime() {
        return DateUtils.formatDateTime(
                mView.getContext(), mQSO.getStartTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR)
                + " " +
                mQSO.getStartTime().getZone().getShortName(mQSO.getStartTime().getMillis());
    }

    @Override
    public String getEndTime() {
        return DateUtils.formatDateTime(
                mView.getContext(), mQSO.getEndTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR)
                + " " +
                mQSO.getEndTime().getZone().getShortName(mQSO.getEndTime().getMillis());
    }

    @Override
    public String getPower() {
        return mQSO.getPower();
    }

    @Override
    public String getTransmissionFrequency() {
        return mQSO.getTransmissionFrequency();
    }

    @Override
    public String getReceivingFrequency() {
        return mQSO.getReceivingFrequency();
    }

    @Override
    public String getMode() {
        return mQSO.getMode();
    }

    @Override
    public String getOtherQuality() {
        return mQSO.getOtherQuality();
    }

    @Override
    public String getComment() {
        return mQSO.getComment();
    }

    @Override
    public String getMyQuality() {
        return mQSO.getMyQuality();
    }
}
