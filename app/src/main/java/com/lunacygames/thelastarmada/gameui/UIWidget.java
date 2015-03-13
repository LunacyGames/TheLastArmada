package com.lunacygames.thelastarmada.gameui;

import android.view.MotionEvent;

import com.lunacygames.thelastarmada.player.PlayerList;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zeus on 2/20/15.
 */
public class UIWidget {

    private FloatBuffer vertex;
    private FloatBuffer text;

    private final UICallback callback;
    private final float x;
    private final float y;
    private final float size[];
    private int[] textureID;
    private final String caption;
    private final int tag;

    /* texture map */
    static float texture[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };
    private boolean visible = true;

    public UIWidget(String caption, int[] textureID, int tag,float x,
                    float y, float[] size, UICallback callback) {
        /* we see into the matrix */
        float matrix[] = {
                0.0f + x,  0.0f + y, 0.0f, /* top left vertex */
                0.0f + x,  size[1] + y, 0.0f, /* bottom left vertex */
                size[0] + x,  0.0f + y, 0.0f, /* bottom right vertex */
                size[0] + x,  size[1] + y, 0.0f  /* top right vertex */
        };

        /* make a byte buffer to fit all floats in the square array */
        ByteBuffer buffer = ByteBuffer.allocateDirect(matrix.length * 4);
        /* good ol' endianess */
        buffer.order(ByteOrder.nativeOrder());
        this.vertex = buffer.asFloatBuffer();
        this.vertex.put(matrix);
        this.vertex.position(0);

        this.caption = caption;
        this.callback = callback;
        this.x = x;
        this.y = y;
        this.size = size;
        this.textureID = textureID;
        this.tag = tag;

        /* if we are given a texture, must add it as well */
        if(textureID != null) {
            buffer = ByteBuffer.allocateDirect(texture.length * 4);
            buffer.order(ByteOrder.nativeOrder());
            text = buffer.asFloatBuffer();
            text.put(texture);
            text.position(0);
        } else {
            text = null;
        }
    }

    public void setTexture(GL10 gl, int[] texture) {
        gl.glDeleteTextures(1, textureID, 0);
        gl.glFlush();
        this.textureID = texture;
    }

    public void onDraw(GL10 gl) {
        /* we don't draw if the widget is invisible */
        if(!this.visible) return;

        gl.glPushMatrix();
        gl.glLoadIdentity();

        if(textureID != null) {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);
        }

        /* set client state */
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        if(this.textureID == null) {
            gl.glColor4f(0.0f, 0.0f, 1.0f, 0.5f);
        } else {
            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            gl.glFrontFace(GL10.GL_CW);
        }

        /* point to the vertex buffer */
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex);

        /* texture if needed */
        if(this.textureID != null) {
            gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, text);
        }

        if(this.tag == PlayerList.getPlayer()) {
            gl.glBlendFunc(GL10.GL_ADD, GL10.GL_ONE_MINUS_SRC_ALPHA);
        }


        /* draw the vertices as triangles */
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        /* if this is the active player */
        //if(this.tag == PlayerList.getPlayer()) {
        //    gl.glBlendFunc(GL10.GL_ADD, GL10.GL_ONE_MINUS_SRC_ALPHA);
        //    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        //}


        /* disable texture state if we have a texture */
        if(this.textureID != null) {
            gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        }
        /* disable client state */
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        /* reset colour to white */
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        gl.glPopMatrix();
    }

    public void onClick(MotionEvent e) {
        /* if we do not have a callback method, return */
        if(this.callback == null) return;
        /* otherwise, execute it */
        this.callback.onClick(e, this);
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float[] getSize() {
        return size;
    }

    public String getCaption() {
        return this.caption;
    }

    public int getTag() {
        return tag;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }
}
