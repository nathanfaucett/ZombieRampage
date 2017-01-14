package io.faucette.zombierampage;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import io.faucette.camera_component.CameraManager;
import io.faucette.math.Mat32;
import io.faucette.math.Vec4;
import io.faucette.scene_renderer.RendererPlugin;


public class GLRendererPlugin extends RendererPlugin {


    public GLRendererPlugin() {
    }

    public void set(int width, int height) {
        getSceneRenderer()
        .getScene()
        .getComponentManager(CameraManager.class)
        .getActiveCamera()
        .set((float) width, (float) height);

        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public GLRendererPlugin init() {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glClearColor(0f, 0f, 0f, 1f);
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

    public static int loadTexture(final Context context, final int resourceId) {
        final int[] textures = new int[1];

        GLES20.glGenTextures(1, textures, 0);
        int textureHandle = textures[0];

        if (textureHandle != 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

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

    public static float[] mat32ToFloat(float[] out, Mat32 mat32) {
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
}
