package com.slapin.napt.sample.dagger;


class MainActivityComponentBridge {

    static MainActivityComponent.Factory factory() {
        return DaggerMainActivityComponent.factory();
    }
}
