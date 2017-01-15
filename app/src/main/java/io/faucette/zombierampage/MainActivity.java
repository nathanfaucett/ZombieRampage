package io.faucette.zombierampage;


import android.app.Activity;
import android.os.Bundle;

import java.util.Iterator;
import java.util.Map;


public class MainActivity extends Activity {
    private GameView gameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Entities.initAnimations();

        gameView = new GameView(this);
        setContentView(gameView);
    }
}
