package net.neoturbine.veles.qso.edit;

import net.neoturbine.veles.BR;
import net.neoturbine.veles.QSO;
import net.neoturbine.veles.qso.model.QSOBuilder;
import net.neoturbine.veles.qso.model.VelesLocation;

import org.joda.time.DateTime;

import java.util.Objects;

public class ViewModelImp extends EditContracts.ViewModel {
    private QSOBuilder mBuilder = new QSOBuilder();

    @Override
    public void setQSO(QSO qso) {
        mBuilder = QSOBuilder.fromQSO(qso);
        notifyChange();
    }

    @Override
    public QSO getQSO() {
        return mBuilder.createQSO();
    }

    @Override
    public String getOtherStation() {
        return mBuilder.getOtherStation();
    }

    @Override
    public void setOtherStation(String station) {
        mBuilder.setOtherStation(station);
        notifyPropertyChanged(BR.otherStation);
    }

    @Override
    public String getMyStation() {
        return mBuilder.getMyStation();
    }

    @Override
    public void setMyStation(String myStation) {
        mBuilder.setMyStation(myStation);
        notifyPropertyChanged(BR.myStation);
    }

    @Override
    public String getMode() {
        return mBuilder.getMode();
    }

    @Override
    public void setMode(String mode) {
        mBuilder.setMode(mode);
        notifyPropertyChanged(BR.mode);
    }

    @Override
    public String getPower() {
        return mBuilder.getPower();
    }

    @Override
    public void setPower(String power) {
        mBuilder.setPower(power);
        notifyPropertyChanged(BR.power);
    }

    @Override
    public String getTransmissionFrequency() {
        return mBuilder.getTxFrequency();
    }

    @Override
    public void setTransmissionFrequency(String transmissionFrequency) {
        mBuilder.setTxFrequency(transmissionFrequency);
        notifyPropertyChanged(BR.transmissionFrequency);
    }

    @Override
    public String getReceivingFrequency() {
        return mBuilder.getRxFrequency();
    }

    @Override
    public void setReceivingFrequency(String receivingFrequency) {
        mBuilder.setRxFrequency(receivingFrequency);
        notifyPropertyChanged(BR.receivingFrequency);
    }

    @Override
    public String getOtherQuality() {
        return mBuilder.getOtherQuality();
    }

    @Override
    public void setOtherQuality(String quality) {
        mBuilder.setOtherQuality(quality);
        notifyPropertyChanged(BR.otherQuality);
    }

    @Override
    public String getMyQuality() {
        return mBuilder.getMyQuality();
    }

    @Override
    public void setMyQuality(String quality) {
        mBuilder.setMyQuality(quality);
        notifyPropertyChanged(BR.myQuality);
    }

    @Override
    public String getComment() {
        return mBuilder.getComment();
    }

    @Override
    public void setComment(String comment) {
        mBuilder.setComment(comment);
        notifyPropertyChanged(BR.comment);
    }

    @Override
    public DateTime getStartTime() {
        return mBuilder.getStartTime();
    }

    @Override
    public void setStartTime(DateTime startTime) {
        mBuilder.setStartTime(startTime);
        notifyPropertyChanged(BR.startTime);
    }

    @Override
    public DateTime getEndTime() {
        return mBuilder.getEndTime();
    }

    @Override
    public void setEndTime(DateTime endTime) {
        mBuilder.setEndTime(endTime);
        notifyPropertyChanged(BR.endTime);
    }

    @Override
    public VelesLocation getMyLocation() {
        return mBuilder.getMyLocation();
    }

    @Override
    public void setMyLocation(VelesLocation location) {
        if (!Objects.equals(location, mBuilder.getMyLocation())) {
            mBuilder.setMyLocation(location);
            notifyPropertyChanged(BR.myLocation);
        }
    }

    @Override
    public VelesLocation getOtherLocation() {
        return mBuilder.getOtherLocation();
    }

    @Override
    public void setOtherLocation(VelesLocation location) {
        if (!Objects.equals(location, mBuilder.getOtherLocation())) {
            mBuilder.setOtherLocation(location);
            notifyPropertyChanged(BR.otherLocation);
        }
    }
}
