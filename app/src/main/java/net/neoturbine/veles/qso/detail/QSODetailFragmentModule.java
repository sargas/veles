package net.neoturbine.veles.qso.detail;

import android.app.Fragment;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.FragmentKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = QSODetailFragmentSubcomponent.class)
public abstract class QSODetailFragmentModule {
    @SuppressWarnings("unused")
    @Binds
    @IntoMap
    @FragmentKey(QSODetailFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment>
        bindQSODetailFragmentInjectorFactory(QSODetailFragmentSubcomponent.Builder builder);
}
