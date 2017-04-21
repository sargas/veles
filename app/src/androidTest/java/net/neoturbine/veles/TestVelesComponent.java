package net.neoturbine.veles;

import net.neoturbine.veles.qso.data.FakeDataRepositoryModule;
import net.neoturbine.veles.qso.detail.QSODetailFragmentModule;
import net.neoturbine.veles.qso.list.QSOListActivityModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {FakeDataRepositoryModule.class, QSOListActivityModule.class, QSODetailFragmentModule.class, AndroidInjectionModule.class})
interface TestVelesComponent extends VelesComponent {
}
