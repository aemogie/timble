package com.theaemogie.oektown.renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Shader {

    private int shaderProgramID;

    private String vertexSource;
    private String fragmentSource;

    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;

        try {

            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitSource = source.split("(#type)( )+([a-zA-Z]+)");

            //Find first pattern in #type [pattern]
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            //Find second pattern in #type [pattern]
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            switch (firstPattern) {
                case "vertex":
                    vertexSource = splitSource[1];
                    break;
                case "fragment":
                    fragmentSource = splitSource[1];
                    break;
                default:
                    throw new IOException("Unexpected token: '" + firstPattern + "'!");
            }

            switch (secondPattern) {
                case "vertex":
                    vertexSource = splitSource[2];
                    break;
                case "fragment":
                    fragmentSource = splitSource[2];
                    break;
                default:
                    throw new IOException("Unexpected token: '" + secondPattern + "'!");
            }

        } catch (IOException error) {
            error.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'!";
        }
    }

    public void compile() {

        int vertexID;
        int fragmentID;

        //region VertexShader
        //Load and compile vertex shader;
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        //Pass shader code to GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);
        //Check for errors
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed!");
            System.out.println(glGetShaderInfoLog(vertexID, length));
            assert false : "";
        }
        //endregion

        //region FragmentShader
        //Load and compile fragment shader;
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        //Pass shader code to GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);
        //Check for errors
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed!");
            System.out.println(glGetShaderInfoLog(fragmentID, length));
            assert false : "";
        }
        //endregion

        //region Link 'em
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);
        //Check for errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int length = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tShader linking failed!");
            System.out.println(glGetProgramInfoLog(shaderProgramID, length));
        }
        //endregion
    }

    public void use() {
        glUseProgram(shaderProgramID);
    }

    public void detach() {
        glUseProgram(0);
    }
}
