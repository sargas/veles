package net.neoturbine.veles.qso.data;

import dagger.Module;
import dagger.Provides;

@Module(includes = {ApplicationContextModule.class})
public class DataRepositoryModule {
    @Provides static DataRepository provideDataRepository(DatabaseDataRepository repository) {
        return repository;
    }
}
