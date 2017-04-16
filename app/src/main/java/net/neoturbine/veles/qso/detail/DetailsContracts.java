package net.neoturbine.veles.qso.detail;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import net.neoturbine.veles.QSO;

@SuppressWarnings("WeakerAccess")
public class DetailsContracts {
    abstract public static class ViewModel extends BaseObservable {
        abstract public void attachView(DetailView view);
        abstract public void setQSO(QSO qso);

        @Bindable
        abstract public String getOtherStation();
        @Bindable
        abstract public String getMyStation();
        @Bindable
        abstract public String getStartTime();
        @Bindable
        abstract public String getEndTime();
        @Bindable
        abstract public String getMode();
        @Bindable
        abstract public String getPower();
        @Bindable
        abstract public String getTransmissionFrequency();
        @Bindable
        abstract public String getReceivingFrequency();
        @Bindable
        abstract public String getOtherQuality();
        @Bindable
        abstract public String getMyQuality();
        @Bindable
        abstract public String getComment();
        @Bindable
        abstract public int getMapsVisibility();
    }

    public interface DetailView {
        Context getContext();
    }
}
