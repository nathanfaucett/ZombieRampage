package io.faucette.zombierampage;


import android.app.Activity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class MainActivity extends Activity {
    private GameView gameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Entities.initAnimations();

        AdView adView = new AdView(this);
        adView.setAdUnitId(getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.SMART_BANNER);


        RelativeLayout mainLayout = new RelativeLayout(this);
        ActivityControl activityControl = new ActivityControl(this, adView);

        gameView = new GameView(this, activityControl);
        mainLayout.addView(gameView);

        RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mainLayout.addView(adView, adParams);

        setContentView(mainLayout);
    }
}
