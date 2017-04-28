package net.neoturbine.veles.qso.detail;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import net.neoturbine.veles.R;
import net.neoturbine.veles.qso.model.VelesLocation;

public class VelesLocationMap extends Fragment {
    private static final int DEFAULT_ZOOM = 10;
    private VelesLocation mLocation;
    private String mStationName;
    private View mView;
    private MapFragment mMapFragment;

    public VelesLocationMap() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_veles_location_map, container, false);
        mMapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        clearMap();

        return mView;
    }

    public void setLocation(@Nullable VelesLocation location, String station) {
        mLocation = location;
        mStationName = station;
        if (location == null)
            clearMap();
        else {
            showMap();
        }
    }

    private void showMap() {
        mMapFragment.getMapAsync(googleMap -> {
            mView.setVisibility(View.VISIBLE);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocation.asLatLng(), DEFAULT_ZOOM));

            switch (mLocation.getType()) {
                case LatitudeLongitude:
                    googleMap.addMarker(new MarkerOptions()
                            .title(mStationName)
                            .position(mLocation.asLatLng()));
                    break;
                case Locator:
                    googleMap.addPolygon(mLocation.asPolygonOptions()
                            .strokeColor(Color.BLACK));
                    break;
                case FreeForm:
                    googleMap.addMarker(new MarkerOptions()
                            .title(mLocation.getFreeForm())
                            .position(mLocation.asLatLng())).showInfoWindow();
                    googleMap.getUiSettings().setMapToolbarEnabled(false);
                    googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            }
        });
    }

    private void clearMap() {
        mView.setVisibility(View.INVISIBLE);
    }
}
