package net.neoturbine.veles;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static net.neoturbine.veles.QTHConverter.nThLetter;
import static net.neoturbine.veles.QTHConverter.toDigit;
import static org.junit.Assert.assertEquals;

@SuppressWarnings("unused")
@RunWith(Parameterized.class)
public class NthLetterUnitTest {
    @SuppressWarnings("WeakerAccess")
    @Parameter
    public int mNumber;
    @SuppressWarnings("WeakerAccess")
    @Parameter(value = 1)
    public char mCharacter;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {1, 'A'}, {8, 'H'}, {26, 'Z'}
        });
    }

    @Test
    public void test_uppercase() {
        assertEquals(mCharacter, nThLetter(mNumber, true));
    }

    @Test
    public void test_lowercase() {
        assertEquals(Character.toLowerCase(mCharacter),
                nThLetter(mNumber, false));
    }

    @Test
    public void test_backwards_uppercase() {
        assertEquals(mNumber, toDigit(mCharacter));
    }

    @Test
    public void test_backwards_lowercase() {
        assertEquals(mNumber, toDigit(Character.toLowerCase(mCharacter)));
    }
}
