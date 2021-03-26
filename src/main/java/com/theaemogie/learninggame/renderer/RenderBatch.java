package com.theaemogie.learninggame.renderer;

import com.theaemogie.learninggame.components.SpriteRenderer;
import com.theaemogie.learninggame.gameengine.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */

@SuppressWarnings("PointlessArithmeticExpression")
public class RenderBatch {

    //region Vertex Structure

    //==================================================
    //Position            //Color                     //
    //    x,     y,       //    r,     g,     b,     a//
    //==================================================
    //float, float,         float, float, float, float

    //endregion

    //region Variables
    private final int POS_SIZE = 2;
    private final int POS_OFFSET = 0;
    private final int COLOR_SIZE = 4;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE + Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;

    private float[] vertices;

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;
    //endregion

    public RenderBatch(int maxBatchSize) {

        shader = new Shader("src/main/resources/assets/shaders/default.glsl");
        shader.compile();

        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
    }

    public void start() {
        //region Generate and bind VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        //endregion

        //region VBO - Allocate space in GPU for vertices.
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);
        //endregion

        //region EBO - Create and upload indices buffer to GPU.
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
        //endregion

        //region Enable buffer attribute pointer.
        //Positions
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);
        //Colors
        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        //endregion
    }

    private int[] generateIndices() {
        //6 indices per quad (3 per triangle * 2 triangles)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;
        // 3, 2, 0, 0, 2, 1,        7, 6, 4, 4, 6, 5
        //region Triangle 1
        elements[offsetArrayIndex + 0] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;
        //endregion
        //region Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
        //endregion
    }

    public void render() {
        //For now re-buffering everything every frame. Will make it so it re-buffers only if changes occur in future tutorial.
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        //region Use shader.
        shader.use();
        shader.uploadMat4f("uProjection", Window.getCurrentScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getCurrentScene().getCamera().getViewMatrix());
        //endregion

        //region Enable VAO
        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        //endregion

        //region Draw everything.
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);
        //endregion

        //region Unbind and disable everything.
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.detach();
        //endregion
    }

    public void addSprite(SpriteRenderer sprite) {
        //region Get index and add renderObject.
        int index = this.numSprites;
        this.sprites[index] = sprite;
        this.numSprites++;

        //region Add properties to local vertex array.
        loadVertexProperties(index);
        //endregion

        if (numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        //region Add vertices with appropriate properties.
        float xAdd = 1.0f;
        float yAdd = 1.0f;

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                yAdd = 0.0f;
            } else if (i == 1) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }
            //load pos
            vertices[offset + 0] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            //load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            offset += VERTEX_SIZE;
        }
        //endregion
    }

    public boolean hasRoom() {
        return hasRoom;
    }
}