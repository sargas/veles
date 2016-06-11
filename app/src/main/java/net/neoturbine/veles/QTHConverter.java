package net.neoturbine.veles;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

final class QTHConverter {
    private QTHConverter() {
    }

    static char nThLetter(final int number, final boolean upperCase) {
        return (char) ((int) (number + (upperCase ? 'A' : 'a') - 1));
    }

    private static char nThLetter(final double number, final boolean upperCase) {
        return nThLetter((int) number, upperCase);
    }

    private static char toLetter(final double number) {
        return (char) ((int) (number % 10 + '0'));
    }

    static String LatLngToQTH(@NonNull final LatLng latlng) {
        return toQTH(latlng.longitude, latlng.latitude);
    }

    @NonNull
    static String toQTH(double longitude, double latitude) {
        StringBuilder output = new StringBuilder("123456");

        latitude += 90;
        longitude += 180;

        output.setCharAt(0, nThLetter(longitude / 20 + 1, true));
        output.setCharAt(2, toLetter(longitude % 20 / 2));
        output.setCharAt(4, nThLetter(longitude % 20 % 2 * 12 + 1, false));

        output.setCharAt(1, nThLetter(latitude / 10 + 1, true));
        output.setCharAt(3, toLetter(latitude % 10));
        output.setCharAt(5, nThLetter(latitude % 10 % 1 * 24 + 1, false));

        return output.toString();
    }

    static String LocationToQTH(@NonNull final Location location) {
        return toQTH(location.getLongitude(), location.getLatitude());
    }
}
