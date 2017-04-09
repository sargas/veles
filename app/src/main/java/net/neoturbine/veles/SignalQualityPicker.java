package net.neoturbine.veles;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.neoturbine.veles.databinding.SignalQualityPickerBinding;

import java.util.ArrayList;

public class SignalQualityPicker extends Fragment {
    private static final String TAG_READABILITY = "readability";
    private static final String TAG_SIGNAL = "signal";
    private static final String TAG_TONE = "tone";
    private static final String STATE_QUALITY = "state_quality";
    private static final int DEFAULT_READABILITY_INDEX = 4;
    private static final int DEFAULT_SIGNAL_INDEX = 8;
    private static final int DEFAULT_TONE_INDEX = 0;

    public static class SignalQualityData {
        public final ObservableField<String> quality = new ObservableField<>("");
        public final ObservableField<String> hint = new ObservableField<>("");
    }

    private final SignalQualityData data = new SignalQualityData();

    public SignalQualityPicker() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SignalQualityPickerBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.signal_quality_picker, container, false
        );
        binding.setData(data);
        binding.rstButton.setOnClickListener(openRSTPickerReadability);

        if (savedInstanceState != null)
            data.quality.set(savedInstanceState.getString(STATE_QUALITY));

        return binding.getRoot();
    }

    @Override
    public void onInflate(
            final Context context, final AttributeSet attrs, final Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray typedAttrs = context.obtainStyledAttributes(attrs, R.styleable.SignalQualityPicker);
        try {
            readAttributesIntoDataObject(typedAttrs);
        } finally {
            typedAttrs.recycle();
        }
    }

    private void readAttributesIntoDataObject(TypedArray typedAttrs) {
        if (typedAttrs.hasValue(R.styleable.SignalQualityPicker_text))
            data.quality.set(typedAttrs.getString(R.styleable.SignalQualityPicker_text));
        if (typedAttrs.hasValue(R.styleable.SignalQualityPicker_hint))
            data.hint.set(typedAttrs.getString(R.styleable.SignalQualityPicker_hint));
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_QUALITY, data.quality.get());
    }

    String getQuality() {
        return data.quality.get();
    }

    void setQuality(String quality) {
        data.quality.set(quality);
    }

    public static class RSTPickerDialogFragment extends DialogFragment {
        interface Callback {
            void accept(ArrayList<Integer> results);
        }

        private int mResTitle;
        private int mResItems;
        private int mDefaultItem;
        private ArrayList<Integer> mResults;
        private Callback mCallback;

        private void setParameters(@StringRes int resTitle, @ArrayRes int resItems,
                                   int defaultItem,
                                   ArrayList<Integer> current_results, Callback callback) {
            mResTitle = resTitle;
            mResItems = resItems;
            mResults = current_results;
            mCallback = callback;
            mDefaultItem = defaultItem;
        }

        @SuppressLint("InflateParams")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (savedInstanceState != null) {
                return super.onCreateDialog(savedInstanceState);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mResTitle)
                    .setSingleChoiceItems(mResItems, mDefaultItem, null)
                    .setPositiveButton(R.string.rst_positive_button, (dialog, which) -> {
                        int result = ((AlertDialog) dialog)
                                .getListView().getCheckedItemPosition() + 1;
                        mResults.add(result);
                        mCallback.accept(mResults);
                    })
                    .setNegativeButton(R.string.rst_negative_button, null);

            AlertDialog dialog = builder.create();
            dialog.getListView().addFooterView(
                    dialog.getLayoutInflater().inflate(R.layout.rst_citation, null, true),
                    null, false);
            return dialog;
        }
    }

    private final View.OnClickListener openRSTPickerReadability = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createRSTDialog(R.string.rst_readability_title, R.array.rst_readability,
                    getReadabilityIndex(), new ArrayList<>(3), openRSTPickerSignal, TAG_READABILITY);
        }
    };

    private final RSTPickerDialogFragment.Callback openRSTPickerSignal = new RSTPickerDialogFragment.Callback() {
        @Override
        public void accept(ArrayList<Integer> results) {
            createRSTDialog(R.string.rst_signal_title, R.array.rst_signal,
                    getSignalIndex(), results, openRSTPickerTone, TAG_SIGNAL);
        }
    };

    private final RSTPickerDialogFragment.Callback openRSTPickerTone = new RSTPickerDialogFragment.Callback() {
        @Override
        public void accept(ArrayList<Integer> results) {
            createRSTDialog(R.string.rst_tone_title, R.array.rst_tone,
                    getToneIndex(), results, openRSTPickerFinish, TAG_TONE);
        }
    };

    private final RSTPickerDialogFragment.Callback openRSTPickerFinish =
            results -> data.quality.set(convertResultArrayToRST(results));

    private String convertResultArrayToRST(ArrayList<Integer> results) {
        StringBuilder builder = new StringBuilder();
        builder.append(results.get(0)).append(results.get(1));

        if (hasTone(results))
            builder.append(results.get(2) - 1);

        return builder.toString();
    }

    private boolean hasTone(ArrayList<Integer> results) {
        return results.get(2) != 1;
    }

    private void createRSTDialog(@StringRes int resTitle, @ArrayRes int resItems,
                                 int defaultItem,
                                 ArrayList<Integer> current_results, RSTPickerDialogFragment.Callback callback,
                                 String fragmentTag) {
        RSTPickerDialogFragment dialog = new RSTPickerDialogFragment();
        dialog.setParameters(resTitle, resItems, defaultItem, current_results, callback);
        FragmentManager fm = getChildFragmentManager();
        dialog.show(fm, fragmentTag);
    }

    private boolean isRST() {
        return TextUtils.isDigitsOnly(data.quality.get()) &&
                (data.quality.get().length() == 3
                        || data.quality.get().length() == 2) &&
                Character.getNumericValue(data.quality.get().charAt(0)) < 6;
    }

    private int getReadabilityIndex() {
        if (isRST())
            return Character.getNumericValue(data.quality.get().charAt(0)) - 1;
        else
            return DEFAULT_READABILITY_INDEX;
    }

    private int getSignalIndex() {
        if (isRST())
            return Character.getNumericValue(data.quality.get().charAt(1)) - 1;
        else
            return DEFAULT_SIGNAL_INDEX;
    }

    private int getToneIndex() {
        if (isRST() && data.quality.get().length() == 3)
            return Character.getNumericValue(data.quality.get().charAt(2));
        else
            return DEFAULT_TONE_INDEX;
    }
}
