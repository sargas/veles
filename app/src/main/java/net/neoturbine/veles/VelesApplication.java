package net.neoturbine.veles;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.support.annotation.NonNull;

import com.squareup.leakcanary.LeakCanary;

import net.neoturbine.veles.qso.data.ApplicationContextModule;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasDispatchingActivityInjector;
import dagger.android.HasDispatchingFragmentInjector;
import timber.log.Timber;

@SuppressWarnings("WeakerAccess")
public class VelesApplication extends Application implements HasDispatchingActivityInjector, HasDispatchingFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Activity> mDispatchingActivityInjector;
    @Inject
    DispatchingAndroidInjector<Fragment> mDispatchingFragmentInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        setupTimber();

        getComponent().inject(this);
    }

    private void setupTimber() {
        Timber.plant(new Timber.DebugTree());
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
