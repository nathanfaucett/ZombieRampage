package io.faucette.zombierampage;


import android.app.Activity;
import android.os.Bundle;


public class MainActivity extends Activity {
    private GameView gameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        setContentView(gameView);
    }
}
