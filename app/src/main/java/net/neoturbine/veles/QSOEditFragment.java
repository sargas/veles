package net.neoturbine.veles;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

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

    private Button mStartTimeButton;
    private Button mStartDateButton;
    private Button mEndTimeButton;
    private Button mEndDateButton;
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

    @SuppressWarnings("WeakerAccess")
    public QSOEditFragment() {
        // Required empty public constructor
    }

    public interface OnFinishEditListener {
        void onFinishEdit();
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

    public static QSOEditFragment newInstance() {
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
                getContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.qso_modes)));

        mStartDateButton = (Button) rootView.findViewById(R.id.qso_pick_start_date);
        mStartTimeButton = (Button) rootView.findViewById(R.id.qso_pick_start_time);
        mEndDateButton = (Button) rootView.findViewById(R.id.qso_pick_end_date);
        mEndTimeButton = (Button) rootView.findViewById(R.id.qso_pick_end_time);
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();

        if (mQSOid != -1) {
            getLoaderManager().initLoader(QSO_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    switch (id) {
                        case QSO_LOADER:
                            return new CursorLoader(
                                    getContext(),
                                    ContentUris.withAppendedId(QSOColumns.CONTENT_URI, mQSOid),
                                    null, null, null, null);
                        default:
                            throw new IllegalArgumentException("Unknown type of loader: " + id);
                    }
                }

                @SuppressWarnings("ConstantConditions")
                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    if (data.getCount() == 0) {
                        return;
                    }
                    data.moveToFirst();

                    Iterator<Integer> editBoxIDs = mTextBoxIDs.iterator();
                    Iterator<String> tableColumns = mTextBoxColumns.iterator();

                    while (editBoxIDs.hasNext() && tableColumns.hasNext()) {
                        TextView tv = (TextView) getView().findViewById(editBoxIDs.next());
                        tv.setText(data.getString(data.getColumnIndexOrThrow(tableColumns.next())));
                    }

                    mStartTime.setTimeInMillis(
                            data.getLong(data.getColumnIndexOrThrow(QSOColumns.START_TIME)));
                    mEndTime.setTimeInMillis(
                            data.getLong(data.getColumnIndexOrThrow(QSOColumns.END_TIME)));

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
                        DateFormat.is24HourFormat(getContext())
                ).show();
            }
        });
    }

    @UiThread
    private void updateTimes() {
        if (mStartTime != null) {
            mStartDateButton.setText(
                    DateFormat.getLongDateFormat(getContext()).format(mStartTime.getTime()));
            mStartTimeButton.setText(
                    DateFormat.getTimeFormat(getContext()).format(mStartTime.getTime()));
        }
        if (mEndTime != null) {
            mEndDateButton.setText(
                    DateFormat.getLongDateFormat(getContext()).format(mEndTime.getTime()));
            mEndTimeButton.setText(
                    DateFormat.getTimeFormat(getContext()).format(mEndTime.getTime()));
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

                mNewValues.put(QSOColumns.START_TIME, mStartTime.getTimeInMillis());
                mNewValues.put(QSOColumns.END_TIME, mEndTime.getTimeInMillis());

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
                //TODO Show SnackBar
                mCallback.onFinishEdit();
                return true;
            case R.id.action_delete:
                getActivity().getContentResolver().delete(
                        ContentUris.withAppendedId(QSOColumns.CONTENT_URI, mQSOid),
                        null, null
                );
                //TODO Show SnackBar + undo action
                mCallback.onFinishEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
