package io.sogn.dialer;

import android.app.Application;

public class DialerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CallManager.init(this);
    }
}
