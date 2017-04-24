package net.neoturbine.veles.qso.list;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.QSOEditActivity;
import net.neoturbine.veles.QSOEditFragment;
import net.neoturbine.veles.QSOIdContainer;
import net.neoturbine.veles.R;
import net.neoturbine.veles.databinding.ActivityQsoListBinding;
import net.neoturbine.veles.qso.detail.QSODetailActivity;
import net.neoturbine.veles.qso.detail.QSODetailFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


/**
 * An activity representing a list of QSOs. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link QSODetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class QSOListActivity extends Activity
        implements QSODetailFragment.QSODetailFragmentParentListener, QSOEditFragment.OnFinishEditListener, ListContracts.View {

    private static final String QSO_LIST_DETAIL_TAG = "QSO_LIST_DETAIL_TAG";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @SuppressWarnings("WeakerAccess")
    @Inject
    QSOListViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);

        ActivityQsoListBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_qso_list);

        mViewModel.bindView(this);
        binding.setViewmodel(mViewModel);
        setActionBar(binding.toolbar);

        setupTwoPane();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.showUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewModel.stopUI();
    }

    public void launchAddQSO() {
        if (mTwoPane) {
            switchFragment(QSOEditFragment.newInstance());
        } else {
            Intent intent = new Intent(this, QSOEditActivity.class);
            startActivity(intent);
        }
    }

    private void setupTwoPane() {
        mTwoPane = !isWideLayout();
    }

    private boolean isWideLayout() {
        return findViewById(R.id.qso_detail_container) == null;
    }

    @BindingAdapter("itemDividerDecoration")
    public static void setDividerDecoration(RecyclerView recyclerView, int orientation) {
        recyclerView.addItemDecoration(
                new DividerItemDecoration(recyclerView.getContext(), orientation));
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

    @Override
    public void setQSOTitle(CharSequence title) {
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setTitle(
                getString(R.string.app_name) + " - " + title);
    }

    public void openID(long id) {
        if (mTwoPane) {
            switchFragment(QSODetailFragment.newInstance(id));
        } else {
            Intent intent = new Intent(this, QSODetailActivity.class);
            intent.putExtra(QSODetailActivity.ARG_QSO_ID, id);

            startActivity(intent);
        }
    }

    @UiThread
    private void switchFragment(Fragment fragment) {
        Fragment currentFragment = getFragmentManager().findFragmentByTag(QSO_LIST_DETAIL_TAG);
        if (isEqualQSO(fragment, currentFragment)) {
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();

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
}
