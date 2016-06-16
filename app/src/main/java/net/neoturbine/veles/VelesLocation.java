package net.neoturbine.veles;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.Serializable;

import static net.neoturbine.veles.QTHConverter.fromQTH;
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
        LatLng center = fromQTH(qth);
        this.mLongitude = center.longitude;
        this.mLatitude = center.latitude;
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

    @NonNull
    LatLng asLatLng() {
        return new LatLng(getLatitude(), getLongitude());
    }

    @NonNull
    private LatLngBounds asLatLngBounds() {
        return new LatLngBounds(
                new LatLng(Math.floor(getLatitude() * 24.0) / 24.0,
                        Math.floor(getLongitude() * 12.0) / 12.0),
                new LatLng(Math.ceil(getLatitude() * 24.0) / 24.0,
                        Math.ceil(getLongitude() * 12.0) / 12.0)
        );
    }

    @NonNull
    PolygonOptions asPolygonOptions() {
        LatLng ne = asLatLngBounds().northeast;
        LatLng sw = asLatLngBounds().southwest;

        return new PolygonOptions()
                .add(ne)
                .add(new LatLng(sw.latitude, ne.longitude))
                .add(sw)
                .add(new LatLng(ne.latitude, sw.longitude));
    }
}
