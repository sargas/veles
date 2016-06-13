package net.neoturbine.veles;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.CollapsingToolbarLayout;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.commons.lang3.SerializationUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QSOEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QSOEditFragment extends Fragment {
    private static final String ARG_QSO_ID = "qso_id";

    private long mQSOid = -1;
    private Calendar mStartTime;
    private Calendar mEndTime;

    private TextView mStartTimeButton;
    private TextView mStartDateButton;
    private TextView mEndTimeButton;
    private TextView mEndDateButton;
    private OnFinishEditListener mCallback;

    private static final int QSO_LOADER = 0;

    private static final List<Integer> mTextBoxIDs = Arrays.asList(
            R.id.qso_station, R.id.qso_mode,
            R.id.qso_comment
    );
    private static final List<String> mTextBoxColumns = Arrays.asList(
            QSOColumns.OTHER_STATION, QSOColumns.MODE,
            QSOColumns.COMMENT
    );
    private static final List<Integer> mTextBoxUnitsIDs = Arrays.asList(
            R.id.qso_tx_freq, R.id.qso_rx_freq, R.id.qso_power
    );
    private static final List<String> mTextBoxUnitsColumns = Arrays.asList(
            QSOColumns.TRANSMISSION_FREQUENCY, QSOColumns.RECEIVE_FREQUENCY,
            QSOColumns.POWER
    );
    private HamLocationPicker mMyLocation;

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

        mStartDateButton = (TextView) rootView.findViewById(R.id.qso_pick_start_date);
        mStartTimeButton = (TextView) rootView.findViewById(R.id.qso_pick_start_time);
        mEndDateButton = (TextView) rootView.findViewById(R.id.qso_pick_end_date);
        mEndTimeButton = (TextView) rootView.findViewById(R.id.qso_pick_end_time);

        mMyLocation = (HamLocationPicker) getChildFragmentManager().findFragmentById(R.id.qso_my_location);
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();

        if (mQSOid != -1) {
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

                    Iterator<Integer> editBoxIDs = mTextBoxIDs.iterator();
                    Iterator<String> tableColumns = mTextBoxColumns.iterator();

                    while (editBoxIDs.hasNext() && tableColumns.hasNext()) {
                        TextView tv = (TextView) getView().findViewById(editBoxIDs.next());
                        tv.setText(data.getString(data.getColumnIndexOrThrow(tableColumns.next())));
                    }

                    Iterator<Integer> unitBoxIDs = mTextBoxUnitsIDs.iterator();
                    Iterator<String> unitBoxColumns = mTextBoxUnitsColumns.iterator();

                    while (unitBoxIDs.hasNext() && unitBoxColumns.hasNext()) {
                        EditTextWithUnitsView etu = (EditTextWithUnitsView) getView()
                                .findViewById(unitBoxIDs.next());
                        etu.setValue(
                                data.getString(data.getColumnIndexOrThrow(unitBoxColumns.next())));
                    }

                    mStartTime.setTimeInMillis(
                            data.getLong(data.getColumnIndexOrThrow(QSOColumns.START_TIME)));
                    mEndTime.setTimeInMillis(
                            data.getLong(data.getColumnIndexOrThrow(QSOColumns.END_TIME)));


                    mMyLocation.setLocation(SerializationUtils.<VelesLocation>deserialize(
                            data.getBlob(data.getColumnIndexOrThrow(QSOColumns.MY_LOCATION))));

                    updateTimes();
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
            updateTimes();
        }

        bindTimeChangeButtons(mStartDateButton, mStartTimeButton, mStartTime);
        bindTimeChangeButtons(mEndDateButton, mEndTimeButton, mEndTime);

        return rootView;
    }

    @UiThread
    private void bindTimeChangeButtons(final View date_button, final View time_button,
                                       final Calendar calendar) {
        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(
                        getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                calendar.set(year, month, day);
                                updateTimes();
                            }
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show();
            }
        });

        time_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                updateTimes();
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(getActivity())
                ).show();
            }
        });
    }

    @UiThread
    private void updateTimes() {
        if (mStartTime != null) {
            mStartDateButton.setText(
                    DateFormat.getLongDateFormat(getActivity()).format(mStartTime.getTime()));
            mStartTimeButton.setText(
                    DateFormat.getTimeFormat(getActivity()).format(mStartTime.getTime()));
        }
        if (mEndTime != null) {
            mEndDateButton.setText(
                    DateFormat.getLongDateFormat(getActivity()).format(mEndTime.getTime()));
            mEndTimeButton.setText(
                    DateFormat.getTimeFormat(getActivity()).format(mEndTime.getTime()));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.qso_edit_menu, menu);
        menu.findItem(R.id.action_delete).setVisible(mQSOid != -1);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                final ContentValues mNewValues = new ContentValues();

                Iterator<Integer> editBoxIDs = mTextBoxIDs.iterator();
                Iterator<String> tableColumns = mTextBoxColumns.iterator();

                while (editBoxIDs.hasNext() && tableColumns.hasNext()) {
                    TextView tv = (TextView) getView().findViewById(editBoxIDs.next());
                    mNewValues.put(tableColumns.next(), tv.getText().toString());
                }

                Iterator<Integer> unitBoxIDs = mTextBoxUnitsIDs.iterator();
                Iterator<String> unitBoxColumns = mTextBoxUnitsColumns.iterator();

                while (unitBoxIDs.hasNext() && unitBoxColumns.hasNext()) {
                    EditTextWithUnitsView etu = (EditTextWithUnitsView) getView()
                            .findViewById(unitBoxIDs.next());
                    mNewValues.put(unitBoxColumns.next(), etu.getValueAsString());
                }

                mNewValues.put(QSOColumns.START_TIME, mStartTime.getTimeInMillis());
                mNewValues.put(QSOColumns.END_TIME, mEndTime.getTimeInMillis());

                mNewValues.put(QSOColumns.MY_LOCATION,
                        SerializationUtils.serialize(mMyLocation.getLocation()));

                if (mQSOid == -1) {
                    getActivity().getContentResolver().insert(
                            QSOColumns.CONTENT_URI,
                            mNewValues
                    );
                } else {
                    getActivity().getContentResolver().update(
                            ContentUris.withAppendedId(QSOColumns.CONTENT_URI, mQSOid),
                            mNewValues, null, null
                    );
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
}
