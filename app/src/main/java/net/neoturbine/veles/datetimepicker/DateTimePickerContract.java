package net.neoturbine.veles.datetimepicker;

import android.app.Dialog;
import android.content.Context;

final class DateTimePickerContract {
    interface View {
        void addDialogAndShow(Dialog dialog);
        Context getContext();
    }
}
