package net.neoturbine.veles.qso.detail;

import dagger.Module;
import dagger.Provides;

@Module
abstract class DetailModule {
    @Provides
    static DetailsContracts.ViewModel provideViewModel() {
        return new ViewModelImpl();
    }
}
