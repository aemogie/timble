package com.theaemogie.timble.renderer;

import com.theaemogie.timble.util.Logger;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.system.NativeType;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static com.theaemogie.timble.util.StringUtils.removeEmptyEntrees;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
@SuppressWarnings("unused")
public class Shader {
	
	private final Path filepath;
	public boolean beingUsed = false;
	HashMap<Integer, String> shaders;
	private int shaderProgramID;
	
	public Shader(Path filepath) {
		this.filepath = filepath;
		shaders = new HashMap<>();
		try {
			String source = new String(Files.readAllBytes(filepath)).trim();
			String[] splitSource = removeEmptyEntrees(source.split("(#type)( )+(\\w)+"));
			for (int i = 0; i < splitSource.length; i++) {
				String shader = splitSource[i];
				String trimmedSrc = source.trim();
				int beginIndex = trimmedSrc.indexOf("#type ");
				if (beginIndex == -1) continue;
				int endIndex = trimmedSrc.substring(beginIndex).indexOf("\n") + beginIndex;
				String typeStr = trimmedSrc.substring(beginIndex, endIndex).replace("#type", "").trim();
				try {
					int type = GL20.class.getDeclaredField(typeStr).getInt(null);
					shaders.put(type, shader.trim());
				} catch (IllegalAccessException | NoSuchFieldException e1) { //GL20
				try {
					int type = GL32.class.getDeclaredField(typeStr).getInt(null);
					System.out.println("\n" + i + " - " + type + " - " + typeStr + " - \n" + shader);
					shaders.put(type, shader.trim());
				} catch (IllegalAccessException | NoSuchFieldException e2) { //GL32
				try {
					int type = GL40.class.getDeclaredField(typeStr).getInt(null);
					System.out.println("\n" + i + " - " + type + " - " + typeStr + " - \n" + shader);
					shaders.put(type, shader.trim());
				} catch (IllegalAccessException | NoSuchFieldException e3) {
					Logger.logFatal("Invalid shader type: \"" + typeStr + "\" in shader file: \"" + filepath.toAbsolutePath() + "\"");
					e3.printStackTrace();
				}}} //GL40
				source = source.substring(endIndex);
			}
		} catch (IOException error) {
			error.printStackTrace();
			assert false : "Error: Could not open file for shader: '" + filepath.toAbsolutePath() + "'!";
		}
	}
	
	public void compile() {
		shaderProgramID = glCreateProgram();
		shaders.forEach((type, source) -> glAttachShader(shaderProgramID, getShaderIDFromSource(source, type)));
		glLinkProgram(shaderProgramID);
		//Check for errors
		int success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			int length = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath.toAbsolutePath() + "'\n\tShader linking failed!");
			System.out.println(glGetProgramInfoLog(shaderProgramID, length));
		}
	}
	
	private int getShaderIDFromSource(String shaderSource, @SuppressWarnings("SpellCheckingInspection") @NativeType("GLenum") int type) {
		//Load and compile fragment shader;
		int shaderID = glCreateShader(type);
		//Pass shader code to GPU
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		//Check for errors
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			int length = glGetShaderi(shaderID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath.toAbsolutePath() + "'\n\tFragment shader compilation failed!");
			System.out.println(glGetShaderInfoLog(shaderID, length));
			assert false;
		}
		return shaderID;
	}
	
	public void use() {
		if (!beingUsed) {
			glUseProgram(shaderProgramID);
			beingUsed = true;
		}
	}
	
	public void detach() {
		glUseProgram(0);
		beingUsed = false;
	}
	
	public void uploadMat4f(String varName, Matrix4f mat4) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
		mat4.get(matBuffer);
		glUniformMatrix4fv(varLocation, false, matBuffer);
	}
	
	public void uploadMat3f(String varName, Matrix3f mat4) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
		mat4.get(matBuffer);
		glUniformMatrix3fv(varLocation, false, matBuffer);
	}
	
	public void uploadVec4f(String varName, Vector4f vec) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
	}
	
	public void uploadVec3f(String varName, Vector3f vec) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform3f(varLocation, vec.x, vec.y, vec.z);
	}
	
	public void uploadVec2f(String varName, Vector2f vec) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform2f(varLocation, vec.x, vec.y);
	}
	
	public void uploadFloat(String varName, float val) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1f(varLocation, val);
	}
	
	public void uploadInt(String varName, int val) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1i(varLocation, val);
	}
	
	public void uploadBoolean(String varName, boolean val) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1i(varLocation, val ? GL_TRUE : GL_FALSE);
	}
	
	public void uploadTexture(String varName, int slot) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1i(varLocation, slot);
	}
	
	public void uploadIntArray(String varName, int[] array) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		glUniform1iv(varLocation, array);
	}
	
	public void uploadVec2fArray(String varName, Vector2f[] array) {
		int varLocation = glGetUniformLocation(shaderProgramID, varName);
		use();
		float[] out = new float[array.length * 2];
		for (int i = 0; i < array.length; i++) {
			Vector2f vec2f = array[i];
			out[i] = vec2f.x;
			out[++i] = vec2f.y;
		}
		glUniform2fv(varLocation, out);
	}
}
