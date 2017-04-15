package net.neoturbine.veles;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.MarkerOptions;

import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.databinding.QsoDetailBinding;
import net.neoturbine.veles.qso.detail.DetailsContracts;
import net.neoturbine.veles.qso.detail.ViewModelImpl;

/**
 * A fragment representing a single QSO detail screen.
 * This fragment is either contained in a {@link QSOListActivity}
 * in two-pane mode (on tablets) or a {@link QSODetailActivity}
 * on handsets.
 */
public class QSODetailFragment extends Fragment implements QSOIdContainer, DetailsContracts.DetailView {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_QSO_ID = "qso_id";

    private long mQSOid = -1;

    private static final int QSO_LOADER = 0;

    private onQSODetailListener mCallback;
    private final DetailsContracts.ViewModel mVM = new ViewModelImpl();

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QSODetailFragment() {
    }

    interface onQSODetailListener {
        void onFinishDelete();

        void onEditQSO(long QSOid);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (onQSODetailListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnFinishEditListener");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(getActivity());
        mVM.attachView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final QsoDetailBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.qso_detail, container, false);
        View rootView = binding.getRoot();

        if (getArguments().containsKey(ARG_QSO_ID)) {
            mQSOid = getArguments().getLong(ARG_QSO_ID);

            getLoaderManager().initLoader(QSO_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    switch (id) {
                        case QSO_LOADER:
                            return new CursorLoader(
                                    getActivity(),
                                    ContentUris.withAppendedId(QSOColumns.CONTENT_URI, mQSOid),
                                    null, null, null, null);
                        default:
                            throw new IllegalArgumentException("Unknown type of loader: " + id);
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    if (!data.moveToFirst()) {
                        return;
                    }

                    final QSO qso = new QSO(data);

                    mVM.setQSO(qso);
                    binding.setViewmodel(mVM);

                    CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
                    if (appBarLayout != null) {
                        appBarLayout.setTitle(mVM.getOtherStation());
                    }

                    setupMap(qso.getMyLocation(), R.id.qso_detail_my_location,
                            qso.getMyStation(), binding.qsoDetailMyLocationText);
                    setupMap(qso.getOtherLocation(), R.id.qso_detail_other_location,
                            qso.getOtherStation(), binding.qsoDetailOtherLocationText);

                    binding.qsoDetailsMaps.setVisibility(
                            qso.getOtherLocation() == null && qso.getMyLocation() == null ?
                                    View.GONE : View.VISIBLE
                    );
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                }
            });
        }

        return rootView;
    }

    @UiThread
    private void setupMap(final VelesLocation location, @IdRes final int fragmentId,
                          final String station, final View textBox) {
        final int DEFAULT_ZOOM = 10;
        FragmentManager fm = getChildFragmentManager();

        MapFragment mapFragment = (MapFragment) fm.findFragmentById(fragmentId);
        assert mapFragment != null;

        if (location != null) {
            fm.beginTransaction().show(mapFragment).commit();
            textBox.setVisibility(View.VISIBLE);

            mapFragment.getMapAsync(googleMap -> {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.asLatLng(), DEFAULT_ZOOM));

                switch (location.getType()) {
                    case LatitudeLongitude:
                        googleMap.addMarker(new MarkerOptions()
                                .title(station)
                                .position(location.asLatLng()));
                        break;
                    case Locator:
                        googleMap.addPolygon(location.asPolygonOptions()
                                .strokeColor(Color.BLACK));
                        break;
                    case FreeForm:
                        googleMap.addMarker(new MarkerOptions()
                                .title(location.getFreeForm())
                                .position(location.asLatLng())).showInfoWindow();
                        googleMap.getUiSettings().setMapToolbarEnabled(false);
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                }
            });
        } else {
            fm.beginTransaction().hide(mapFragment).commit();
            textBox.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param qso_id Parameter 1.
     * @return A new instance of fragment QSODetailFragment.
     */
    static QSODetailFragment newInstance(long qso_id) {
        QSODetailFragment fragment = new QSODetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_QSO_ID, qso_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.qso_detail_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                mCallback.onEditQSO(mQSOid);
                return true;
            case R.id.action_delete:
                getActivity().getContentResolver().delete(
                        ContentUris.withAppendedId(QSOColumns.CONTENT_URI, mQSOid),
                        null, null
                );
                Toast.makeText(getActivity(), R.string.toast_deleted, Toast.LENGTH_LONG).show();
                mCallback.onFinishDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public long getQSOId() {
        if (getArguments() != null) {
            return getArguments().getLong(ARG_QSO_ID);
        }
        return mQSOid;
    }
}
