package io.faucette.zombierampage;


import android.content.Context;
import android.opengl.GLSurfaceView;


public class GameView extends GLSurfaceView {
    private GLRenderer renderer;


    public GameView(Context context) {

        super(context);

        setEGLContextClientVersion(2);

        renderer = new GLRenderer(context);
        setRenderer(renderer);
    }
}
