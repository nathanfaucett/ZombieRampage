package io.faucette.zombierampage;


public class ActivityControl {
    private MainActivity activity;


    public ActivityControl(MainActivity activity) {
        this.activity = activity;
    }

    public void showBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.showBanner();
            }
        });
    }

    public void hideBanner() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.hideBanner();
            }
        });
    }

    public void signIn() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.signIn();
            }
        });
    }

    public void showLeaderBoard() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.signIn();
            }
        });
    }

    public void submitHighScore(final int highScore) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.submitHighScore(highScore);
            }
        });
    }
}
