package io.faucette.zombierampage;


import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

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

    public void onSignIn(final MainActivity.SignInCallback callback) {
        final Semaphore mutex = new Semaphore(0);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.onSignIn(callback);
                mutex.release();
            }
        });

        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void showLeaderBoard() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.showLeaderBoard();
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

    public boolean isSignedIn() {
        final AtomicBoolean isSignedIn = new AtomicBoolean(false);
        final Semaphore mutex = new Semaphore(0);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                isSignedIn.set(activity.isSignedIn());
                mutex.release();
            }
        });

        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isSignedIn.get();
    }
}
