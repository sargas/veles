package net.neoturbine.veles.qso.detail;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@SuppressWarnings("WeakerAccess")
@Subcomponent(modules = DetailModule.class)
public interface QSODetailFragmentSubcomponent extends AndroidInjector<QSODetailFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<QSODetailFragment> {}
}
