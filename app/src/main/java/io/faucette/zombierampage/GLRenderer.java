package io.faucette.zombierampage;


import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;

import io.faucette.scene_graph.Scene;
import io.faucette.scene_renderer.SceneRenderer;


public class GLRenderer implements GLSurfaceView.Renderer {
    public Game game;

    private SceneRenderer sceneRenderer;
    private Context context;


    public GLRenderer(Context context) {
        this.context = context;
        game = new Game();
        setScene(game.scene);
    }

    public void setScene(Scene s) {
        if (sceneRenderer != null) {
            sceneRenderer.clear();
        }

        sceneRenderer = new SceneRenderer(s);

        sceneRenderer.addRendererPlugin(new GLRendererPlugin());
        sceneRenderer.addRenderer(new SpriteGLRenderer(context));
        sceneRenderer.addRenderer(new UIGLRenderer(context));
    }

    public void onDrawFrame(GL10 unused) {
        game.update();
        sceneRenderer.render();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config) {
        game.init();
        sceneRenderer.init();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        game.scene.getPlugin(InputPlugin.class).setDimensions((float) width, (float) height);
        sceneRenderer.getRendererPlugin(GLRendererPlugin.class).set(width, height);
    }
}
