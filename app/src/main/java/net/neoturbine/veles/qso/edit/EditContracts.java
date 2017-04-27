package net.neoturbine.veles.qso.edit;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.qso.model.VelesLocation;

import org.joda.time.DateTime;

@SuppressWarnings("WeakerAccess")
public interface EditContracts {
    abstract class ViewModel extends BaseObservable {
        abstract public void setQSO(QSO qso);
        abstract public QSO getQSO();

        @Bindable
        abstract public String getOtherStation();
        abstract public void setOtherStation(String station);
        @Bindable
        abstract public String getMyStation();
        abstract public void setMyStation(String myStation);
        @Bindable
        abstract public String getMode();
        abstract public void setMode(String mode);
        @Bindable
        abstract public String getPower();
        abstract public void setPower(String power);
        @Bindable
        abstract public String getTransmissionFrequency();
        abstract public void setTransmissionFrequency(String transmissionFrequency);
        @Bindable
        abstract public String getReceivingFrequency();
        abstract public void setReceivingFrequency(String receivingFrequency);
        @Bindable
        abstract public String getOtherQuality();
        abstract public void setOtherQuality(String quality);
        @Bindable
        abstract public String getMyQuality();
        abstract public void setMyQuality(String quality);
        @Bindable
        abstract public String getComment();
        abstract public void setComment(String comment);
        @Bindable
        abstract public DateTime getStartTime();
        abstract public void setStartTime(DateTime startTime);
        @Bindable
        abstract public DateTime getEndTime();
        abstract public void setEndTime(DateTime endTime);
        @Bindable
        abstract public VelesLocation getMyLocation();
        abstract public void setMyLocation(VelesLocation location);
        @Bindable
        abstract public VelesLocation getOtherLocation();
        abstract public void setOtherLocation(VelesLocation location);
    }
}
