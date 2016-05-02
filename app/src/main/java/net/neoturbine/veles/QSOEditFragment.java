package net.neoturbine.veles;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QSOEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QSOEditFragment extends Fragment {
    private static final String ARG_QSO_ID = "qso_id";

    private long mQSOid;

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
        return inflater.inflate(R.layout.qso_edit, container, false);
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
