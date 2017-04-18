package net.neoturbine.veles.qso.list;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.databinding.QsoListContentBinding;

import java.util.List;

import javax.inject.Inject;

public class QSOAdapter
        extends RecyclerView.Adapter<QSOItemViewHolder> {

    private List<QSO> mQSOs = null;
    @Nullable
    private Callback mCallback;

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
    public void onBindViewHolder(final QSOItemViewHolder holder, int position) {
        QSO qso = mQSOs.get(position);
        holder.bind(qso);

        if (mCallback != null)
            holder.itemView.setOnClickListener((e) ->
                    mCallback.openID(getItemId(holder.getAdapterPosition()))
            );
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

    public void changeList(List<QSO> objects) {
        mQSOs = objects;
        notifyDataSetChanged();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void openID(long id);
    }
}
