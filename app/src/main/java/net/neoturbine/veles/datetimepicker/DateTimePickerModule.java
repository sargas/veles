package net.neoturbine.veles.datetimepicker;

import dagger.Module;
import dagger.Provides;

@Module
class DateTimePickerModule {
    @Provides
    DateTimePickerContract.ViewModel providesViewModel(DateTimePickerModel model) {
        return new DateTimePickerViewModel(model);
    }

    @Provides
    DateTimePickerModel providesModel() {
        return new DateTimePickerModel();
    }
}
