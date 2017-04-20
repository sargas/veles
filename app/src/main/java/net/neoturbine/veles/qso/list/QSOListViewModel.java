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
    private final List<Disposable> mDisposables = new ArrayList<>();

    @Inject
    QSOListViewModel(QSOAdapter adapter, DataRepository dataRepository) {
        mAdapter = adapter;
        mDataRepository = dataRepository;
    }

    public void showUI() {
        mDisposables.add(
                mDataRepository
                        .getAllQSO()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::setQSOs));
        mAdapter.onClick().subscribe(mView::openID);
    }

    public void stopUI() {
        for (Disposable subscription : mDisposables)
            if (subscription.isDisposed())
                subscription.dispose();
        mDisposables.clear();
    }

    private boolean isEmptyList() {
        return mAdapter.getItemCount() == 0;
    }

    public void bindView(ListContracts.View view) {
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
