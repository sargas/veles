package net.neoturbine.veles;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * An activity representing a list of QSOs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link QSODetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class QSOListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private final SimpleItemRecyclerViewAdapter mAdapter = new SimpleItemRecyclerViewAdapter();

    private static final int QSO_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qso_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        View recyclerView = findViewById(R.id.qso_list);
        assert recyclerView != null;
        ((RecyclerView) recyclerView).setAdapter(mAdapter);

        if (findViewById(R.id.qso_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        getLoaderManager().initLoader(QSO_LOADER, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                switch (id) {
                    case QSO_LOADER:
                        return new CursorLoader(getApplicationContext(), QSOColumns.CONTENT_URI, null,
                                null, null, QSOColumns.START_TIME);
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

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private Cursor mCursor = null;
        private DataSetObserver mDataSetObserver = new SimpleDataSetObserver();
        private boolean mValidData = false;

        public SimpleItemRecyclerViewAdapter() {
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
                holder.mIdView.setText(mCursor.getInt(mCursor.getColumnIndexOrThrow(QSOColumns._ID)));
                holder.mContentView.setText(mCursor.getInt(mCursor.getColumnIndexOrThrow(QSOColumns.COMMENT)));
            }
            /*holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(QSODetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        QSODetailFragment fragment = new QSODetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.qso_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, QSODetailActivity.class);
                        intent.putExtra(QSODetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });*/
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

        public void changeCursor(Cursor newCursor) {
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }

        private class SimpleDataSetObserver extends DataSetObserver {
            @Override
            public void onChanged() {
                super.onChanged();
                mValidData = true;
                notifyDataSetChanged();
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                mValidData = false;
                notifyDataSetChanged();
            }
        }
    }
}
