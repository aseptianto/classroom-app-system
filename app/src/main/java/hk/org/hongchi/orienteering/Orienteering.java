package hk.org.hongchi.orienteering;

import android.app.Application;
import android.content.res.Configuration;

/**
 * Created by user on 10/27/2015.
 */
public class Orienteering extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initSingletons();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * Initializes any singleton classes
     */
    protected void initSingletons() {
        // Initialize App DDP State Singleton
        DDPService.initInstance(getApplicationContext());
    }
}
