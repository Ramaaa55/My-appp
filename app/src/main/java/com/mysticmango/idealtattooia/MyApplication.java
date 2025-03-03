package com.mysticmango.idealtattooia;

import android.app.Application;
import android.util.Log;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.RequestConfiguration;
import java.util.Arrays;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Set up AdMob configuration
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                // Use test device IDs directly instead of DEVICE_ID_EMULATOR constant
                .setTestDeviceIds(Arrays.asList("EMULATOR"))
                .build();
        MobileAds.setRequestConfiguration(configuration);

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.d(TAG, "AdMob SDK initialized");
            }
        });
    }
}