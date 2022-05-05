package com.slapin.napt.sample;

class MainActivityComponentBridge {

    static MainActivityComponent.Factory factory() {
        return DaggerMainActivityComponent.factory();
    }
}
