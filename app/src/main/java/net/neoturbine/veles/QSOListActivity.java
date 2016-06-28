package net.neoturbine.veles;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * An activity representing a list of QSOs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link QSODetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class QSOListActivity extends AppCompatActivity
        implements QSODetailFragment.onQSODetailListener, QSOEditFragment.OnFinishEditListener {

    private static final String QSO_LIST_DETAIL_TAG = "QSO_LIST_DETAIL_TAG";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @VisibleForTesting
    final SimpleItemRecyclerViewAdapter mAdapter = new SimpleItemRecyclerViewAdapter();

    private static final int QSO_LOADER = 0;
    private RecyclerView mRecyclerView;
    private TextView mEmptyListLink;
    private TextView mEmptyListMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qso_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.qso_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View.OnClickListener startAddActivity = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTwoPane) {
                    switchFragment(QSOEditFragment.newInstance());
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, QSOEditActivity.class);
                    context.startActivity(intent);
                }
            }
        };

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(startAddActivity);

        mEmptyListMessage = (TextView) findViewById(R.id.empty_list_message);
        mEmptyListLink = (TextView) findViewById(R.id.empty_list_link);
        assert mEmptyListLink != null;
        mEmptyListLink.setOnClickListener(startAddActivity);
        mEmptyListLink.setMovementMethod(LinkMovementMethod.getInstance());

        mRecyclerView = (RecyclerView) findViewById(R.id.qso_list);
        assert mRecyclerView != null;
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider().positionInsideItem(true).build());

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                displayOrHideEmptyView();
            }
        });
        displayOrHideEmptyView();

        getLoaderManager().initLoader(QSO_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                switch (id) {
                    case QSO_LOADER:
                        return new CursorLoader(getApplicationContext(), QSOColumns.CONTENT_URI, null,
                                null, null, QSOColumns.START_TIME + " DESC");
                    default:
                        throw new IllegalArgumentException("Unknown type of loader: " + id);
                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                mAdapter.changeCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                mAdapter.changeCursor(null);
            }
        });
    }

    @UiThread
    private void displayOrHideEmptyView() {
        if (mAdapter.getItemCount() > 0) {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyListMessage.setVisibility(View.GONE);
            mEmptyListLink.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyListMessage.setVisibility(View.VISIBLE);
            mEmptyListLink.setVisibility(View.VISIBLE);
        }
    }

    class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private Cursor mCursor = null;
        private boolean mValidData = false;

        SimpleItemRecyclerViewAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.qso_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            if (mValidData && mCursor.moveToPosition(position)) {
                holder.mStationView.setText(
                        mCursor.getString(mCursor.getColumnIndexOrThrow(QSOColumns.OTHER_STATION)));
                holder.mDateView.setText(
                        DateUtils.getRelativeTimeSpanString(
                                mCursor.getLong(mCursor.getColumnIndexOrThrow(QSOColumns.START_TIME))));
                holder.mModeView.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(QSOColumns.MODE)));
                holder.mFrequencyView.setText(
                        mCursor.getString(mCursor.getColumnIndexOrThrow(QSOColumns.TRANSMISSION_FREQUENCY)));
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        QSODetailFragment fragment = QSODetailFragment.newInstance(getItemId(holder.getAdapterPosition()));
                        switchFragment(fragment);
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, QSODetailActivity.class);
                        intent.putExtra(QSODetailActivity.ARG_QSO_ID, getItemId(holder.getAdapterPosition()));

                        context.startActivity(intent);
                    }
                }

            });
        }

        @Override
        public int getItemCount() {
            if (mValidData && mCursor != null) {
                return mCursor.getCount();
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (mValidData && mCursor != null && mCursor.moveToPosition(position)) {
                return mCursor.getLong(mCursor.getColumnIndexOrThrow(QSOColumns._ID));
            }
            return -1;
        }

        void changeCursor(Cursor newCursor) {
            if (newCursor == mCursor) {
                return;
            }

            final Cursor oldCursor = mCursor;
            mCursor = newCursor;

            if (mCursor != null) {
                mValidData = true;
                notifyDataSetChanged();
            } else {
                mValidData = false;
                notifyItemRangeRemoved(0, getItemCount());
            }

            if (oldCursor != null) {
                oldCursor.close();
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mStationView;
            final TextView mDateView;
            final TextView mFrequencyView;
            final TextView mModeView;
            final View mView;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mStationView = (TextView) view.findViewById(R.id.station_name);
                mDateView = (TextView) view.findViewById(R.id.date);
                mModeView = (TextView) view.findViewById(R.id.mode);
                mFrequencyView = (TextView) view.findViewById(R.id.frequency);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mDateView.getText() + "'";
            }
        }
    }

    @Override
    public void onFinishDelete() {
        switchFragment(null);
    }

    @Override
    public void onEditQSO(final long QSOid) {
        switchFragment(QSOEditFragment.newInstance(QSOid));
    }

    @Override
    public void onFinishEdit() {
        getFragmentManager().popBackStack();
    }

    @UiThread
    private void switchFragment(Fragment fragment) {
        Fragment currentFragment = getFragmentManager().findFragmentByTag(QSO_LIST_DETAIL_TAG);
        if (currentFragment != null && fragment != null
                && fragment.getClass().equals(currentFragment.getClass())
                && fragment instanceof QSOIdContainer && currentFragment instanceof QSOIdContainer
                && ((QSOIdContainer) fragment).getQSOId() == ((QSOIdContainer) currentFragment).getQSOId()) {
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction().addToBackStack(null);

        if (fragment != null)
            ft.replace(R.id.qso_detail_container, fragment, QSO_LIST_DETAIL_TAG);
        else
            ft.remove(currentFragment);

        ft.commit();
    }
}
