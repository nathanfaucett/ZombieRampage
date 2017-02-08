package io.faucette.zombierampage;


import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.faucette.camera_component.Camera;
import io.faucette.camera_component.CameraManager;
import io.faucette.math.Mat32;
import io.faucette.scene_graph.Scene;
import io.faucette.scene_renderer.Renderer;
import io.faucette.scene_renderer.RendererPlugin;
import io.faucette.scene_renderer.SceneRenderer;
import io.faucette.ui_component.UI;
import io.faucette.ui_component.UIManager;
import io.faucette.transform_components.Transform2D;


public class UIGLRenderer extends Renderer {

    private Mat32 projection = new Mat32();
    private Mat32 modelView = new Mat32();
    private Mat32 identity = new Mat32();

    private float[] projectoionData = new float[16];
    private float[] modelViewData = new float[16];
    private float[] clippingData = new float[]{0f, 0f, 1f, 1f};
    private float[] sizeData = new float[]{1f, 1f};

    private Context context;


    public UIGLRenderer(Context context) {
        this.context = context;
        GLRendererPlugin.mat32ToFloat16(modelViewData, new Mat32());
    }

    public UIGLRenderer setWidthHeight(float width, float height) {
        float top = 0;
        float right = width;
        float bottom = height;
        float left = 0;
        Mat32.orthographic(projection, top, right, bottom, left);
        GLRendererPlugin.mat32ToFloat16(projectoionData, projection);
        return this;
    }

    @Override
    public UIGLRenderer render() {
        SceneRenderer sceneRenderer = getSceneRenderer();
        GLRendererPlugin glPlugin = sceneRenderer.getRendererPlugin(GLRendererPlugin.class);
        Scene scene = sceneRenderer.getScene();

        UIManager uiManager = scene.getComponentManager(UIManager.class);
        Iterator<UI> it = uiManager.iterator();

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
        int modelViewHandle = GLES20.glGetUniformLocation(program, "modelView");
        int sizeHandle = GLES20.glGetUniformLocation(program, "size");
        int clippingHandle = GLES20.glGetUniformLocation(program, "clipping");

        GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projectoionData, 0);

        while (it.hasNext()) {
            UI ui = it.next();

            if (ui.getVisible()) {
                Transform2D transform2D = ui.getEntity().getComponent(Transform2D.class);
                renderUI(glPlugin, ui, transform2D, modelViewHandle, sizeHandle, clippingHandle);
            }
        }


        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(uvHandle);


        return this;
    }

    private void renderUI(
            GLRendererPlugin glPlugin,
            UI ui,
            Transform2D transform2D,
            int modelViewHandle,
            int sizeHandle,
            int clippingHandle
    ) {
        sizeData[0] = ui.getWidth();
        sizeData[1] = ui.getHeight();

        clippingData[0] = ui.getX();
        clippingData[1] = ui.getY();
        clippingData[2] = ui.getW();
        clippingData[3] = ui.getH();

        transform2D.getModelView(modelView, identity);
        GLRendererPlugin.mat32ToFloat16(modelViewData, modelView);

        GLES20.glUniformMatrix4fv(modelViewHandle, 1, false, modelViewData, 0);
        GLES20.glUniform2fv(sizeHandle, 1, sizeData, 0);
        GLES20.glUniform4fv(clippingHandle, 1, clippingData, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glPlugin.getTexture(context, ui.getImage()));

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
