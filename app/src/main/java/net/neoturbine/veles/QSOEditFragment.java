package net.neoturbine.veles;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QSOEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QSOEditFragment extends Fragment {
    private static final String ARG_QSO_ID = "qso_id";

    private long mQSOid;

    public QSOEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param qso_id Parameter 1.
     * @return A new instance of fragment QSOEditFragment.
     */
    public static QSOEditFragment newInstance(long qso_id) {
        QSOEditFragment fragment = new QSOEditFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_QSO_ID, qso_id);
        fragment.setArguments(args);
        return fragment;
    }

    public static QSOEditFragment newInstance() {
        return new QSOEditFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQSOid = getArguments().getLong(ARG_QSO_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.qso_edit, container, false);
    }
}
