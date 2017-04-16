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
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.danlew.android.joda.DateUtils;
import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.qso.detail.QSODetailActivity;
import net.neoturbine.veles.qso.detail.QSODetailFragment;

import org.joda.time.DateTime;


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
        JodaTimeAndroid.init(this);
        setContentView(R.layout.activity_qso_list);

        setupToolbar();
        setupTwoPane();

        View.OnClickListener addQSO = getOnClickToAddQSO();
        setupFAB(addQSO);
        setupEmptyListViews(addQSO);

        setupList();
        setupAdapter();
        getLoaderManager().initLoader(QSO_LOADER, null, new QSOCursorLoader());
        displayOrHideEmptyView();
    }

    private void setupAdapter() {
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                displayOrHideEmptyView();
            }
        });
    }

    @NonNull
    private View.OnClickListener getOnClickToAddQSO() {
        return view -> {
            if (mTwoPane) {
                switchFragment(QSOEditFragment.newInstance());
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, QSOEditActivity.class);
                context.startActivity(intent);
            }
        };
    }

    private void setupTwoPane() {
        mTwoPane = !isWideLayout();
    }

    private boolean isWideLayout() {
        return findViewById(R.id.qso_detail_container) == null;
    }

    private void setupList() {
        mRecyclerView = (RecyclerView) findViewById(R.id.qso_list);
        assert mRecyclerView != null;
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this)
                .showLastDivider().positionInsideItem(true).build());
    }

    private void setupEmptyListViews(View.OnClickListener addQSO) {
        mEmptyListMessage = (TextView) findViewById(R.id.empty_list_message);
        mEmptyListLink = (TextView) findViewById(R.id.empty_list_link);
        assert mEmptyListLink != null;
        mEmptyListLink.setOnClickListener(addQSO);
        mEmptyListLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setupFAB(View.OnClickListener addQSO) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(addQSO);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }

    @UiThread
    private void displayOrHideEmptyView() {
        if (isEmptyList()) {
            hideListShowViews();
        } else {
            showListHideViews();
        }
    }

    private boolean isEmptyList() {
        return mAdapter.getItemCount() == 0;
    }

    private void hideListShowViews() {
        mRecyclerView.setVisibility(View.GONE);
        mEmptyListMessage.setVisibility(View.VISIBLE);
        mEmptyListLink.setVisibility(View.VISIBLE);
    }

    private void showListHideViews() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mEmptyListMessage.setVisibility(View.GONE);
        mEmptyListLink.setVisibility(View.GONE);
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
            if (hasRowAtPosition(position)) {
                QSO qso = new QSO(mCursor);
                holder.mStationView.setText(qso.getOtherStation());

                DateTime startTime = qso.getStartTime();
                holder.mDateView.setText(DateUtils.getRelativeTimeSpanString(
                        QSOListActivity.this, startTime));
                holder.mModeView.setText(qso.getMode());
                holder.mFrequencyView.setText(qso.getTransmissionFrequency());
            }

            holder.mView.setOnClickListener(v -> {
                if (mTwoPane) {
                    QSODetailFragment fragment = QSODetailFragment.newInstance(getItemId(holder.getAdapterPosition()));
                    switchFragment(fragment);
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, QSODetailActivity.class);
                    intent.putExtra(QSODetailActivity.ARG_QSO_ID, getItemId(holder.getAdapterPosition()));

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (mValidData) {
                return mCursor.getCount();
            }
            return 0;
        }

        @Override
        public long getItemId(int position) {
            if (hasRowAtPosition(position)) {
                return mCursor.getLong(mCursor.getColumnIndexOrThrow(QSOColumns._ID));
            }
            return -1;
        }

        private boolean hasRowAtPosition(int position) {
            return mValidData && mCursor.moveToPosition(position);
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
        if (isEqualQSO(fragment, currentFragment)) {
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction().addToBackStack(null);

        if (fragment != null)
            ft.replace(R.id.qso_detail_container, fragment, QSO_LIST_DETAIL_TAG);
        else
            ft.remove(currentFragment);

        ft.commit();
    }

    private boolean isEqualQSO(Fragment fragmentA, Fragment fragmentB) {
        return fragmentB != null && fragmentA != null
                && fragmentA.getClass().equals(fragmentB.getClass())
                && fragmentA instanceof QSOIdContainer && fragmentB instanceof QSOIdContainer
                && ((QSOIdContainer) fragmentA).getQSOId() == ((QSOIdContainer) fragmentB).getQSOId();
    }

    private class QSOCursorLoader implements LoaderManager.LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id != QSO_LOADER)
                throw new IllegalArgumentException("Unknown type of loader: " + id);

            return new CursorLoader(getApplicationContext(), QSOColumns.CONTENT_URI, null,
                    null, null, QSOColumns.UTC_START_TIME + " DESC");
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mAdapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mAdapter.changeCursor(null);
        }
    }
}
