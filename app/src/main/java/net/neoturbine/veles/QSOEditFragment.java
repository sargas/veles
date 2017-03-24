package net.neoturbine.veles;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import net.danlew.android.joda.JodaTimeAndroid;

import org.apache.commons.lang3.SerializationUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QSOEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QSOEditFragment extends Fragment implements QSOIdContainer {
    private static final String ARG_QSO_ID = "qso_id";
    private static final String PREF_LAST_USED_STATION = "PREF_LAST_USED_STATION";

    private long mQSOid = -1;

    private OnFinishEditListener mCallback;

    private static final int QSO_LOADER = 0;

    private final Map<TextView, String> mTextBoxes = new HashMap<>(4);
    private final Map<EditTextWithUnitsView, String> mTextBoxWithUnits = new HashMap<>(3);
    private final Map<HamLocationPicker, String> mLocationPickers = new HashMap<>(2);
    private final Map<DateTimePicker, String> mDatetimePickers = new HashMap<>(2);
    private final Map<SignalQualityPicker, String> mSignalQualityPickers = new HashMap<>(2);
    private TextView mMyStation;
    private SharedPreferences mPrefs;

    @SuppressWarnings("WeakerAccess")
    public QSOEditFragment() {
        // Required empty public constructor
    }

    interface OnFinishEditListener {
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
    static QSOEditFragment newInstance(long qso_id) {
        QSOEditFragment fragment = new QSOEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_QSO_ID, qso_id);
        fragment.setArguments(args);
        return fragment;
    }

    static QSOEditFragment newInstance() {
        return new QSOEditFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(getActivity());
        if (getArguments() != null) {
            mQSOid = getArguments().getLong(ARG_QSO_ID);
        }
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.qso_edit, container, false);

        AutoCompleteTextView modeView = (AutoCompleteTextView) rootView.findViewById(R.id.qso_mode);
        modeView.setAdapter(new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.qso_modes)));

        mMyStation = (TextView) rootView.findViewById(R.id.qso_my_station);

        FragmentManager fm = getChildFragmentManager();

        mLocationPickers.put(
                (HamLocationPicker) fm.findFragmentById(R.id.qso_my_location),
                QSOColumns.MY_LOCATION);
        mLocationPickers.put(
                (HamLocationPicker) fm.findFragmentById(R.id.qso_other_location),
                QSOColumns.OTHER_LOCATION);

        mTextBoxWithUnits.put((EditTextWithUnitsView) rootView.findViewById(R.id.qso_tx_freq),
                QSOColumns.TRANSMISSION_FREQUENCY);
        mTextBoxWithUnits.put((EditTextWithUnitsView) rootView.findViewById(R.id.qso_rx_freq),
                QSOColumns.RECEIVE_FREQUENCY);
        mTextBoxWithUnits.put((EditTextWithUnitsView) rootView.findViewById(R.id.qso_power),
                QSOColumns.POWER);

        mTextBoxes.put((TextView) rootView.findViewById(R.id.qso_station), QSOColumns.OTHER_STATION);
        mTextBoxes.put(mMyStation, QSOColumns.MY_STATION);
        mTextBoxes.put((TextView) rootView.findViewById(R.id.qso_mode), QSOColumns.MODE);
        mTextBoxes.put((TextView) rootView.findViewById(R.id.qso_comment), QSOColumns.COMMENT);

        mDatetimePickers.put(
                (DateTimePicker) fm.findFragmentById(R.id.qso_start_time),
                QSOColumns.START_TIME);
        mDatetimePickers.put(
                (DateTimePicker) fm.findFragmentById(R.id.qso_end_time),
                QSOColumns.END_TIME);

        mSignalQualityPickers.put(
                (SignalQualityPicker) fm.findFragmentById(R.id.qso_my_quality),
                QSOColumns.MY_QUALITY);
        mSignalQualityPickers.put(
                (SignalQualityPicker) fm.findFragmentById(R.id.qso_other_quality),
                QSOColumns.OTHER_QUALITY);

        mPrefs = getActivity().getPreferences(Context.MODE_PRIVATE);

        if (isEditingQSO()) {
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

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    if (!data.moveToFirst()) {
                        return;
                    }

                    for (Map.Entry<TextView, String> entry : mTextBoxes.entrySet()) {
                        entry.getKey().setText(data.getString(
                                data.getColumnIndexOrThrow(entry.getValue())));
                    }

                    for (Map.Entry<EditTextWithUnitsView, String> entry : mTextBoxWithUnits.entrySet()) {
                        entry.getKey().setValue(
                                data.getString(data.getColumnIndexOrThrow(entry.getValue())));
                    }

                    for (Map.Entry<DateTimePicker, String> entry : mDatetimePickers.entrySet()) {
                        entry.getKey().setDateTime(SerializationUtils.<DateTime>deserialize(
                                data.getBlob(data.getColumnIndexOrThrow(entry.getValue()))));
                    }

                    for (Map.Entry<HamLocationPicker, String> entry : mLocationPickers.entrySet()) {
                        entry.getKey().setLocation(SerializationUtils.<VelesLocation>deserialize(
                                data.getBlob(data.getColumnIndexOrThrow(entry.getValue()))));
                    }

                    for (Map.Entry<SignalQualityPicker, String> entry : mSignalQualityPickers.entrySet()) {
                        entry.getKey().setQuality(
                                data.getString(data.getColumnIndexOrThrow(entry.getValue())));
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                }
            });
        } else {
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity()
                    .findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(getResources().getString(R.string.title_qso_new));
            }

            mMyStation.setText(mPrefs.getString(PREF_LAST_USED_STATION, ""));

        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.qso_edit_menu, menu);
        menu.findItem(R.id.action_delete).setVisible(isEditingQSO());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                final ContentValues mNewValues = new ContentValues();

                for (Map.Entry<TextView, String> entry : mTextBoxes.entrySet()) {
                    mNewValues.put(entry.getValue(), entry.getKey().getText().toString());
                }

                for (Map.Entry<EditTextWithUnitsView, String> entry : mTextBoxWithUnits.entrySet()) {
                    mNewValues.put(entry.getValue(), entry.getKey().getValueAsString());
                }

                for (Map.Entry<DateTimePicker, String> entry : mDatetimePickers.entrySet()) {
                    mNewValues.put(entry.getValue(),
                            SerializationUtils.serialize(entry.getKey().getDateTime()));

                    if (entry.getValue().equals(QSOColumns.START_TIME))
                        mNewValues.put(QSOColumns.UTC_START_TIME,
                                entry.getKey().getDateTime().withZone(DateTimeZone.UTC).getMillis());
                }

                for (Map.Entry<HamLocationPicker, String> entry : mLocationPickers.entrySet()) {
                    mNewValues.put(entry.getValue(),
                            SerializationUtils.serialize(entry.getKey().getLocation()));
                }

                for (Map.Entry<SignalQualityPicker, String> entry : mSignalQualityPickers.entrySet()) {
                    mNewValues.put(entry.getValue(), entry.getKey().getQuality());
                }

                if (isEditingQSO()) {
                    getActivity().getContentResolver().update(
                            ContentUris.withAppendedId(QSOColumns.CONTENT_URI, mQSOid),
                            mNewValues, null, null
                    );
                } else {
                    getActivity().getContentResolver().insert(
                            QSOColumns.CONTENT_URI,
                            mNewValues
                    );

                    mPrefs.edit()
                            .putString(PREF_LAST_USED_STATION, mMyStation.getText().toString())
                            .apply();
                }
                mCallback.onFinishEdit();
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
