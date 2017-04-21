package net.neoturbine.veles.qso.list;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@SuppressWarnings("WeakerAccess")
@Subcomponent
public interface QSOListActivitySubcomponent extends AndroidInjector<QSOListActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<QSOListActivity> {}
}
