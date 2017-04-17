package net.neoturbine.veles.qso.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.danlew.android.joda.DateUtils;
import net.neoturbine.veles.QSO;
import net.neoturbine.veles.QSOListActivity;
import net.neoturbine.veles.R;

import org.joda.time.DateTime;

import java.util.List;

public class QSOAdapter
        extends RecyclerView.Adapter<ViewHolder> {

    private final QSOListActivity qsoListActivity;
    private List<QSO> mQSOs = null;

    public QSOAdapter(QSOListActivity qsoListActivity) {
        this.qsoListActivity = qsoListActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.qso_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (hasRowAtPosition(position)) {
            QSO qso = mQSOs.get(position);
            holder.mStationView.setText(qso.getOtherStation());

            DateTime startTime = qso.getStartTime();
            holder.mDateView.setText(DateUtils.getRelativeTimeSpanString(
                    qsoListActivity, startTime));
            holder.mModeView.setText(qso.getMode());
            holder.mFrequencyView.setText(qso.getTransmissionFrequency());
        }

        holder.mView.setOnClickListener(
                v -> qsoListActivity.openID(getItemId(holder.getAdapterPosition())));
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

}
