package net.neoturbine.veles.qso.list;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import net.danlew.android.joda.DateUtils;
import net.neoturbine.veles.QSO;
import net.neoturbine.veles.R;

import javax.inject.Inject;
import javax.inject.Named;

public class QSOItemViewModel extends BaseObservable {
    private QSO mQSO;
    private final Context mContext;
    private final String mLoadingMessage;

    @Inject
    public QSOItemViewModel(@Named("application context") Context context) {
        mContext = context;
        mLoadingMessage = context.getString(R.string.qso_loading);
    }

    public void setQSO(QSO qso) {
        mQSO = qso;
        notifyChange();
    }

    @Bindable
    public String getOtherStation() {
        return mQSO == null ? mLoadingMessage : mQSO.getOtherStation();
    }

    @Bindable
    public CharSequence getStartTime() {
        if (mQSO == null)
            return "";
        else
            return DateUtils.getRelativeTimeSpanString(mContext, mQSO.getStartTime());
    }

    @Bindable
    public String getTransmissionFrequency() {
        if (mQSO == null)
            return "";
        else
            return mQSO.getTransmissionFrequency();
    }

    @Bindable
    public String getMode() {
        return mQSO.getMode();
    }
}
