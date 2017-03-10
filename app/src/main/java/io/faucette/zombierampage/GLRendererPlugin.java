package io.faucette.zombierampage;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.faucette.camera_component.CameraManager;
import io.faucette.math.Mat32;
import io.faucette.math.Vec4;
import io.faucette.scene_graph.Scene;
import io.faucette.scene_renderer.RendererPlugin;
import io.faucette.ui_component.UI;
import io.faucette.ui_component.UIManager;


public class GLRendererPlugin extends RendererPlugin {

    private static final String vertexSource =
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
    private static final String fragmentSource =
            "precision mediump float;" +

                    "uniform sampler2D texture;" +
                    "uniform vec4 clipping;" +
                    "uniform float alpha;" +

                    "varying vec2 vUv;" +

                    "void main() {" +
                    "  gl_FragColor = texture2D(texture, clipping.xy + (vUv * clipping.zw));" +
                    "  gl_FragColor = gl_FragColor * alpha;" +
                    "}";
    private static float vertexData[] = {
            0.5f, 0.5f,
            -0.5f, 0.5f,
            0.5f, -0.5f,
            -0.5f, -0.5f
    };
    private static float uvData[] = {
            1f, 1f,
            0f, 1f,
            1f, 0f,
            0f, 0f
    };

    private Typeface font;
    private Map<Integer, Integer> textures;
    private Map<String, Integer> textTextures;
    private Map<String, Rect> textBounds;
    private int program = -1;

    private FloatBuffer vertexBuffer;
    private FloatBuffer uvBuffer;


    public GLRendererPlugin(Context context) {
        textures = new HashMap<>();
        textTextures = new HashMap<>();
        textBounds = new HashMap<>();
        font = Typeface.createFromAsset(context.getAssets(), "fonts/pixel.ttf");
    }

    private static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                Log.e("Error", "Could not compile shader " + shaderType + ":");
                Log.e("Error", GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    public static int createProgram(String vertexSource, String fragmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, pixelShader);
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e("Error", "Could not link program: ");
                Log.e("Error", GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    public static int loadBitmap(final Bitmap bitmap) {
        final int[] textures = new int[1];

        GLES20.glGenTextures(1, textures, 0);
        int textureHandle = textures[0];

        if (textureHandle != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            bitmap.recycle();
        }

        if (textureHandle == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle;
    }

    public static int loadTexture(final Context context, final int resourceId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return loadBitmap(BitmapFactory.decodeResource(context.getResources(), resourceId, options));
    }

    public static float[] mat32ToFloat16(float[] out, Mat32 mat32) {
        float[] m = mat32.getValues();

        out[0] = m[0];
        out[1] = m[1];
        out[2] = 0;
        out[3] = 0;

        out[4] = m[2];
        out[5] = m[3];
        out[6] = 0;
        out[7] = 0;

        out[8] = 0;
        out[9] = 0;
        out[10] = 1;
        out[11] = 0;

        out[12] = m[4];
        out[13] = m[5];
        out[14] = 0;
        out[15] = 1;

        return out;
    }

    private static String getTextId(String text, int fontSize, int fontColor) {
        return text + ":" + fontSize + ":" + fontColor;
    }

    public Rect getTextBounds(UI ui) {
        return textBounds.get(getTextId(ui.getText(), ui.getFontSize(), ui.getFontColor()));
    }

    public Bitmap createTextBitmap(String text, int fontSize, int fontColor) {
        Rect bounds = new Rect();
        Paint paint = new Paint();

        paint.setAntiAlias(false);
        paint.setTypeface(font);
        paint.setTextSize(fontSize);
        paint.setColor(fontColor);
        paint.setFakeBoldText(true);
        paint.getTextBounds(text, 0, text.length(), bounds);

        Bitmap bitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0x00000000);

        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;
        canvas.drawText(text, x, y, paint);

        textBounds.put(getTextId(text, fontSize, fontColor), bounds);

        return bitmap;
    }

    @Override
    public GLRendererPlugin init() {

        super.init();

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(0f, 0f, 0f, 1f);

        loadBuffers();
        loadShaders();

        return this;
    }

    @Override
    public GLRendererPlugin clear() {
        super.clear();

        int[] textureHandle = new int[1];

        Iterator it = textures.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            textureHandle[0] = (Integer) pair.getValue();
            GLES20.glDeleteTextures(1, textureHandle, 0);
            it.remove();
        }

        it = textTextures.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            textureHandle[0] = (Integer) pair.getValue();
            GLES20.glDeleteTextures(1, textureHandle, 0);
            it.remove();
        }

        return this;
    }

    @Override
    public GLRendererPlugin before() {
        Vec4 background = getSceneRenderer()
                .getScene()
                .getComponentManager(CameraManager.class)
                .getActiveCamera()
                .getBackground();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(background.x, background.y, background.z, background.w);

        return this;
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public FloatBuffer getUVBuffer() {
        return uvBuffer;
    }

    public Map<Integer, Integer> getTextures() {
        return textures;
    }

    public Integer getTextTexture(final Context context, String text, int fontSize, int fontColor) {
        String id = GLRendererPlugin.getTextId(text, fontSize, fontColor);

        if (!textTextures.containsKey(id)) {
            textTextures.put(id, new Integer(
                    GLRendererPlugin.loadBitmap(
                            createTextBitmap(text, fontSize, fontColor)
                    )
            ));
        }
        return textTextures.get(id);
    }

    public Integer getUITexture(final Context context, UI ui) {
        if (ui.getText() == "") {
            return getTexture(context, ui.getImage());
        } else {
            return getTextTexture(context, ui.getText(), ui.getFontSize(), ui.getFontColor());
        }
    }

    public Integer getTexture(final Context context, Integer texture) {
        if (!textures.containsKey(texture)) {
            textures.put(texture, new Integer(GLRendererPlugin.loadTexture(context, texture)));
        }
        return textures.get(texture);
    }

    public int getProgram() {
        return program;
    }

    public void set(int width, int height) {
        Scene scene = sceneRenderer.getScene();

        scene
                .getComponentManager(CameraManager.class)
                .getActiveCamera()
                .set((float) width, (float) height);

        scene
                .getComponentManager(UIManager.class)
                .setWidthHeight((float) width, (float) height);

        sceneRenderer
                .getRenderer(UIGLRenderer.class)
                .setWidthHeight((float) width, (float) height);

        GLES20.glViewport(0, 0, width, height);
    }

    private void loadShaders() {
        program = GLRendererPlugin.createProgram(vertexSource, fragmentSource);
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
}
