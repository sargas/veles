package net.neoturbine.veles.qso.model;

import net.neoturbine.veles.QSO;

import org.joda.time.DateTime;

@SuppressWarnings("unused")
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

    public long getId() {
        return id;
    }

    public String getMyStation() {
        return myStation;
    }

    public String getOtherStation() {
        return otherStation;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public DateTime getEndTime() {
        return endTime;
    }

    public String getMode() {
        return mode;
    }

    public String getPower() {
        return power;
    }

    public String getMyQuality() {
        return myQuality;
    }

    public String getOtherQuality() {
        return otherQuality;
    }

    public VelesLocation getMyLocation() {
        return myLocation;
    }

    public VelesLocation getOtherLocation() {
        return otherLocation;
    }

    public String getTxFrequency() {
        return txFrequency;
    }

    public String getRxFrequency() {
        return rxFrequency;
    }

    public String getComment() {
        return comment;
    }

    public QSO createQSO() {
        return new QSO(id, myStation, otherStation, startTime, endTime, mode, power, myQuality, otherQuality, myLocation, otherLocation, txFrequency, rxFrequency, comment);
    }

    public static QSOBuilder fromQSO(QSO qso) {
        return new QSOBuilder()
                .setId(qso.getID())
                .setMyStation(qso.getMyStation())
                .setOtherStation(qso.getOtherStation())
                .setStartTime(qso.getStartTime())
                .setEndTime(qso.getEndTime())
                .setMode(qso.getMode())
                .setPower(qso.getPower())
                .setMyQuality(qso.getMyQuality())
                .setOtherQuality(qso.getOtherQuality())
                .setMyLocation(qso.getMyLocation())
                .setOtherLocation(qso.getOtherLocation())
                .setTxFrequency(qso.getTransmissionFrequency())
                .setRxFrequency(qso.getReceivingFrequency())
                .setComment(qso.getComment());
    }
}