package net.neoturbine.veles.qso.list;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.qso.data.DataRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class QSOListViewModel extends BaseObservable {
    private ListContracts.View mView;
    private final QSOAdapter mAdapter;
    private final DataRepository mDataRepository;
    private Disposable mClickSubscriber;
    private Disposable mDatabaseSubscriber;

    @Inject
    QSOListViewModel(QSOAdapter adapter, DataRepository dataRepository) {
        mAdapter = adapter;
        mDataRepository = dataRepository;
    }

    void showUI() {
        if (isNullOrDisposed(mDatabaseSubscriber))
            mDatabaseSubscriber = mDataRepository
                    .getAllQSO()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::setQSOs);

        if (isNullOrDisposed(mClickSubscriber))
            mClickSubscriber = mAdapter
                    .onClick()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mView::openID);
    }

    void stopUI() {
        if (!isNullOrDisposed(mDatabaseSubscriber))
            mDatabaseSubscriber.dispose();

        if (!isNullOrDisposed(mClickSubscriber))
            mClickSubscriber.dispose();
    }

    private boolean isNullOrDisposed(Disposable disposable) {
        return disposable == null || disposable.isDisposed();
    }

    private boolean isEmptyList() {
        return mAdapter.getItemCount() == 0;
    }

    void bindView(ListContracts.View view) {
        mView = view;
    }

    @Bindable
    public int getListVisibility() {
        return isEmptyList() ? View.GONE : View.VISIBLE;
    }

    @Bindable
    public int getMissingQSOVisibility() {
        return isEmptyList() ? View.VISIBLE : View.GONE;
    }

    @Bindable
    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    private void setQSOs(List<QSO> newList) {
        mAdapter.changeList(newList);
        notifyChange();
    }

    public void launchAddQSO(@SuppressWarnings("unused") View ignored) {
        mView.launchAddQSO();
    }
}
