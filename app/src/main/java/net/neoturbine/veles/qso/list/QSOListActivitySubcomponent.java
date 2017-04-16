package net.neoturbine.veles.qso.list;

import net.neoturbine.veles.QSOListActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@Subcomponent
public interface QSOListActivitySubcomponent extends AndroidInjector<QSOListActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<QSOListActivity> {}
}
