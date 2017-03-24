package net.neoturbine.veles;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.Arrays;

public class SignalQualityPicker extends Fragment {
    private static final String TAG_READABILITY = "readability";
    private static final String TAG_SIGNAL = "signal";
    private static final String TAG_TONE = "tone";
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
        binding.rstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRSTPickerReadability();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onInflate(
            final Context context, final AttributeSet attrs, final Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray typedAttrs = context.obtainStyledAttributes(attrs, R.styleable.SignalQualityPicker);
        try {
            if (typedAttrs.hasValue(R.styleable.SignalQualityPicker_text))
                data.quality.set(typedAttrs.getString(R.styleable.SignalQualityPicker_text));
            if (typedAttrs.hasValue(R.styleable.SignalQualityPicker_hint))
                data.hint.set(typedAttrs.getString(R.styleable.SignalQualityPicker_hint));
        } finally {
            typedAttrs.recycle();
        }
    }

    String getQuality() {
        return data.quality.get();
    }

    void setQuality(String quality) {
        data.quality.set(quality);
    }

    public static class RSTPickerDialogFragment extends DialogFragment {
        interface Callback {
            void accept(int results[]);
        }

        private int mResTitle;
        private int mResItems;
        private int mDefaultItem;
        private int[] mResults;
        private Callback mCallback;

        private void setParameters(@StringRes int resTitle, @ArrayRes int resItems,
                                   int defaultItem,
                                   int[] current_results, Callback callback) {
            mResTitle = resTitle;
            mResItems = resItems;
            mResults = current_results;
            mCallback = callback;
            mDefaultItem = defaultItem;
        }

        @SuppressLint("InflateParams")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(mResTitle)
                    .setSingleChoiceItems(mResItems, mDefaultItem, null)
                    .setPositiveButton(R.string.rst_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int result = ((AlertDialog) dialog)
                                    .getListView().getCheckedItemPosition() + 1;
                            int[] newResults = Arrays.copyOf(mResults, mResults.length + 1);
                            newResults[mResults.length] = result;
                            if (mCallback != null)
                                mCallback.accept(newResults);
                        }
                    })
                    .setNegativeButton(R.string.rst_negative_button, null);

            AlertDialog dialog = builder.create();
            dialog.getListView().addFooterView(
                    dialog.getLayoutInflater().inflate(R.layout.rst_citation, null, true),
                    null, false);
            return dialog;
        }
    }

    private void openRSTPickerReadability() {
        RSTPickerDialogFragment dialog = new RSTPickerDialogFragment();
        dialog.setParameters(R.string.rst_readability_title, R.array.rst_readability,
                getReadabilityIndex(), new int[]{}, openRSTPickerSignal);
        FragmentManager fm = getChildFragmentManager();
        dialog.show(fm, TAG_READABILITY);
    }

    private final RSTPickerDialogFragment.Callback openRSTPickerSignal = new RSTPickerDialogFragment.Callback() {
        @Override
        public void accept(int[] results) {
            RSTPickerDialogFragment dialog = new RSTPickerDialogFragment();
            dialog.setParameters(R.string.rst_signal_title, R.array.rst_signal,
                    getSignalIndex(), results, openRSTPickerTone);
            FragmentManager fm = getChildFragmentManager();
            dialog.show(fm, TAG_SIGNAL);
        }
    };

    private final RSTPickerDialogFragment.Callback openRSTPickerTone = new RSTPickerDialogFragment.Callback() {
        @Override
        public void accept(int[] results) {
            RSTPickerDialogFragment dialog = new RSTPickerDialogFragment();
            dialog.setParameters(R.string.rst_tone_title, R.array.rst_tone,
                    getToneIndex(), results, openRSTPickerFinish);
            FragmentManager fm = getChildFragmentManager();
            dialog.show(fm, TAG_TONE);
        }
    };

    private final RSTPickerDialogFragment.Callback openRSTPickerFinish = new RSTPickerDialogFragment.Callback() {
        @Override
        public void accept(int[] results) {
            StringBuilder builder = new StringBuilder();
            builder.append(results[0]).append(results[1]);
            if (results[2] != 1)
                builder.append(results[2] - 1);

            data.quality.set(builder.toString());
        }
    };

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
