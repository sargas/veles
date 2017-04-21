package net.neoturbine.veles;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.support.annotation.NonNull;

import net.neoturbine.veles.qso.data.ApplicationContextModule;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasDispatchingActivityInjector;
import dagger.android.HasDispatchingFragmentInjector;

@SuppressWarnings("WeakerAccess")
public class VelesApplication extends Application implements HasDispatchingActivityInjector, HasDispatchingFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingActivityInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> mDispatchingFragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        getComponent().inject(this);
    }

    @NonNull
    protected VelesComponent getComponent() {
        return DaggerVelesComponent.builder()
                .applicationContextModule(new ApplicationContextModule(getApplicationContext()))
                .build();
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return mDispatchingActivityInjector;
    }

    @Override
    public DispatchingAndroidInjector<Fragment> fragmentInjector() {
        return mDispatchingFragmentInjector;
    }
}
