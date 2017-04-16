package net.neoturbine.veles.qso.detail;

import android.view.View;

import net.danlew.android.joda.DateUtils;
import net.neoturbine.veles.QSO;
import net.neoturbine.veles.R;

public class ViewModelImpl extends DetailsContracts.ViewModel {
    private String mLoadingMessage;
    private DetailsContracts.DetailView mView;
    private QSO mQSO;

    @Override
    public void attachView(DetailsContracts.DetailView view) {
        mView = view;
        mLoadingMessage = mView.getContext().getString(R.string.qso_loading);
    }

    @Override
    public void setQSO(QSO qso) {
        mQSO = qso;
        notifyChange();
    }

    @Override
    public String getOtherStation() {
        return mQSO == null ? mLoadingMessage : mQSO.getOtherStation();
    }

    @Override
    public String getMyStation() {
        return mQSO == null ? "" : mQSO.getMyStation();
    }

    @Override
    public String getStartTime() {
        return mQSO == null ? "" : DateUtils.formatDateTime(
                mView.getContext(), mQSO.getStartTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR)
                + " " +
                mQSO.getStartTime().getZone().getShortName(mQSO.getStartTime().getMillis());
    }

    @Override
    public String getEndTime() {
        return mQSO == null ? "" : DateUtils.formatDateTime(
                mView.getContext(), mQSO.getEndTime(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR)
                + " " +
                mQSO.getEndTime().getZone().getShortName(mQSO.getEndTime().getMillis());
    }

    @Override
    public String getPower() {
        return mQSO == null ? "" : mQSO.getPower();
    }

    @Override
    public String getTransmissionFrequency() {
        return mQSO == null ? "" : mQSO.getTransmissionFrequency();
    }

    @Override
    public String getReceivingFrequency() {
        return mQSO == null ? "" : mQSO.getReceivingFrequency();
    }

    @Override
    public String getMode() {
        return mQSO == null ? "" : mQSO.getMode();
    }

    @Override
    public String getOtherQuality() {
        return mQSO == null ? "" : mQSO.getOtherQuality();
    }

    @Override
    public String getComment() {
        return mQSO == null ? "" : mQSO.getComment();
    }

    @Override
    public String getMyQuality() {
        return mQSO == null ? "" : mQSO.getMyQuality();
    }

    @Override
    public int getMapsVisibility() {
        if (mQSO == null || (mQSO.getMyLocation() == null && mQSO.getOtherLocation() == null))
            return View.GONE;
        else
            return View.VISIBLE;
    }
}
