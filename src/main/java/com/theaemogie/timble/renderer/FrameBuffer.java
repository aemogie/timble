package com.theaemogie.timble.renderer;

import static org.lwjgl.opengl.GL30.*;

/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */
public class FrameBuffer {
	private int fboID = 0;
	private Texture texture;
	
	public FrameBuffer(int width, int height) {
		
		fboID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);
		
		this.texture = new Texture(width, height);
		
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getTextureID(), 0);
		
		int rboID = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, rboID);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);
		
		if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
			assert false : "Error: Framebuffer not complete!";
		}
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);
	}
	
	public void unbind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public int getFboID() {
		return fboID;
	}
	
	public int getTextureID() {
		return texture.getTextureID();
	}
	
	public Texture getTexture() {
		return texture;
	}
}
