package net.neoturbine.veles.datetimepicker;

import dagger.Component;

@Component(modules = {DateTimePickerModule.class})
public interface DateTimePickerComponent {
    DateTimePickerContract.ViewModel getViewModel();
}
