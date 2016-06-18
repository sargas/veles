package net.neoturbine.veles;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.format.DateUtils;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

import net.neoturbine.veles.databinding.QsoDetailBinding;

/**
 * A fragment representing a single QSO detail screen.
 * This fragment is either contained in a {@link QSOListActivity}
 * in two-pane mode (on tablets) or a {@link QSODetailActivity}
 * on handsets.
 */
public class QSODetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_QSO_ID = "qso_id";

    private long mQSOid = -1;

    private static final int QSO_LOADER = 0;

    private static final int REQUEST_EDIT = 1;

    private onFinishListener mCallback;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QSODetailFragment() {
    }

    interface onFinishListener {
        void onFinishDelete();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (onFinishListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnFinishEditListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View rootView = DataBindingUtil.inflate(inflater, R.layout.qso_detail, container, false)
                .getRoot();

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
                    QsoDetailBinding binding = DataBindingUtil.getBinding(getView());
                    assert binding != null;

                    final QSO qso = new QSO(data);

                    binding.setQso(qso);
                    binding.setStartTime(DateUtils.formatDateTime(
                            getActivity(), qso.getStartTime(),
                            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR));
                    binding.setEndTime(DateUtils.formatDateTime(
                            getActivity(), qso.getEndTime(),
                            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR));

                    CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
                    if (appBarLayout != null) {
                        appBarLayout.setTitle(qso.getOtherStation());
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
        FragmentManager fm = getChildFragmentManager();

        MapFragment mapFragment = (MapFragment) fm.findFragmentById(fragmentId);
        assert mapFragment != null;

        if (location != null) {
            fm.beginTransaction().show(mapFragment).commit();
            textBox.setVisibility(View.VISIBLE);

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location.asLatLng(), 10));

                    switch (location.getType()) {
                        case LatitudeLongitude:
                            googleMap.addMarker(new MarkerOptions()
                                    .title(station)
                                    .position(location.asLatLng()));
                            break;
                        case QTH:
                            googleMap.addPolygon(location.asPolygonOptions()
                                    .strokeColor(Color.BLACK));
                            break;
                    }
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
                Intent intent = new Intent(getActivity(), QSOEditActivity.class);
                intent.putExtra(QSOEditActivity.ARG_QSO_ID, mQSOid);

                startActivityForResult(intent, REQUEST_EDIT);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT:
                if (resultCode == QSOEditActivity.RESULT_DELETED) {
                    mCallback.onFinishDelete();
                }
                break;
        }
    }
}
