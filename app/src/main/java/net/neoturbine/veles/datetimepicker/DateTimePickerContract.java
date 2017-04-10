package net.neoturbine.veles.datetimepicker;

import android.app.Dialog;
import android.content.Context;

public final class DateTimePickerContract {
    public interface View {
        void addDialogAndShow(Dialog dialog);
        Context getContext();
    }
}
