package net.neoturbine.veles.qso.list;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import net.neoturbine.veles.QSO;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class QSOListViewModel extends BaseObservable {
    private ListContracts.View mView;
    private final QSOAdapter mAdapter;

    @Inject
    QSOListViewModel(QSOAdapter adapter) {
        mAdapter = adapter;
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
    public QSOAdapter getAdapter() {
        return mAdapter;
    }

    void setQSOs(List<QSO> newList) {
        mAdapter.changeList(newList);
        notifyChange();
    }

    public void launchAddQSO(@SuppressWarnings("unused") View ignored) {
        mView.launchAddQSO();
    }

    Observable<Long> onClickQSO() {
        return mAdapter.onClick();
    }
}
