package net.neoturbine.veles;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import static net.neoturbine.veles.QTHConverter.toQTH;

class VelesLocation implements Serializable {
    private static final long serialVersionUID = 6587082321100439439L;
    private final double mLongitude;

    private final double mLatitude;
    @NonNull
    private final String mQTH;
    @NonNull
    private final Type mType;

    VelesLocation(double longitude, double latitude) {
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mQTH = toQTH(longitude, latitude);
        this.mType = Type.LatitudeLongitude;
    }

    VelesLocation(@NonNull String qth) {
        this.mLongitude = this.mLatitude = 0;
        this.mQTH = qth;
        this.mType = Type.QTH;
    }

    VelesLocation(@NonNull Location location) {
        this(location.getLongitude(), location.getLatitude());
    }

    VelesLocation(@NonNull LatLng latlng) {
        this(latlng.longitude, latlng.latitude);
    }

    @Override
    @NonNull
    public String toString() {
        return "VelesLocation{" + "mLongitude=" + mLongitude +
                ", mLatitude=" + mLatitude +
                ", mQTH='" + mQTH + '\'' +
                ", mType=" + mType +
                '}';
    }

    double getLongitude() {
        return mLongitude;
    }

    double getLatitude() {
        return mLatitude;
    }

    @NonNull
    String getQTH() {
        return mQTH;
    }

    @NonNull
    public Type getType() {
        return mType;
    }

    enum Type {
        LatitudeLongitude, QTH
    }


}
