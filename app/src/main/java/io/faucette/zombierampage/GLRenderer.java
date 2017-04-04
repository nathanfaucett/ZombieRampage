package io.faucette.zombierampage;


import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import io.faucette.scene_graph.Scene;
import io.faucette.scene_renderer.SceneRenderer;


public class GLRenderer implements GLSurfaceView.Renderer {
    public Game game;
    public ActivityControl activityControl;

    private float width;
    private float height;
    private float actualWidth;
    private float actualHeight;

    private boolean surfaceCreated;
    private SceneRenderer sceneRenderer;
    private Context context;

    private float origWidth = 960f;


    public GLRenderer(Context context, ActivityControl activityControl) {
        this.context = context;
        this.activityControl = activityControl;
        game = new Game(this);
        surfaceCreated = false;
    }

    public void init() {
        game.init();
    }

    public void setScene(Scene s) {
        if (sceneRenderer != null) {
            sceneRenderer.clear();
        }

        sceneRenderer = new SceneRenderer(s);

        sceneRenderer.addRendererPlugin(new GLRendererPlugin(context));
        sceneRenderer.addRenderer(new SpriteGLRenderer(context));
        sceneRenderer.addRenderer(new UIGLRenderer(context));

        if (surfaceCreated) {
            sceneRenderer.init();
        }

        onResize();
    }

    public void onDrawFrame(GL10 unused) {
        if (surfaceCreated) {
            game.update();
            if (sceneRenderer != null) {
                sceneRenderer.render();
            }
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        surfaceCreated = true;
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        float ratio = (float) width / (float) height;
        float origHeight = origWidth / ratio;

        this.actualWidth = width;
        this.actualHeight = height;

        this.width = origWidth;
        this.height = origHeight;

        onResize();
    }

    public void onResize() {
        if (game.scene != null) {
            game.scene.getPlugin(InputPlugin.class).setDimensions(width, height, actualWidth, actualHeight);
            sceneRenderer.getRendererPlugin(GLRendererPlugin.class).set((int) width, (int) height, (int) actualWidth, (int) actualHeight);
        }
    }
}
