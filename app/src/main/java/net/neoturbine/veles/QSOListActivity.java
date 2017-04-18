package net.neoturbine.veles;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;

import android.view.View;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import net.danlew.android.joda.JodaTimeAndroid;
import net.neoturbine.veles.qso.data.DataRepository;
import net.neoturbine.veles.qso.detail.QSODetailActivity;
import net.neoturbine.veles.qso.detail.QSODetailFragment;
import net.neoturbine.veles.qso.list.QSOAdapter;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import io.reactivex.android.schedulers.AndroidSchedulers;


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

    @Inject
    QSOAdapter mAdapter;

    @Inject
    DataRepository mDataRepository;

    private RecyclerView mRecyclerView;
    private TextView mEmptyListLink;
    private TextView mEmptyListMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        JodaTimeAndroid.init(this);
        DataBindingUtil.setContentView(this, R.layout.activity_qso_list);

        setupToolbar();
        setupTwoPane();

        View.OnClickListener addQSO = getOnClickToAddQSO();
        setupFAB(addQSO);
        setupEmptyListViews(addQSO);

        setupList();
        setupAdapter();
        mDataRepository.getAllQSO()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mAdapter::changeList);
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
        mAdapter.setCallback(this::openID);
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

    public void openID(long id) {
        if (mTwoPane) {
            QSODetailFragment fragment = QSODetailFragment.newInstance(id);
            switchFragment(fragment);
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
}
