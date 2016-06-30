package net.neoturbine.veles;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static net.neoturbine.veles.QTHConverter.LatLngToLocator;
import static net.neoturbine.veles.QTHConverter.fromLocator;
import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class QTHConverterUnitTest {
    @SuppressWarnings("unused")
    @RunWith(Parameterized.class)
    public static class LatLngToStringTest {
        @Parameter
        public String mString;
        @Parameter(value = 1)
        public LatLng mLatLng;

        @Parameters
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"HK34wh", new LatLng(14.321, -32.123)},
                    {"JN18eu", new LatLng(48.85749, 2.37442)},
                    {"KH07ag", new LatLng(-12.72608, 20.03906)},
                    {"GH25uf", new LatLng(-14.77488, -54.31641)},
                    {"FO43uc", new LatLng(53.12041, -70.3125)}
            });
        }

        @Test
        public void test_toLocatorString() {
            assertEquals(mString, LatLngToLocator(mLatLng));
        }

        @Test
        public void test_fromLocatorString() {
            LatLng actual = fromLocator(mString);
            assertEquals(mLatLng.longitude, actual.longitude, 5.0 / 60.);
            assertEquals(mLatLng.latitude, actual.latitude, 2.5 / 60.);
        }
    }
}
