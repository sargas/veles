package net.neoturbine.veles.qso.edit;

import android.app.Fragment;

import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.FragmentKey;
import dagger.multibindings.IntoMap;

@Module(subcomponents = QSOEditFragmentSubcomponent.class)
public abstract class QSOEditFragmentModule {
    @SuppressWarnings("unused")
    @Binds
    @IntoMap
    @FragmentKey(QSOEditFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment>
        bindQSOEditFragmentInjectorFactory(QSOEditFragmentSubcomponent.Builder builder);
}
