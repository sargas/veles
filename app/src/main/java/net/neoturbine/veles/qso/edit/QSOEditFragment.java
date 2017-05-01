package net.neoturbine.veles.qso.edit;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.RxFragment;

import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.BR;
import net.neoturbine.veles.HamLocationPicker;
import net.neoturbine.veles.QSOIdContainer;
import net.neoturbine.veles.R;
import net.neoturbine.veles.SignalQualityPicker;
import net.neoturbine.veles.databinding.QsoEditBinding;
import net.neoturbine.veles.datetimepicker.DateTimePicker;
import net.neoturbine.veles.qso.data.DataRepository;
import net.neoturbine.veles.qso.model.VelesLocation;

import org.joda.time.DateTime;

import java.util.function.Supplier;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QSOEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QSOEditFragment extends RxFragment implements QSOIdContainer {
    private static final String ARG_QSO_ID = "qso_id";
    private static final String PREF_LAST_USED_STATION = "PREF_LAST_USED_STATION";

    private long mQSOid = -1;

    private OnFinishEditListener mCallback;

    private SharedPreferences mPrefs;
    @SuppressWarnings("WeakerAccess")
    @Inject
    EditContracts.ViewModel mVM;
    @SuppressWarnings("WeakerAccess")
    @Inject
    DataRepository mDataRepository;

    @SuppressWarnings("WeakerAccess")
    public QSOEditFragment() {
        // Required empty public constructor
    }

    public interface OnFinishEditListener {
        void onFinishEdit();

        void onFinishDelete();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param qso_id Parameter 1.
     * @return A new instance of fragment QSOEditFragment.
     */
    public static QSOEditFragment newInstance(long qso_id) {
        QSOEditFragment fragment = new QSOEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_QSO_ID, qso_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(getActivity());
        if (getArguments() != null) {
            mQSOid = getArguments().getLong(ARG_QSO_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        AndroidInjection.inject(this);
        super.onAttach(context);

        try {
            mCallback = (OnFinishEditListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnFinishEditListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        QsoEditBinding binding =
                DataBindingUtil.inflate(inflater, R.layout.qso_edit, container, false);
        binding.setViewmodel(mVM);

        binding.qsoMode.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.qso_modes)));

        bindHamLocationPicker(R.id.qso_my_location, BR.myLocation, mVM::setMyLocation, mVM::getMyLocation);
        bindHamLocationPicker(R.id.qso_other_location, BR.otherLocation, mVM::setOtherLocation, mVM::getOtherLocation);

        bindDateTimeFragment(R.id.qso_start_time, BR.startTime, mVM::setStartTime, mVM::getStartTime);
        bindDateTimeFragment(R.id.qso_end_time, BR.endTime, mVM::setEndTime, mVM::getEndTime);

        bindSignalQualityPicker(R.id.qso_other_quality, BR.otherQuality, mVM::setOtherQuality, mVM::getOtherQuality);
        bindSignalQualityPicker(R.id.qso_my_quality, BR.myQuality, mVM::setMyQuality, mVM::getMyQuality);

        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        if (isEditingQSO()) {
            mDataRepository
                    .getQSO(mQSOid)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnError((e) -> Timber.e(e, "Unable to load QSO id %d", mQSOid))
                    .subscribe(mVM::setQSO);
        } else {
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity()
                    .findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getResources().getString(R.string.title_qso_new));
            }

            mVM.setMyStation(mPrefs.getString(PREF_LAST_USED_STATION, ""));
        }

        return binding.getRoot();
    }

    private void bindDateTimeFragment(@IdRes int fragmentId, int brID,
                                      Consumer<DateTime> setter,
                                      Supplier<DateTime> getter) {
        FragmentManager fm = getChildFragmentManager();
        DateTimePicker dateTimePicker = (DateTimePicker)
                fm.findFragmentById(fragmentId);

        dateTimePicker.onDateTimePickerChangeListener()
                .compose(bindToLifecycle())
                .subscribe(setter);

        mVM.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int id) {
                if (brID == id || BR._all == id) {
                    dateTimePicker.setDateTime(getter.get());
                }
            }
        });
    }

    private void bindSignalQualityPicker(@IdRes int fragmentId, int brId,
                                         Consumer<String> setter,
                                         Supplier<String> getter) {
        FragmentManager fm = getChildFragmentManager();

        SignalQualityPicker signalQualityPicker = (SignalQualityPicker) fm.findFragmentById(fragmentId);

        signalQualityPicker.onSignalQualityChange()
                .compose(bindToLifecycle())
                .subscribe(setter);

        mVM.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int id) {
                if (id == brId || id == BR._all) {
                    signalQualityPicker.setQuality(getter.get());
                }
            }
        });
    }

    private void bindHamLocationPicker(@IdRes int fragmentId, int brID,
                                       Consumer<VelesLocation> setter,
                                       Supplier<VelesLocation> getter) {
        FragmentManager fm = getChildFragmentManager();

        HamLocationPicker hamLocationPicker =
                (HamLocationPicker) fm.findFragmentById(fragmentId);

        hamLocationPicker.onLocationChange()
                .compose(bindToLifecycle())
                .subscribe((optionalLocation) -> setter.accept(optionalLocation.orElse(null)));

        mVM.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int id) {
                if (id == brID || id == BR._all) {
                    hamLocationPicker.setLocation(getter.get());
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.qso_edit_menu, menu);
        menu.findItem(R.id.action_delete).setVisible(isEditingQSO());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (isEditingQSO()) {
                    mDataRepository.updateQSO(mVM.getQSO());
                } else {
                    mDataRepository.addQSO(mVM.getQSO());

                    mPrefs.edit()
                            .putString(PREF_LAST_USED_STATION, mVM.getMyStation())
                            .apply();
                }
                mCallback.onFinishEdit();
                return true;
            case R.id.action_delete:
                mDataRepository.deleteQSO(mQSOid);
                Toast.makeText(getActivity(), R.string.toast_deleted, Toast.LENGTH_LONG).show();
                mCallback.onFinishDelete();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isEditingQSO() {
        return mQSOid != -1;
    }

    @Override
    public long getQSOId() {
        if (getArguments() != null) {
            return getArguments().getLong(ARG_QSO_ID);
        }
        return mQSOid;
    }
}
