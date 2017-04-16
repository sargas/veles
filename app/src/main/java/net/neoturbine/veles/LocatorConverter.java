package net.neoturbine.veles;

import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.CharUtils;

public final class LocatorConverter {
    private LocatorConverter() {
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

    static int toDigit(final char digit) {
        if (CharUtils.isAsciiNumeric(digit)) {
            return CharUtils.toIntValue(digit);
        } else if (CharUtils.isAsciiAlphaLower(digit)) {
            return (int) digit - 'a' + 1;
        } else if (CharUtils.isAsciiAlphaUpper(digit)) {
            return (int) digit - 'A' + 1;
        }
        throw new IllegalArgumentException("digit " + digit + " not recognized.");
    }

    static String LatLngToLocator(@NonNull
                                  final LatLng latlng) {
        return toLocator(latlng.longitude, latlng.latitude);
    }

    @NonNull
    public static String toLocator(double longitude, double latitude) {
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

    public static LatLng fromLocator(@NonNull String locator) {
        if (locator.length() != 6)
            throw new IllegalArgumentException("length of QTH locator must be 6");
        double latitude = (toDigit(locator.charAt(1)) - 1.0) * 10.0 + toDigit(locator.charAt(3)) + (toDigit(locator.charAt(5)) - 1.0) / 24.0 + 1.0 / 48.0;
        double longitude = (toDigit(locator.charAt(0)) - 1.0) * 20.0 + toDigit(locator.charAt(2)) * 2 + (toDigit(locator.charAt(4)) - 1.0) / 12.0 + 1.0 / 24.0;

        latitude -= 90.0;
        longitude -= 180.0;
        return new LatLng(latitude, longitude);
    }

    static String LocationToLocator(@NonNull
                                    final Location location) {
        return toLocator(location.getLongitude(), location.getLatitude());
    }
}
