package com.lunacygames.thelastarmada.gameui;

import android.util.Log;
import android.view.MotionEvent;

import com.lunacygames.thelastarmada.gameutils.PlatformData;
import com.lunacygames.thelastarmada.gameutils.TextureHandler;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zeus on 3/13/15.
 */
public class TopMessage {
    private static int[] texture;
    private static String message;
    private static boolean shown;
    private static int counter;
    private static FloatBuffer vertex;
    private static FloatBuffer text;

    /* texture map */
    static float textureMap[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    public static void showMessage(String msg) {
        message = msg;
        shown = true;
        counter = 60;
    }

    public static void setup() {
        int height = PlatformData.getScreenHeight();
        int width = PlatformData.getScreenWidth();

        float msgbox[] = {
                0,  0, 0.0f,                    /* top left vertex */
                0,  0.05f * height, 0.0f,       /* bottom left vertex */
                width, 0, 0.0f,                 /* bottom right vertex */
                width,  0.05f * height, 0.0f    /* top right vertex */
        };
        /* make a byte buffer to fit all floats in the square array */
        ByteBuffer buffer = ByteBuffer.allocateDirect(msgbox.length * 4);
        /* good ol' endianess */
        buffer.order(ByteOrder.nativeOrder());
        vertex = buffer.asFloatBuffer();
        vertex.put(msgbox);
        vertex.position(0);

        /* now for the texture */
        buffer = ByteBuffer.allocateDirect(textureMap.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        text = buffer.asFloatBuffer();
        text.put(textureMap);
        text.position(0);
    }

    public static void onDraw(GL10 gl) {
        if(counter > 0) counter--;
        if(!shown) return;

        int height = PlatformData.getScreenHeight();
        int width = PlatformData.getScreenWidth();

        if(texture != null) {
            gl.glDeleteTextures(1, texture, 0);
            gl.glFlush();
        }

        texture =
                TextureHandler.createTextureFromString(gl, message, (int)(0.05 * height),
                        width, TextureHandler.TextAlign.ALIGN_CENTER);


        gl.glPushMatrix();
        gl.glLoadIdentity();

        /* bind texture */
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);

        /* set client state */
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        /* set face rotation */
        gl.glFrontFace(GL10.GL_CW);

        /* point to the vertex buffer */
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, text);

        /* draw the vertices as triangles */
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        /* disable client state */
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glPopMatrix();
    }

    public static void onMotionEvent(MotionEvent e) {
        Log.d("TopMessage: ", "motionEvent");
        if(counter > 0) return;
        else shown = false;
    }

    public static boolean isShown() {
        return shown;
    }
}
