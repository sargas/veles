package net.neoturbine.veles.qso.data;

import dagger.Component;

@Component(modules = {DataRepositoryModule.class})
public interface DataRepositoryComponent {
    DataRepository getRepository();
}
