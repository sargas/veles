package net.neoturbine.veles.qso.detail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import net.neoturbine.veles.QSOEditActivity;
import net.neoturbine.veles.qso.list.QSOListActivity;
import net.neoturbine.veles.R;

/**
 * An activity representing a single QSO detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link QSOListActivity}.
 */
public class QSODetailActivity extends AppCompatActivity
        implements QSODetailFragment.onQSODetailListener {
    public static final String ARG_QSO_ID = "qso_id";
    private static final int REQUEST_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qso_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            QSODetailFragment fragment = QSODetailFragment.newInstance(
                    getIntent().getLongExtra(ARG_QSO_ID, 0)
            );
            getFragmentManager().beginTransaction()
                    .add(R.id.qso_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, QSOListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishDelete() {
        finish();
    }

    @Override
    public void onEditQSO(final long QSOid) {
        Intent intent = new Intent(this, QSOEditActivity.class);
        intent.putExtra(QSOEditActivity.ARG_QSO_ID, QSOid);

        startActivityForResult(intent, REQUEST_EDIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT:
                if (resultCode == QSOEditActivity.RESULT_DELETED) {
                    onFinishDelete();
                }
                break;
        }
    }
}
