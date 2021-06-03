package io.github.aemogie.timble.renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class Texture {
	
	private Path filepath;
	private transient int textureID;
	private int width, height;
	
	public Texture() {
		textureID = -1;
		width = -1;
		height = -1;
	}
	
	public Texture(int width, int height) {
		this.filepath = Paths.get("Generated");
		
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER,GL_LINEAR);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
		
	}
	
	public void init(Path filepath) {
		this.filepath = filepath;
		
		textureID = glGenTextures();
		
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		//Set params
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		//Load images
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);

        stbi_set_flip_vertically_on_load(true);
		ByteBuffer image = stbi_load(filepath.toAbsolutePath().toString(), width, height, channels, 0);
		//
		if (image != null) {
			this.width = width.get(0);
			this.height = height.get(0);
			if (channels.get(0) == 3) {
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, image);
			} else if (channels.get(0) == 4) {
				glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
			}
		} else {
			assert false : "Error: (Texture) Could not load image '" + filepath + "'!";
		}
		glGenerateMipmap(GL_TEXTURE_2D);
		stbi_image_free(image);
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, textureID);
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getTextureID() {
		return textureID;
	}
	
	public Path getFilepath() {
		return filepath;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (!(obj instanceof Texture)) return false;
		Texture objTex = (Texture) obj;
		
		return objTex.getWidth() == this.width && objTex.getHeight() == this.height && objTex.getTextureID() == this.textureID && objTex.getFilepath().equals(this.filepath);
	}
	
	@SuppressWarnings({"StringConcatenationInsideStringBufferAppend", "StringBufferReplaceableByString"})
	@Override
	public String toString() {
		StringBuilder outputString = new StringBuilder("Class: " + this.getClass().getCanonicalName() + "\n");
		outputString.append("Path: " + this.filepath.toAbsolutePath() + "\n");
		return outputString.toString();
	}
}
