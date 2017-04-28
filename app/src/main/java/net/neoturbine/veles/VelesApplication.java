package net.neoturbine.veles;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.squareup.leakcanary.LeakCanary;

import io.fabric.sdk.android.Fabric;
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

        Crashlytics crashKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashKit);

        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            setupDebugTimber();
        } else {
            setupProductionTimber();
        }

        getComponent().inject(this);
    }

    private void setupDebugTimber() {
        Timber.plant(new Timber.DebugTree());
    }

    private void setupProductionTimber() {
        Timber.plant(new Timber.Tree() {
            @Override
            protected void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable t) {
                Crashlytics.log(priority, tag, message);
                if (t != null)
                    Crashlytics.logException(t);
            }
        });
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
