package net.neoturbine.veles.qso.detail;

import android.app.FragmentManager;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
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
import com.trello.rxlifecycle2.components.RxFragment;

import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.BR;
import net.neoturbine.veles.QSOIdContainer;
import net.neoturbine.veles.qso.list.QSOListActivity;
import net.neoturbine.veles.R;
import net.neoturbine.veles.qso.model.VelesLocation;
import net.neoturbine.veles.databinding.QsoDetailBinding;
import net.neoturbine.veles.qso.data.DataRepository;

import java.util.function.Supplier;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A fragment representing a single QSO detail screen.
 * This fragment is either contained in a {@link QSOListActivity}
 * in two-pane mode (on tablets) or a {@link QSODetailActivity}
 * on handsets.
 */
public class QSODetailFragment extends RxFragment implements QSOIdContainer, DetailsContracts.DetailView {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    private static final String ARG_QSO_ID = "qso_id";

    private long mQSOid = -1;

    private QSODetailFragmentParentListener mParentListener;
    @SuppressWarnings("WeakerAccess")
    @Inject
    DetailsContracts.ViewModel mVM;
    @SuppressWarnings("WeakerAccess")
    @Inject
    DataRepository mDataRepository;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QSODetailFragment() {
    }

    public interface QSODetailFragmentParentListener {
        void onFinishDelete();

        void onEditQSO(long QSOid);

        void setQSOTitle(CharSequence title);
    }

    @Override
    public void onAttach(Context context) {
        AndroidInjection.inject(this);
        super.onAttach(context);

        try {
            mParentListener = (QSODetailFragmentParentListener) context;
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
        QsoDetailBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.qso_detail, container, false);

        binding.setViewmodel(mVM);

        if (getArguments().containsKey(ARG_QSO_ID)) {
            mQSOid = getArguments().getLong(ARG_QSO_ID);

            bindTitle();
            bindMap(BR.myLocation, mVM::getMyLocation,
                    R.id.qso_detail_my_location, mVM::getMyStation);
            bindMap(BR.otherLocation, mVM::getOtherLocation,
                    R.id.qso_detail_other_location, mVM::getOtherStation);

            mDataRepository.getQSO(mQSOid)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError((e) -> Timber.e(e, "Unable to load QSO"))
                    .subscribe(mVM::setQSO);
        }

        return binding.getRoot();
    }

    private void bindTitle() {
        mVM.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int id) {
                if (id == BR.otherStation || id == BR._all) {
                    mParentListener.setQSOTitle(mVM.getOtherStation());
                }
            }
        });
    }

    private void bindMap(int brID, Supplier<VelesLocation> location,
                         @IdRes int fragmentId, Supplier<String> stationName) {
        mVM.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int id) {
                if (id == brID || id == BR._all) {
                    setupMap(location.get(), fragmentId, stationName.get());
                }
            }
        });
    }

    @UiThread
    private void setupMap(final VelesLocation location, @IdRes final int fragmentId,
                          final String station) {
        final int DEFAULT_ZOOM = 10;
        FragmentManager fm = getChildFragmentManager();

        MapFragment mapFragment = (MapFragment) fm.findFragmentById(fragmentId);
        assert mapFragment != null;

        if (location != null) {
            fm.beginTransaction().show(mapFragment).commit();

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
            fm.beginTransaction().hide(mapFragment).commitAllowingStateLoss();
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param qso_id Parameter 1.
     * @return A new instance of fragment QSODetailFragment.
     */
    public static QSODetailFragment newInstance(long qso_id) {
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
                mParentListener.onEditQSO(mQSOid);
                return true;
            case R.id.action_delete:
                mDataRepository.deleteQSO(mQSOid);
                Toast.makeText(getActivity(), R.string.toast_deleted, Toast.LENGTH_LONG).show();
                mParentListener.onFinishDelete();
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
