package io.faucette.zombierampage;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ActivityControl {
    private Activity activity;
    private AdView adView;


    public ActivityControl(Activity activity, AdView adView) {
        this.activity = activity;
        this.adView = adView;
    }

    public void showBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adView.setVisibility(View.VISIBLE);
                adView.loadAd(new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build());
            }
        });
    }

    public void hideBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adView.setVisibility(View.GONE);
            }
        });
    }
}
