package io.faucette.zombierampage;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;


public class GameView extends GLSurfaceView {
    public GLRenderer renderer;


    public GameView(Context context) {

        super(context);

        setEGLContextClientVersion(2);

        renderer = new GLRenderer(context);
        setRenderer(renderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return renderer.game.scene.getPlugin(InputPlugin.class).onTouchEvent(e);
    }
}
