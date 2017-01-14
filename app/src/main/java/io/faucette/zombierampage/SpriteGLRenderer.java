package io.faucette.zombierampage;


import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    private static float vertexData[] = {
            1f,   1f,
            -1f,  1f,
            1f,  -1f,
            -1f, -1f
    };
    private static float uvData[] = {
            1f, 1f,
            0f, 1f,
            1f, 0f,
            0f, 0f
    };

    private static final String vertexSrc =
            "attribute vec2 position;" +
            "attribute vec2 uv;" +

            "uniform mat4 projection;" +
            "uniform mat4 modelView;" +
            "uniform vec2 size;" +

            "varying vec2 vUv;" +

            "void main() {" +
            "  vUv = vec2(uv.x, 1.0 - uv.y);" +
            "  gl_Position = projection * modelView * vec4(size * position, 0.0, 1.0);" +
            "}";

    private static final String fragmentSrc =
            "precision mediump float;" +

            "uniform sampler2D texture;" +
            "uniform vec4 clipping;" +

            "varying vec2 vUv;" +

            "void main() {" +
            "  gl_FragColor = texture2D(texture, clipping.xy + (vUv * clipping.zw));" +
            "}";

    private int program = -1;

    private float[] projectoionData = new float[16];
    private float[] modelViewData = new float[16];
    private float[] clippingData = new float[] { 0f, 0f, 1f, 1f};
    private float[] sizeData = new float[] { 1f, 1f};
    private Mat32 modelView = new Mat32();

    private Context context;
    private HashMap<Integer, Integer> textures;

    private FloatBuffer vertexBuffer;
    private FloatBuffer uvBuffer;


    public SpriteGLRenderer(Context context) {
        this.context = context;
        textures = new HashMap<>();
    }

    @Override
    public Renderer init() {

        super.init();

        loadBuffers();
        loadShaders();

        return this;
    }

    private void loadShaders() {
        program = GLRendererPlugin.createProgram(vertexSrc, fragmentSrc);
    }

    private void loadBuffers() {
        ByteBuffer bb;

        bb = ByteBuffer.allocateDirect(vertexData.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertexData);
        vertexBuffer.position(0);

        bb = ByteBuffer.allocateDirect(uvData.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvData);
        uvBuffer.position(0);
    }

    @Override
    public Renderer clear() {
        super.clear();

        int[] textureHandle = new int[1];
        Iterator it = textures.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            textureHandle[0] = (Integer) pair.getValue();
            GLES20.glDeleteTextures(1, textureHandle, 0);
            it.remove();
        }

        return this;
    }

    @Override
    public Renderer render() {
        SceneRenderer sceneRenderer = getSceneRenderer();
        Scene scene = sceneRenderer.getScene();

        SpriteManager spriteManager = scene.getComponentManager(SpriteManager.class);
        Camera camera = scene.getComponentManager(CameraManager.class).getActiveCamera();
        Mat32 projection = camera.getProjection();
        Mat32 view = camera.getView();

        Iterator<Sprite> it = spriteManager.iterator();


        GLES20.glUseProgram(program);

        int positionHandle = GLES20.glGetAttribLocation(program, "position");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, vertexBuffer);

        int uvHandle = GLES20.glGetAttribLocation(program, "uv");
        GLES20.glEnableVertexAttribArray(uvHandle);
        GLES20.glVertexAttribPointer(uvHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, uvBuffer);

        int textureHandle = GLES20.glGetUniformLocation(program, "texture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glUniform1i(textureHandle, 0);

        int projectionHandle = GLES20.glGetUniformLocation(program, "projection");
        int viewHandle = GLES20.glGetUniformLocation(program, "modelView");
        int sizeHandle = GLES20.glGetUniformLocation(program, "size");
        int clippingHandle = GLES20.glGetUniformLocation(program, "clipping");


        while (it.hasNext()) {
            Sprite sprite = it.next();
            Transform2D transform2D = sprite.getEntity().getComponent(Transform2D.class);
            renderSprite(sprite, transform2D, projection, view, projectionHandle, viewHandle, sizeHandle, clippingHandle);
        }


        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(uvHandle);


        return this;
    }

    private void renderSprite(
            Sprite sprite,
            Transform2D transform2D,
            Mat32 projection,
            Mat32 view,
            int projectionHandle,
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
        GLRendererPlugin.mat32ToFloat(projectoionData, projection);
        GLRendererPlugin.mat32ToFloat(modelViewData, modelView);

        GLES20.glUniformMatrix4fv(projectionHandle, 1, false, projectoionData, 0);
        GLES20.glUniformMatrix4fv(viewHandle, 1, false, modelViewData, 0);
        GLES20.glUniform2fv(sizeHandle, 1, sizeData, 0);
        GLES20.glUniform4fv(clippingHandle, 1, clippingData, 0);

        Integer image = sprite.getImage();
        if (!textures.containsKey(image)) {
            textures.put(image, new Integer(GLRendererPlugin.loadTexture(context, image)));
        }
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures.get(image));

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
