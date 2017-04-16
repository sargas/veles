package net.neoturbine.veles;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlacesStatusCodes;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import net.neoturbine.veles.databinding.HamLocationPickerBinding;
import net.neoturbine.veles.qso.model.VelesLocation;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.regex.Pattern;

import static net.neoturbine.veles.LocatorConverter.LatLngToLocator;
import static net.neoturbine.veles.LocatorConverter.LocationToLocator;
import static net.neoturbine.veles.LocatorConverter.toLocator;

public final class HamLocationPicker extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        FragmentCompat.OnRequestPermissionsResultCallback {

    private static final String STATE_LOCATIONS = "STATE_LOCATIONS";
    private static final String STATE_TAB_HOLDER = "STATE_TAB_HOLDER";
    private static final String STATE_SEARCH_LOCATOR = "STATE_SEARCH_LOCATOR";

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final SparseArray<CurrentTab> mIdsToTabs =
            new SparseArray<>(CurrentTab.values().length);

    static {
        for (CurrentTab tabType : CurrentTab.values())
            mIdsToTabs.put(tabType.getRadioId(), tabType);
    }

    private final HashMap<CurrentTab, VelesLocation> mLastLocations = new HashMap<>();
    private final Pattern mLocatorRegexPattern = Pattern.compile("[A-R][A-R][0-9][0-9]([a-x][a-x]|[A-X][A-X])");
    private final CurrentTabHolder mCurrentTabHolder = new CurrentTabHolder();
    private GoogleApiClient mGoogleApiClient;
    private HamLocationPickerBinding mBinding;
    private CharSequence mTitleText;

    public HamLocationPicker() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //noinspection ConstantConditions,unchecked
            mLastLocations.putAll((HashMap<CurrentTab, VelesLocation>)
                    savedInstanceState.getSerializable(STATE_LOCATIONS));

            //noinspection ConstantConditions
            mCurrentTabHolder.currentTab.set(
                    ((CurrentTabHolder) savedInstanceState.getSerializable(STATE_TAB_HOLDER))
                            .currentTab.get());
        }

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.ham_location_picker, container, true
        );
        mBinding.setCurrentTab(mCurrentTabHolder);

        mBinding.locationText.setText(mTitleText);

        final View.OnClickListener setTab = v -> mCurrentTabHolder.currentTab.set(mIdsToTabs.get(v.getId()));
        mBinding.locationCurrentRadio.setOnClickListener(v -> {
            setTab.onClick(v);
            fillFusedLocation(true);
        });
        mBinding.locationLocatorRadio.setOnClickListener(setTab);
        mBinding.locationSearchRadio.setOnClickListener(setTab);
        mBinding.locationCoordinateRadio.setOnClickListener(setTab);
        mBinding.locationFreeFormRadio.setOnClickListener(setTab);

        PlaceAutocompleteFragment autocompleteFragment =
                (PlaceAutocompleteFragment) getChildFragmentManager()
                        .findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                mBinding.locationSearchLocator.setText(
                        getString(
                                R.string.format_locator, LatLngToLocator(place.getLatLng())));
                mLastLocations.put(CurrentTab.SEARCH, new VelesLocation(place.getLatLng()));
            }

            @Override
            public void onError(final Status status) {
                mBinding.locationSearchLocator.setText(
                        getString(R.string.format_error,
                                PlacesStatusCodes.getStatusCodeString(status.getStatusCode())));
            }
        });

        mBinding.locationManualLocator.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mBinding.locationManualLocatorLayout.setError(null);
                    mLastLocations.remove(CurrentTab.LOCATOR);
                } else if (mLocatorRegexPattern.matcher(s).matches()) {
                    mBinding.locationManualLocatorLayout.setError(null);
                    mLastLocations.put(CurrentTab.LOCATOR, VelesLocation.fromLocatorString(s));
                } else {
                    mBinding.locationManualLocatorLayout.setError(
                            getString(R.string.location_locator_error));
                    mLastLocations.remove(CurrentTab.LOCATOR);
                }
            }
        });
        mBinding.locationFreeForm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mLastLocations.remove(CurrentTab.FREE_FORM);
                } else {
                    mLastLocations.put(CurrentTab.FREE_FORM, VelesLocation.fromFreeFormString(s));
                }
            }
        });
        TextWatcher coordinateWatcher = new TextWatcher() {
            private final Range<Double> mLongitudeRange = Range.between(-180.0, 180.0);
            private final Range<Double> mLatitudeRange = Range.between(-90.0, 90.0);

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                double longitude = 0;
                double latitude = 0;
                boolean isValid = true;

                String longitudeString = mBinding.locationCoordinateLon.getText().toString();
                if (NumberUtils.isParsable(longitudeString) &&
                        mLongitudeRange.contains(Double.parseDouble(longitudeString))) {
                    longitude = Double.parseDouble(longitudeString);
                    mBinding.locationCoordinateLonLayout.setError(null);
                } else {
                    isValid = false;
                    if (!TextUtils.isEmpty(longitudeString))
                        mBinding.locationCoordinateLonLayout.setError(
                                getString(R.string.location_coordinate_longitude_error));
                }

                String latitudeString = mBinding.locationCoordinateLat.getText().toString();
                if (NumberUtils.isParsable(latitudeString) &&
                        mLatitudeRange.contains(Double.parseDouble(latitudeString))) {
                    latitude = Double.parseDouble(latitudeString);
                    mBinding.locationCoordinateLatLayout.setError(null);
                } else {
                    isValid = false;
                    if (!TextUtils.isEmpty(latitudeString))
                        mBinding.locationCoordinateLatLayout.setError(
                                getString(R.string.location_coordinate_latitude_error));
                }

                if (isValid) {
                    mBinding.locationCoordinateLocator.setText(
                            getString(R.string.format_locator, toLocator(longitude, latitude)));
                    mLastLocations.put(CurrentTab.COORDINATE,
                            VelesLocation.fromLongitudeLatitude(longitude, latitude));
                } else {
                    mBinding.locationCoordinateLocator.setText("");
                    mLastLocations.remove(CurrentTab.COORDINATE);
                }
            }
        };
        mBinding.locationCoordinateLat.addTextChangedListener(coordinateWatcher);
        mBinding.locationCoordinateLon.addTextChangedListener(coordinateWatcher);

        if (savedInstanceState != null)
            mBinding.locationCoordinateLocator.setText(
                    savedInstanceState.getCharSequence(STATE_SEARCH_LOCATOR));

        return mBinding.getRoot();

    }

    @Override
    public void onInflate(
            final Context context, final AttributeSet attrs, final Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HamLocationPicker);

        mTitleText = a.getText(R.styleable.HamLocationPicker_text);

        a.recycle();
    }

    @Override
    public void onConnected(@Nullable
                            final Bundle bundle) {
        setGoogleServicesAvailable(true);
        fillFusedLocation(false);
    }

    private void fillFusedLocation(boolean requestPermission) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (requestPermission) {
                FragmentCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return;
        }

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation == null) {
            return;
        }

        mBinding.locationCurrentLocator.setText(
                getString(R.string.format_locator, LocationToLocator(lastLocation)));
        mLastLocations.put(CurrentTab.FIND, new VelesLocation(lastLocation));
    }

    @Override
    public void onConnectionFailed(@NonNull
                                   final ConnectionResult connectionResult) {
        setGoogleServicesAvailable(false);
    }

    @Override
    public void onConnectionSuspended(final int i) {
        setGoogleServicesAvailable(false);
    }

    private void setGoogleServicesAvailable(boolean isOutThere) {
        int visibility = isOutThere ? View.VISIBLE : View.GONE;
        mBinding.locationCurrentRadio.setVisibility(visibility);
        mBinding.locationSearchRadio.setVisibility(visibility);
        if (!isOutThere
                && (mCurrentTabHolder.currentTab.get() == CurrentTab.FIND
                || mCurrentTabHolder.currentTab.get() == CurrentTab.SEARCH)) {
            mCurrentTabHolder.currentTab.set(null);
            mBinding.locationRadioGroup.clearCheck();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    fillFusedLocation(false);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable(STATE_LOCATIONS, mLastLocations);
        state.putSerializable(STATE_TAB_HOLDER, mCurrentTabHolder);
        /* This value must be saved, since onPlaceSelectedListener isn't called on resume */
        state.putCharSequence(STATE_SEARCH_LOCATOR, mBinding.locationSearchLocator.getText());
    }

    @Nullable
    VelesLocation getLocation() {
        return mLastLocations.get(mCurrentTabHolder.currentTab.get());
    }

    void setLocation(@Nullable VelesLocation location) {
        if (location == null)
            return;
        switch (location.getType()) {
            case LatitudeLongitude:
                mCurrentTabHolder.currentTab.set(CurrentTab.COORDINATE);
                mBinding.locationCoordinateRadio.setChecked(true);
                mLastLocations.put(CurrentTab.COORDINATE, location);
                mBinding.locationCoordinateLon.setText(
                        String.valueOf(location.getLongitude()));
                mBinding.locationCoordinateLat.setText(
                        String.valueOf(location.getLatitude()));
                break;
            case Locator:
                mCurrentTabHolder.currentTab.set(CurrentTab.LOCATOR);
                mBinding.locationLocatorRadio.setChecked(true);
                mLastLocations.put(CurrentTab.LOCATOR, location);
                mBinding.locationManualLocator.setText(
                        location.getLocator());
                break;
            case FreeForm:
                mCurrentTabHolder.currentTab.set(CurrentTab.FREE_FORM);
                mBinding.locationFreeFormRadio.setChecked(true);
                mLastLocations.put(CurrentTab.FREE_FORM, location);
                mBinding.locationFreeForm.setText(location.getFreeForm());
                break;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public enum CurrentTab {
        FIND(R.id.location_current_radio),
        LOCATOR(R.id.location_locator_radio),
        SEARCH(R.id.location_search_radio),
        COORDINATE(R.id.location_coordinate_radio),
        FREE_FORM(R.id.location_free_form_radio);

        private final int mRadioID;

        CurrentTab(final int radioID) {
            this.mRadioID = radioID;
        }

        public int getRadioId() {
            return this.mRadioID;
        }
    }

    public static final class CurrentTabHolder implements Serializable {
        private static final long serialVersionUID = 8370389244855208970L;
        public final ObservableField<CurrentTab> currentTab = new ObservableField<>();
    }
}
