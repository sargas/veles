package net.neoturbine.veles.qso.edit;

import net.neoturbine.veles.QSOEditFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@SuppressWarnings("WeakerAccess")
@Subcomponent(modules = EditModule.class)
public interface QSOEditFragmentSubcomponent extends AndroidInjector<QSOEditFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<QSOEditFragment> {}
}
