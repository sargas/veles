package net.neoturbine.veles.qso.data;

import android.content.Context;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationContextModule {
    private final Context mAppContext;

    public ApplicationContextModule(Context applicationContext) {
        mAppContext = applicationContext;
    }

    @Provides
    public @Named("application context") Context provideApplicationContext() {
        return mAppContext;
    }
}
