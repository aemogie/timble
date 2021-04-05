package com.theaemogie.timble.renderer;

import com.theaemogie.timble.components.SpriteRenderer;
import com.theaemogie.timble.timble.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;


/**
 * @author <a href="mailto:theaemogie@gmail.com"> Aemogie. </a>
 */

@SuppressWarnings("PointlessArithmeticExpression")
public class RenderBatch {
	
	//region Vertex Structure
	
	//===========================================================================================
	//Position            //Color                             //Texture Coords     //Texture ID//
	//    x,     y,       //    r,     g,     b,     a        //    x,     y,      //   id     //
	//===========================================================================================
	//float, float,         float, float, float, float,         float, float,        float
	
	//endregion
	
	//region Variables
	private final int POS_SIZE = 2;
	private final int POS_OFFSET = 0;
	
	private final int COLOR_SIZE = 4;
	private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
	
	private final int TEXTURE_COORDS_SIZE = 2;
	private final int TEXTURE_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
	
	private final int TEXTURE_ID_SIZE = 1;
	private final int TEXTURE_ID_OFFSET = TEXTURE_COORDS_OFFSET + TEXTURE_COORDS_SIZE * Float.BYTES;
	
	private final int ENTITY_ID_SIZE = 1;
	private final int ENTITY_ID_OFFSET = TEXTURE_ID_OFFSET + TEXTURE_ID_SIZE * Float.BYTES;
	
	private final int VERTEX_SIZE = 2 + 4 + 2 + 1 + 1;
	private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;
	
	private SpriteRenderer[] sprites;
	private int numSprites;
	private boolean hasRoom;
	
	private float[] vertices;
	private int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};
	
	private int vaoID, vboID;
	private int maxBatchSize;
	private List<Texture> textures;
	//endregion
	
	public RenderBatch(int maxBatchSize) {
		this.sprites = new SpriteRenderer[maxBatchSize];
		this.maxBatchSize = maxBatchSize;
		
		// 4 vertices quads
		vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];
		
		this.numSprites = 0;
		this.hasRoom = true;
		
		this.textures = new ArrayList<>();
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
		
		glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
		glEnableVertexAttribArray(1);
		
		glVertexAttribPointer(2, TEXTURE_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_COORDS_OFFSET);
		glEnableVertexAttribArray(2);
		
		glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
		glEnableVertexAttribArray(3);
		
		glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
		glEnableVertexAttribArray(4);
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
	
	public void render(Window window) {
		
		//region Check if any sprites are dirty. If yes, reload and rebuffer.
		boolean rebufferData = false;
		for (int i = 0; i < numSprites; i++) {
			SpriteRenderer sprite = sprites[i];
			if (sprite.isDirty()) {
				loadVertexProperties(i);
				sprite.setClean();
				rebufferData = true;
			}
		}
		
		if (rebufferData) {
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
		}
		//endregion
		
		//region Use shader.
		Shader shader = Renderer.getBoundShader();
		shader.uploadMat4f("uProjection", window.getCurrentScene().getCamera().getProjectionMatrix());
		shader.uploadMat4f("uView", window.getCurrentScene().getCamera().getViewMatrix());
		for (int i = 0; i < textures.size(); i++) {
			glActiveTexture(GL_TEXTURE0 + i + 1);
			textures.get(i).bind();
		}
		shader.uploadIntArray("uTextures", textureSlots);
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
		
		for (Texture texture : textures) {
			texture.unbind();
		}
		shader.detach();
		//endregion
	}
	
	public void addSprite(SpriteRenderer sprite) {
		//region Get index and add renderObject.
		int index = this.numSprites;
		this.sprites[index] = sprite;
		this.numSprites++;
		
		if (sprite.getTexture() != null) {
			if (!textures.contains(sprite.getTexture())) {
				textures.add(sprite.getTexture());
			}
		}
		
		//region Add properties to local vertex array.
		loadVertexProperties(index);
		//endregion
		
		if (numSprites >= this.maxBatchSize) {
			this.hasRoom = false;
		}
	}
	
	private void loadVertexProperties(int index) {
		SpriteRenderer sprite = this.sprites[index];
		
		// Find offset within array (4 vertices per sprite)
		int offset = index * 4 * VERTEX_SIZE;
		
		Vector4f color = sprite.getColor();
		Vector2f[] textureCoords = sprite.getTextureCoords();
		
		int textureID = 0;
		if (sprite.getTexture() != null) {
			for (int i = 0; i < textures.size(); i++) {
				if (textures.get(i).equals(sprite.getTexture())) {
					textureID = i + 1;
					break;
				}
			}
		}
		
		
		//region Add vertices with appropriate properties.
		float xAdd = 1.0f;
		float yAdd = 1.0f;
		
		for (int i = 0; i < 4; i++) {
			if (i == 1) {
				yAdd = 0.0f;
			} else if (i == 2) {
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
			
			//load texture coordinates
			vertices[offset + 6] = textureCoords[i].x;
			vertices[offset + 7] = textureCoords[i].y;
			
			//load texture id
			vertices[offset + 8] = textureID;
			
			//load entity id
			vertices[offset + 9] = sprite.gameObject.getUUID() + 1;
			
			offset += VERTEX_SIZE;
		}
		//endregion
	}
	
	public boolean hasRoom() {
		return this.hasRoom;
	}
	
	public boolean hasTextureRoom() {
		return this.textures.size() < 8;
	}
	
	public boolean hasTexture(Texture texture) {
		return this.textures.contains(texture);
	}
}