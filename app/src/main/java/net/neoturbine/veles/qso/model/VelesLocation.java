package net.neoturbine.veles.qso.model;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.Serializable;
import java.util.Objects;

import static net.neoturbine.veles.utils.LocatorConverter.fromLocator;
import static net.neoturbine.veles.utils.LocatorConverter.toLocator;

public class VelesLocation implements Serializable {
    private static final long serialVersionUID = 6587082321100439439L;
    private final double mLongitude;
    private final double mLatitude;
    @NonNull
    private final String mLocator;
    @NonNull
    private final Type mType;
    @NonNull
    private final String mFreeForm;

    private VelesLocation(double longitude, double latitude) {
        this.mLongitude = longitude;
        this.mLatitude = latitude;
        this.mLocator = toLocator(longitude, latitude);
        this.mType = Type.LatitudeLongitude;
        this.mFreeForm = this.mLocator;
    }

    private VelesLocation(@NonNull String locator) {
        LatLng center = fromLocator(locator);
        this.mLongitude = center.longitude;
        this.mLatitude = center.latitude;
        this.mLocator = locator;
        this.mType = Type.Locator;
        this.mFreeForm = locator;
    }

    private VelesLocation(@NonNull String freeForm, @SuppressWarnings({"SameParameterValue", "UnusedParameters"}) int ignored) {
        this.mLatitude = this.mLongitude = 0;
        this.mLocator = "";
        this.mType = Type.FreeForm;
        this.mFreeForm = freeForm;
    }

    public static VelesLocation fromLongitudeLatitude(double longitude, double latitude) {
        return new VelesLocation(longitude, latitude);
    }

    public static VelesLocation fromLocatorString(@NonNull CharSequence locator) {
        return new VelesLocation(locator.toString());
    }

    public static VelesLocation fromFreeFormString(@NonNull CharSequence freeForm) {
        return new VelesLocation(freeForm.toString(), 0);
    }

    public VelesLocation(@NonNull Location location) {
        this(location.getLongitude(), location.getLatitude());
    }

    public VelesLocation(@NonNull LatLng latlng) {
        this(latlng.longitude, latlng.latitude);
    }

    @Override
    @NonNull
    public String toString() {
        return "VelesLocation{" + "mLongitude=" + mLongitude +
                ", mLatitude=" + mLatitude +
                ", mLocator='" + mLocator + '\'' +
                ", mType=" + mType +
                '}';
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    @NonNull
    public String getLocator() {
        return mLocator;
    }

    @NonNull
    public Type getType() {
        return mType;
    }

    @NonNull
    public String getFreeForm() {
        return mFreeForm;
    }

    public enum Type {
        LatitudeLongitude, Locator, FreeForm
    }

    @NonNull
    public LatLng asLatLng() {
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
    public PolygonOptions asPolygonOptions() {
        LatLng ne = asLatLngBounds().northeast;
        LatLng sw = asLatLngBounds().southwest;

        return new PolygonOptions()
                .add(ne)
                .add(new LatLng(sw.latitude, ne.longitude))
                .add(sw)
                .add(new LatLng(ne.latitude, sw.longitude));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VelesLocation that = (VelesLocation) o;
        return Double.compare(that.mLongitude, mLongitude) == 0 &&
                Double.compare(that.mLatitude, mLatitude) == 0 &&
                Objects.equals(mLocator, that.mLocator) &&
                mType == that.mType &&
                Objects.equals(mFreeForm, that.mFreeForm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mLongitude, mLatitude, mLocator, mType, mFreeForm);
    }
}
