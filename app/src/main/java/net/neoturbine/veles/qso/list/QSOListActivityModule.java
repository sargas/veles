package net.neoturbine.veles.qso.list;

import android.app.Activity;

import dagger.Binds;
import dagger.Module;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;

@Module(subcomponents = QSOListActivitySubcomponent.class)
public abstract class QSOListActivityModule {
    @SuppressWarnings("unused")
    @Binds
    @IntoMap
    @ActivityKey(QSOListActivity.class)
    abstract AndroidInjector.Factory<? extends Activity>
        bindQSOListActivityInjectorFactory(QSOListActivitySubcomponent.Builder builder);
}
