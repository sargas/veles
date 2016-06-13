package net.neoturbine.veles;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * An activity representing a single QSO detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link QSOListActivity}.
 */
public class QSOEditActivity extends AppCompatActivity
        implements QSOEditFragment.OnFinishEditListener {
    static final String ARG_QSO_ID = "qso_id";
    static final int RESULT_DELETED = RESULT_FIRST_USER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qso_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_toolbar);
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
            QSOEditFragment fragment;
            if (getIntent().hasExtra(ARG_QSO_ID)) {
                fragment = QSOEditFragment.newInstance(
                        getIntent().getLongExtra(ARG_QSO_ID, -1)
                );
            } else {
                fragment = QSOEditFragment.newInstance();
            }
            getFragmentManager().beginTransaction()
                    .add(R.id.qso_edit_container, fragment)
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
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishEdit() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onFinishDelete() {
        setResult(RESULT_DELETED);
        finish();
    }
}
