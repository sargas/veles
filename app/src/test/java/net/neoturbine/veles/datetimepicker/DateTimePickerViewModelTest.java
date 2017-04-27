package net.neoturbine.veles.datetimepicker;


import android.content.Context;
import android.databinding.Observable;
import android.os.Bundle;

import net.neoturbine.veles.BR;

import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DateTimePickerViewModelTest {
    private static final long LIMIT_FOR_SPONTANEITY_IN_MILLIS = 500;

    @Mock
    private Context mContext;

    @Mock
    private DateTimePickerContract.View mView;

    private final DateTimePickerViewModel mVM = new DateTimePickerViewModel(new DateTimePickerModel());

    private final DateTime mTestDateTime = new DateTime(2004, 10, 3, 12, 30,
            DateTimeZone.forID("America/New_York"));

    private void setupMocksAndAttachView(Bundle bundle) {
        when(mView.getContext()).thenReturn(mContext);
        mVM.attachView(mView, bundle);
    }

    @Test
    public void testTimeIsNow() {
        DateTime currentTime = new DateTime();
        setupMocksAndAttachView(null);
        Interval diff = new Interval(mVM.getDateTime(), currentTime);
        assertThat(diff.toDurationMillis(), Matchers.lessThan(LIMIT_FOR_SPONTANEITY_IN_MILLIS));
    }

    @Test
    public void testTriggerViewsOnSettingDateTime() {
        Observable.OnPropertyChangedCallback callback = mock(Observable.OnPropertyChangedCallback.class);

        mVM.addOnPropertyChangedCallback(callback);

        setupMocksAndAttachView(null);
        mVM.setDateTime(mTestDateTime);
        assertEquals(mTestDateTime, mVM.getDateTime());

        verify(callback).onPropertyChanged(mVM, BR._all);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void testTriggerViewsOnRestoringDateTime() {
        Observable.OnPropertyChangedCallback callback = mock(Observable.OnPropertyChangedCallback.class);

        Bundle bundle = mock(Bundle.class);
        when(bundle.getSerializable(DateTimePickerViewModel.STATE_TIME)).thenReturn(mTestDateTime);

        mVM.addOnPropertyChangedCallback(callback);

        setupMocksAndAttachView(bundle);
        assertEquals(mTestDateTime, mVM.getDateTime());

        verify(callback).onPropertyChanged(mVM, BR._all);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void testDateFormat() {
        setupMocksAndAttachView(null);
        mVM.setDateTime(mTestDateTime);
        assertEquals("October 3, 2004", mVM.getDate());
    }

    @Test
    public void testTimeFormat() {
        setupMocksAndAttachView(null);
        mVM.setDateTime(mTestDateTime);
        assertEquals("12:30 PM", mVM.getTime());
    }

    @Test
    public void testTriggerViewsOnHintSetting() {
        String testHint = "Hello World";

        Observable.OnPropertyChangedCallback callback = mock(Observable.OnPropertyChangedCallback.class);
        mVM.addOnPropertyChangedCallback(callback);

        setupMocksAndAttachView(null);
        verifyZeroInteractions(callback);

        mVM.setHint(testHint);
        assertEquals(testHint, mVM.getHint());

        verify(callback).onPropertyChanged(mVM, BR.hint);
        verifyNoMoreInteractions(callback);
    }

    @Test
    public void testSavingState() {
        setupMocksAndAttachView(null);
        mVM.setDateTime(mTestDateTime);

        Bundle bundle = mock(Bundle.class);
        mVM.onSaveInstanceState(bundle);

        verify(bundle).putSerializable(DateTimePickerViewModel.STATE_TIME, mTestDateTime);
        verifyNoMoreInteractions(bundle);
    }
}
