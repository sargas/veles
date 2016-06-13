package net.neoturbine.veles;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import net.neoturbine.veles.databinding.HamLocationPickerBinding;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static net.neoturbine.veles.QTHConverter.LatLngToQTH;
import static net.neoturbine.veles.QTHConverter.LocationToQTH;

public final class HamLocationPicker extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private static final SparseArray<CurrentTab> mIdsToTabs =
            new SparseArray<>(CurrentTab.values().length);

    static {
        for (CurrentTab tabType : CurrentTab.values())
            mIdsToTabs.put(tabType.getRadioId(), tabType);
    }

    private final Map<CurrentTab, VelesLocation> mLastLocations = new HashMap<>();
    private final Pattern mQTHRegexPattern = Pattern.compile("[A-R][A-R][0-9][0-9]([a-x][a-x]|[A-X][A-X])");
    private final CurrentTabHolder mCurrentTabHolder = new CurrentTabHolder();
    private GoogleApiClient mGoogleApiClient;
    private HamLocationPickerBinding mBinding;

    public HamLocationPicker() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO Handle screen orientation change
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

        final View.OnClickListener setTab = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mCurrentTabHolder.currentTab.set(mIdsToTabs.get(v.getId()));
            }
        };
        mBinding.locationCurrentRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTab.onClick(v);
                fillFusedLocation(true);
            }
        });
        mBinding.locationQthRadio.setOnClickListener(setTab);
        mBinding.locationSearchRadio.setOnClickListener(setTab);
        mBinding.locationCoordinateRadio.setOnClickListener(setTab);

        PlaceAutocompleteFragment autocompleteFragment =
                (PlaceAutocompleteFragment) getChildFragmentManager()
                        .findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(final Place place) {
                mBinding.locationSearchQth.setText(
                        getString(
                                R.string.format_qth, LatLngToQTH(place.getLatLng())));
                mLastLocations.put(CurrentTab.SEARCH, new VelesLocation(place.getLatLng()));
            }

            @Override
            public void onError(final Status status) {
                //TODO add onError
            }
        });

        mBinding.locationManualQth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mBinding.locationManualQthLayout.setError(null);
                    mLastLocations.remove(CurrentTab.QTH);
                } else if (mQTHRegexPattern.matcher(s).matches()) {
                    mBinding.locationManualQthLayout.setError(null);
                    mLastLocations.put(CurrentTab.QTH, new VelesLocation(s.toString()));
                } else {
                    mBinding.locationManualQthLayout.setError(
                            getString(R.string.location_qth_error));
                    mLastLocations.remove(CurrentTab.QTH);
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
                    mLastLocations.put(CurrentTab.COORDINATE, new VelesLocation(longitude, latitude));
                } else if (TextUtils.isEmpty(mBinding.locationCoordinateLon.getText()) &&
                        TextUtils.isEmpty(latitudeString)) {
                    mLastLocations.remove(CurrentTab.COORDINATE);
                } else {
                    mLastLocations.remove(CurrentTab.COORDINATE);
                }
            }
        };
        mBinding.locationCoordinateLat.addTextChangedListener(coordinateWatcher);
        mBinding.locationCoordinateLon.addTextChangedListener(coordinateWatcher);


        return mBinding.getRoot();

    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {
        fillFusedLocation(false);
    }

    private void fillFusedLocation(boolean requestPermission) {
        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (requestPermission) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return;
        }

        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation == null) {
            // TODO Maybe show a warning/error if requestPermission = true?
            return;
        }

        mBinding.locationCurrentQth.setText(
                getString(R.string.format_qth, LocationToQTH(lastLocation)));
        mLastLocations.put(CurrentTab.FIND, new VelesLocation(lastLocation));
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        // TODO Add onConnectionFailed
    }

    @Override
    public void onConnectionSuspended(final int i) {
        // TODO Add onConnectionSuspended
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
            case QTH:
                mCurrentTabHolder.currentTab.set(CurrentTab.QTH);
                mBinding.locationQthRadio.setChecked(true);
                mLastLocations.put(CurrentTab.QTH, location);
                mBinding.locationManualQth.setText(
                        location.getQTH());
                break;
        }
    }

    public enum CurrentTab {
        FIND(R.id.location_current_radio),
        QTH(R.id.location_qth_radio),
        SEARCH(R.id.location_search_radio),
        COORDINATE(R.id.location_coordinate_radio);

        private final int mRadioID;

        CurrentTab(final int radioID) {
            this.mRadioID = radioID;
        }

        public int getRadioId() {
            return this.mRadioID;
        }
    }

    public static final class CurrentTabHolder {
        public final ObservableField<CurrentTab> currentTab = new ObservableField<>();
    }
}
