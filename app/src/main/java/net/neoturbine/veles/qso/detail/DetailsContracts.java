package net.neoturbine.veles.qso.detail;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.qso.model.VelesLocation;

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
        @Bindable
        abstract public VelesLocation getMyLocation();
        @Bindable
        abstract public int getMyLocationVisibility();
        @Bindable
        abstract public VelesLocation getOtherLocation();
        @Bindable
        abstract public int getOtherLocationVisibility();
    }

    public interface DetailView {
        Context getContext();
    }
}
