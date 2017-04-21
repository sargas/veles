package net.neoturbine.veles.qso.model;

import net.neoturbine.veles.QSO;

import org.joda.time.DateTime;

public class QSOBuilder {
    private long id = -1;
    private String myStation = "";
    private String otherStation = "";
    private DateTime startTime = DateTime.now();
    private DateTime endTime = DateTime.now();
    private String mode = "";
    private String power = "";
    private String myQuality = "";
    private String otherQuality = "";
    private VelesLocation myLocation = null;
    private VelesLocation otherLocation = null;
    private String txFrequency = "";
    private String rxFrequency = "";
    private String comment = "";

    public QSOBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public QSOBuilder setMyStation(String myStation) {
        this.myStation = myStation;
        return this;
    }

    public QSOBuilder setOtherStation(String otherStation) {
        this.otherStation = otherStation;
        return this;
    }

    public QSOBuilder setStartTime(DateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public QSOBuilder setEndTime(DateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public QSOBuilder setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public QSOBuilder setPower(String power) {
        this.power = power;
        return this;
    }

    public QSOBuilder setMyQuality(String myQuality) {
        this.myQuality = myQuality;
        return this;
    }

    public QSOBuilder setOtherQuality(String otherQuality) {
        this.otherQuality = otherQuality;
        return this;
    }

    public QSOBuilder setMyLocation(VelesLocation myLocation) {
        this.myLocation = myLocation;
        return this;
    }

    public QSOBuilder setOtherLocation(VelesLocation otherLocation) {
        this.otherLocation = otherLocation;
        return this;
    }

    public QSOBuilder setTxFrequency(String txFrequency) {
        this.txFrequency = txFrequency;
        return this;
    }

    public QSOBuilder setRxFrequency(String rxFrequency) {
        this.rxFrequency = rxFrequency;
        return this;
    }

    public QSOBuilder setComment(String comment) {
        this.comment = comment;
        return this;
    }

    public QSO createQSO() {
        return new QSO(id, myStation, otherStation, startTime, endTime, mode, power, myQuality, otherQuality, myLocation, otherLocation, txFrequency, rxFrequency, comment);
    }
}