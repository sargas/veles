package net.neoturbine.veles.qso.data;

import dagger.Module;
import dagger.Provides;

@Module
public class FakeDataRepositoryModule {
    @Provides
    static DataRepository provideDataRepository() {
        return FakeDataRepository.getInstance();
    }
}
