package io.faucette.zombierampage;


import android.content.Context;
import android.opengl.GLES20;

import java.util.Iterator;

import io.faucette.camera_component.Camera;
import io.faucette.camera_component.CameraManager;
import io.faucette.math.Mat32;
import io.faucette.scene_graph.Scene;
import io.faucette.scene_renderer.Renderer;
import io.faucette.scene_renderer.SceneRenderer;
import io.faucette.sprite_component.Sprite;
import io.faucette.sprite_component.SpriteManager;
import io.faucette.transform_components.Transform2D;


public class SpriteGLRenderer extends Renderer {

    private float[] projectoionData = new float[16];
    private float[] modelViewData = new float[16];
    private float[] clippingData = new float[]{0f, 0f, 1f, 1f};
    private float[] sizeData = new float[]{1f, 1f};
    private Mat32 modelView = new Mat32();

    private Context context;


    public SpriteGLRenderer(Context context) {
        this.context = context;
    }

    @Override
    public Renderer render() {
        SceneRenderer sceneRenderer = getSceneRenderer();
        GLRendererPlugin glPlugin = sceneRenderer.getRendererPlugin(GLRendererPlugin.class);
        Scene scene = sceneRenderer.getScene();

        SpriteManager spriteManager = scene.getComponentManager(SpriteManager.class);

        if (spriteManager != null) {
            Camera camera = scene.getComponentManager(CameraManager.class).getActiveCamera();
            Mat32 projection = camera.getProjection();
            Mat32 view = camera.getView();

            Iterator<Sprite> it = spriteManager.iterator();

            int program = glPlugin.getProgram();
            GLES20.glUseProgram(program);

            int positionHandle = GLES20.glGetAttribLocation(program, "position");
            GLES20.glEnableVertexAttribArray(positionHandle);
            GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, glPlugin.getVertexBuffer());

            int uvHandle = GLES20.glGetAttribLocation(program, "uv");
            GLES20.glEnableVertexAttribArray(uvHandle);
            GLES20.glVertexAttribPointer(uvHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, glPlugin.getUVBuffer());

            int textureHandle = GLES20.glGetUniformLocation(program, "texture");
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
            GLES20.glUniform1i(textureHandle, 0);

            int projectionHandle = GLES20.glGetUniformLocation(program, "projection");
            int viewHandle = GLES20.glGetUniformLocation(program, "modelView");
            int sizeHandle = GLES20.glGetUniformLocation(program, "size");
            int clippingHandle = GLES20.glGetUniformLocation(program, "clipping");


            GLRendererPlugin.mat32ToFloat16(projectoionData, projection);
            GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projectoionData, 0);

            while (it.hasNext()) {
                Sprite sprite = it.next();

                if (sprite.getVisible()) {
                    Transform2D transform2D = sprite.getEntity().getComponent(Transform2D.class);
                    renderSprite(glPlugin, sprite, transform2D, view, viewHandle, sizeHandle, clippingHandle);
                }
            }


            GLES20.glDisableVertexAttribArray(positionHandle);
            GLES20.glDisableVertexAttribArray(uvHandle);
        }

        return this;
    }

    private void renderSprite(
            GLRendererPlugin glPlugin,
            Sprite sprite,
            Transform2D transform2D,
            Mat32 view,
            int viewHandle,
            int sizeHandle,
            int clippingHandle
    ) {
        sizeData[0] = sprite.getWidth();
        sizeData[1] = sprite.getHeight();

        clippingData[0] = sprite.getX();
        clippingData[1] = sprite.getY();
        clippingData[2] = sprite.getW();
        clippingData[3] = sprite.getH();

        transform2D.getModelView(modelView, view);
        GLRendererPlugin.mat32ToFloat16(modelViewData, modelView);

        GLES20.glUniformMatrix4fv(viewHandle, 1, false, modelViewData, 0);
        GLES20.glUniform2fv(sizeHandle, 1, sizeData, 0);
        GLES20.glUniform4fv(clippingHandle, 1, clippingData, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glPlugin.getTexture(context, sprite.getImage()));

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
