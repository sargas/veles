package net.neoturbine.veles.qso.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.neoturbine.veles.R;

public class ViewHolder extends RecyclerView.ViewHolder {
    public final TextView mStationView;
    public final TextView mDateView;
    public final TextView mFrequencyView;
    public final TextView mModeView;
    public final View mView;

    public ViewHolder(View view) {
        super(view);
        mView = view;
        mStationView = (TextView) view.findViewById(R.id.station_name);
        mDateView = (TextView) view.findViewById(R.id.date);
        mModeView = (TextView) view.findViewById(R.id.mode);
        mFrequencyView = (TextView) view.findViewById(R.id.frequency);
    }

    @Override
    public String toString() {
        return super.toString() + " '" + mDateView.getText() + "'";
    }
}
