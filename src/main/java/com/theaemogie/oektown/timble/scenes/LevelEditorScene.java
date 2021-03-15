package com.theaemogie.oektown.timble.scenes;

import com.theaemogie.oektown.renderer.Shader;
import com.theaemogie.oektown.renderer.Texture;
import com.theaemogie.oektown.timble.Camera;
import com.theaemogie.oektown.util.Time;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class LevelEditorScene extends Scene {

    private Shader defaultShader;
    private Texture spriteTexture;

    private float[] vertexArray = {
            //Position              //Color                                          //Index
            100.0f, 000.0f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,   1, 1,  //Bottom Right #00
            000.0f, 100.0f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,   0, 0,  //Top Left     #01
            100.0f, 100.0f, 0.0f,     0.0f, 0.0f, 1.0f, 1.0f,   1, 0,  //Top Right    #02
            000.0f, 000.0f, 0.0f,     1.0f, 1.0f, 1.0f, 1.0f,   0, 1   //Bottom Left  #03
    };
    private int[] elementArray = {
/*
      #1 -> x   x <- #2
      #3 -> x   x <- #0 <- ANTI-CLOCKWISE STARTING FROM HERE!!
*/
            2, 1, 0,  //Top Right
            0, 1, 3   //Bottom Left
    };

    private int vaoID;
    private int vboID;
    private int eboID;

    public LevelEditorScene() {
    }

    @Override
    public void init() {

        this.camera = new Camera(new Vector2f());

        defaultShader = new Shader("assets/shaders/default.glsl");

        defaultShader.compile();

        this.spriteTexture = new Texture("assets/textures/sprites/sprite1.png");

        //region Generate VAOs, VBOs and EBOs.
        //region VAO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID); //Tell it that anything after the line is regarding vaoID.

        //Create float buffer of vertices.
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();
        //endregion

        //region VBO
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER,vertexBuffer,GL_STATIC_DRAW);
        //endregion

        //region EBO

        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer,GL_STATIC_DRAW);

        int positionSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionSize + colorSize + uvSize) * Float.BYTES;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionSize + colorSize) * Float.BYTES);
        glEnableVertexAttribArray(2);
        //endregion
        //endregion
    }

    @Override
    public void update(double deltaTime) {

        //region Bind everything.
        defaultShader.use();

        //Upload uniforms
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        spriteTexture.bind();

        defaultShader.uploadMat4f("uProjection",camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView",camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", (float) Time.getTime());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        //endregion

        //Draw
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT,0);

        //region Unbind everything.
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        defaultShader.detach();
        //endregion
    }

}
