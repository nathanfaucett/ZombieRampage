package io.faucette.zombierampage;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;


public class GameView extends GLSurfaceView {
    public GLRenderer renderer;

    private boolean started;


    public GameView(Context context, ActivityControl activityControl) {

        super(context);

        setEGLContextClientVersion(2);

        started = false;
        renderer = new GLRenderer(context, activityControl);
        setRenderer(renderer);
    }

    public void init() {
        if (started == false) {
            started = true;
            renderer.init();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (renderer.game.scene != null) {
            return renderer.game.scene.getPlugin(InputPlugin.class).onTouchEvent(e);
        } else {
            return false;
        }
    }
}
