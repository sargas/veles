package net.neoturbine.veles.qso.data;

import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationContextModule {
    private final Context mAppContext;

    public ApplicationContextModule(Context applicationContext) {
        mAppContext = applicationContext;
    }

    @Singleton
    @Provides
    @Named("application context") Context provideApplicationContext() {
        return mAppContext;
    }
}
