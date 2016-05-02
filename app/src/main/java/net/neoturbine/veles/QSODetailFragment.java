package net.neoturbine.veles;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private String mQSOInfo;

    private static final int QSO_LOADER = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QSODetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_QSO_ID)) {
            mQSOid = getArguments().getLong(ARG_QSO_ID);

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
                    mQSOInfo = DatabaseUtils.dumpCursorToString(data);

                    View rootView = getActivity().findViewById(R.id.qso_detail);

                    ((TextView) rootView.findViewById(R.id.qso_detail)).setText(mQSOInfo);

                    CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
                    if (appBarLayout != null) {
                        appBarLayout.setTitle(data.getString(data.getColumnIndexOrThrow(QSOColumns.OTHER_STATION)));
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.qso_detail, container, false);

        if (mQSOid != -1) {
            ((TextView) rootView.findViewById(R.id.qso_detail)).setText(mQSOInfo);
        }

        return rootView;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param qso_id Parameter 1.
     * @return A new instance of fragment QSOEditFragment.
     */
    public static QSODetailFragment newInstance(long qso_id) {
        QSODetailFragment fragment = new QSODetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_QSO_ID, qso_id);
        fragment.setArguments(args);
        return fragment;
    }
}
