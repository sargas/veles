package net.neoturbine.veles;

import android.support.annotation.NonNull;

public class TestVelesApplication extends VelesApplication {
    @Override
    @NonNull
    protected VelesComponent getComponent() {
        return DaggerTestVelesComponent.builder()
                .build();
    }
}
