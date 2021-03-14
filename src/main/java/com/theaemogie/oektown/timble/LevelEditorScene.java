package com.theaemogie.oektown.timble;

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

    private String vertexShaderSource =
            "#version 330 core\n" +
                    "\n" +
                    "layout (location = 0) in vec3 aPos;\n" +
                    "layout (location = 1) in vec4 aColor;\n" +
                    "\n" +
                    "out vec4 fColor;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    fColor = aColor;\n" +
                    "    gl_Position = vec4(aPos, 1.0);\n" +
                    "}";
    private String fragmentShaderSource =
            "#version 330 core\n" +
                    "\n" +
                    "in vec4 fColor;\n" +
                    "out vec4 color;\n" +
                    "\n" +
                    "void main(){\n" +
                    "    color = fColor;\n" +
                    "}";

    private int vertexID;
    private int fragmentID;
    private int shaderProgram;

    private float[] vertexArray = {
            //Position              //Color                                     //Index
             0.5f, -0.5f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f,     //Bottom Right  #00
            -0.5f,  0.5f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f,     //Top Left      #01
             0.5f,  0.5f, 0.0f,     0.0f, 0.0f, 1.0f, 1.0f,     //Top Right     #02
            -0.5f, -0.5f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f      //Bottom Left   #03
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
        //region Compile and link shaders.

        //region VertexShader
        //Load and compile vertex shader;
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //Pass shader code to GPU
        glShaderSource(vertexID, vertexShaderSource);
        glCompileShader(vertexID);
        //Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tVertex shader compilation failed!");
            System.out.println(glGetShaderInfoLog(vertexID, length));
            assert false : "";
        }
        //endregion

        //region FragmentShader
        //Load and compile fragment shader;
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //Pass shader code to GPU
        glShaderSource(fragmentID, fragmentShaderSource);
        glCompileShader(fragmentID);
        //Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tFragment shader compilation failed!");
            System.out.println(glGetShaderInfoLog(fragmentID, length));
            assert false : "";
        }
        //endregion

        //region Link 'em
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexID);
        glAttachShader(shaderProgram, fragmentID);
        glLinkProgram(shaderProgram);
        //Check for errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int length = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: 'defaultShader.glsl'\n\tShader linking failed!");
            System.out.println(glGetProgramInfoLog(shaderProgram, length));
        }
        //endregion

        //endregion

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
        int vertexSizeBytes = (positionSize + colorSize) * Float.BYTES;
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);
        //endregion
        //endregion
    }

    @Override
    public void update(double deltaTime) {
        //region Bind everything.
        glUseProgram(shaderProgram);
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
        glUseProgram(0);
        //endregion
    }

}
