package net.neoturbine.veles.qso.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jakewharton.rxbinding2.view.RxView;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.databinding.QsoListContentBinding;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

class QSOAdapter
        extends RecyclerView.Adapter<QSOItemViewHolder> {

    private List<QSO> mQSOs = null;
    @NonNull
    private final PublishSubject<Long> mClicks = PublishSubject.create();

    @Inject
    QSOAdapter() {
    }

    @Override
    public QSOItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        QsoListContentBinding binding =
                QsoListContentBinding.inflate(inflater, parent, false);
        return new QSOItemViewHolder(binding, new QSOItemViewModel(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final QSOItemViewHolder holder, final int position) {
        QSO qso = mQSOs.get(position);
        holder.bind(qso);

        RxView.clicks(holder.itemView)
                .map((i) -> getItemId(position))
                .subscribe(mClicks);
    }

    @Override
    public int getItemCount() {
        if (mQSOs != null) {
            return mQSOs.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (hasRowAtPosition(position)) {
            return mQSOs.get(position).getID();
        }
        return -1;
    }

    private boolean hasRowAtPosition(int position) {
        return mQSOs != null && position < mQSOs.size();
    }

    void changeList(List<QSO> objects) {
        mQSOs = objects;
        notifyDataSetChanged();
    }

    Observable<Long> onClick() {
        return mClicks;
    }
}
