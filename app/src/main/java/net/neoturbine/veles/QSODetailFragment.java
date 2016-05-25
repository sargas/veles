package net.neoturbine.veles;

import android.content.ContentUris;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QSODetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
                                    getContext(),
                                    ContentUris.withAppendedId(QSOColumns.CONTENT_URI, mQSOid),
                                    null, null, null, null);
                        default:
                            throw new IllegalArgumentException("Unknown type of loader: " + id);
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                    data.moveToFirst();
                    QsoDetailBinding binding = DataBindingUtil.getBinding(getView());
                    assert binding != null;
                    binding.setQso(new QSO(data));
                    binding.setStartTime(DateUtils.formatDateTime(
                            getContext(),
                            data.getLong(data.getColumnIndexOrThrow(QSOColumns.START_TIME)),
                            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR));
                    binding.setEndTime(DateUtils.formatDateTime(
                            getContext(),
                            data.getLong(data.getColumnIndexOrThrow(QSOColumns.END_TIME)),
                            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR));

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

        return rootView;
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
}
