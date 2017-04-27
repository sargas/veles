package net.neoturbine.veles.qso.edit;

import dagger.Module;
import dagger.Provides;

@Module
abstract class EditModule {
    @Provides
    static EditContracts.ViewModel provideViewModel() {
        return new ViewModelImp();
    }
}
