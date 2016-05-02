package net.neoturbine.veles;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QSOEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QSOEditFragment extends Fragment {
    private static final String ARG_QSO_ID = "qso_id";

    private long mQSOid = -1;
    private static final int QSO_LOADER = 0;

    public QSOEditFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.qso_edit, container, false);

        AutoCompleteTextView modeView = (AutoCompleteTextView) rootView.findViewById(R.id.qso_mode);
        modeView.setAdapter(new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.qso_modes)));

        if (mQSOid != -1) {
            getLoaderManager().initLoader(QSO_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    switch (id) {
                        case QSO_LOADER:
                            return new CursorLoader(
                                    getContext(),
                                    QSOColumns.CONTENT_URI,
                                    null,
                                    QSOColumns._ID + "=?",
                                    new String[]{Long.toString(mQSOid)},
                                    null);
                        default:
                            throw new IllegalArgumentException("Unknown type of loader: " + id);
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    if (data.getCount() == 0) {
                        return;
                    }
                    data.moveToFirst();

                    Iterator<Integer> editBoxIDs = Arrays.asList(new Integer[]{
                            R.id.qso_start_time, R.id.qso_end_time,
                            R.id.qso_station, R.id.qso_mode
                    }).iterator();
                    Iterator<String> tableColumns = Arrays.asList(new String[]{
                            QSOColumns.START_TIME, QSOColumns.END_TIME,
                            QSOColumns.OTHER_STATION, QSOColumns.MODE
                    }).iterator();

                    while (editBoxIDs.hasNext() && tableColumns.hasNext()) {
                        @SuppressWarnings("ConstantConditions") TextView tv =
                                (TextView) getView().findViewById(editBoxIDs.next());
                        tv.setText(data.getString(data.getColumnIndexOrThrow(tableColumns.next())));
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                }
            });
        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.qso_edit_menu, menu);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                final ContentValues mNewValues = new ContentValues();

                TextView start_time = (TextView) getView().findViewById(R.id.qso_start_time);
                mNewValues.put(QSOColumns.START_TIME, start_time.getText().toString());

                TextView end_time = (TextView) getView().findViewById(R.id.qso_end_time);
                mNewValues.put(QSOColumns.END_TIME, end_time.getText().toString());

                TextView station = (TextView) getView().findViewById(R.id.qso_station);
                mNewValues.put(QSOColumns.OTHER_STATION, station.getText().toString());

                TextView mode = (TextView) getView().findViewById(R.id.qso_mode);
                mNewValues.put(QSOColumns.MODE, mode.getText().toString());

                getActivity().getContentResolver().insert(
                        QSOColumns.CONTENT_URI,
                        mNewValues
                );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
