package net.neoturbine.veles;

import net.neoturbine.veles.qso.data.DataRepositoryModule;
import net.neoturbine.veles.qso.detail.QSODetailFragmentModule;
import net.neoturbine.veles.qso.list.QSOListActivityModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {DataRepositoryModule.class, QSOListActivityModule.class, QSODetailFragmentModule.class, AndroidInjectionModule.class})
public interface VelesComponent {
    void inject(VelesApplication application);
}
