package com.lunacygames.thelastarmada.gamemap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tron on 2/16/15.
 */
public class MapEntity {

    private FloatBuffer vertex;
    private FloatBuffer text;
    private int[] textureID;
    float x, y;

    /* texture map */
    static float texture[] = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 1.0f
    };

    /**
     * Create a Map Entity. This can be a map tile, player or anything else
     * @param textureID Texture identification.
     * @param x X position on map.
     * @param y Y position on map.
     * @param size The size of the sprite.
     */
    public MapEntity(int[] textureID, float x, float y, float[] size) {

        float square[] = {
                x,  y, 0.0f, /* top left vertex */
                x,  size[1] + y, 0.0f, /* bottom left vertex */
                size[0] + x,  0.0f + y, 0.0f, /* bottom right vertex */
                size[0] + x,  size[1] + y, 0.0f  /* top right vertex */
        };
        /* make a byte buffer to fit all floats in the square array */
        ByteBuffer buffer = ByteBuffer.allocateDirect(square.length * 4);
        /* good ol' endianess */
        buffer.order(ByteOrder.nativeOrder());
        vertex = buffer.asFloatBuffer();
        vertex.put(square);
        vertex.position(0);

        /* now for the texture */
        buffer = ByteBuffer.allocateDirect(texture.length * 4);
        buffer.order(ByteOrder.nativeOrder());
        text = buffer.asFloatBuffer();
        text.put(texture);
        text.position(0);

        /* store the texture ID and coordinates */
        this.textureID = textureID;
        this.x = 0;
        this.y = 0;
    }

    /**
     * Draw method
     * @param gl OpenGL ES context to use for drawing
     */
    public void onDraw(GL10 gl) {

        gl.glPushMatrix();

        /* bind texture */
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);

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
}
