package com.xcompwiz.mystcraft.client.model;

import com.xcompwiz.mystcraft.client.render.TexturedSquare;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelPrism extends ModelElement {
	/**
	 * The (x,y,z) vertex positions and (u,v) texture coordinates for each of the 8 points on a cube
	 */
	private PositionTextureVertex[] vertexPositions;

	/** An array of 6 TexturedQuads, one for each face of a cube */
	private TexturedQuad[] quadList;
	public String name;

	public ModelPrism(ModelRendererAdvanced modelRenderer, int textureX, int textureY, float x, float y, float z, int width, int height1, int height2, int depth, float scalar) {
		this.vertexPositions = new PositionTextureVertex[8];
		this.quadList = new TexturedQuad[6];
		float x2 = x + width;
		float y2a = y + height1;
		float y2b = y + height2;
		float z2 = z + depth;
		x -= scalar;
		y -= scalar;
		z -= scalar;
		x2 += scalar;
		y2a += scalar;
		y2b += scalar;
		z2 += scalar;

		if (modelRenderer.mirror) {
			float swap = x2;
			x2 = x;
			x = swap;
		}

		PositionTextureVertex point1 = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
		PositionTextureVertex point2 = new PositionTextureVertex(x2, y, z, 0.0F, 8.0F);
		PositionTextureVertex point3 = new PositionTextureVertex(x2, y2b, z, 8.0F, 8.0F);
		PositionTextureVertex point4 = new PositionTextureVertex(x, y2a, z, 8.0F, 0.0F);
		PositionTextureVertex point5 = new PositionTextureVertex(x, y, z2, 0.0F, 0.0F);
		PositionTextureVertex point6 = new PositionTextureVertex(x2, y, z2, 0.0F, 8.0F);
		PositionTextureVertex point7 = new PositionTextureVertex(x2, y2b, z2, 8.0F, 8.0F);
		PositionTextureVertex point8 = new PositionTextureVertex(x, y2a, z2, 8.0F, 0.0F);
		this.vertexPositions[0] = point1;
		this.vertexPositions[1] = point2;
		this.vertexPositions[2] = point3;
		this.vertexPositions[3] = point4;
		this.vertexPositions[4] = point5;
		this.vertexPositions[5] = point6;
		this.vertexPositions[6] = point7;
		this.vertexPositions[7] = point8;
		// x+
		this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] { point7, point6, point2, point3 }, textureX + width + width, textureY, textureX + width + width + height2, textureY + depth, modelRenderer.textureWidth, modelRenderer.textureHeight);
		// x-
		this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] { point4, point1, point5, point8 }, textureX + width + width + height2, textureY, textureX + width + width + height2 + height1, textureY + depth, modelRenderer.textureWidth, modelRenderer.textureHeight);
		// bottom
		this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] { point2, point6, point5, point1 }, textureX, textureY, textureX + width, textureY + depth, modelRenderer.textureWidth, modelRenderer.textureHeight);
		// top
		this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] { point7, point3, point4, point8 }, textureX + width, textureY, textureX + width + width, textureY + depth, modelRenderer.textureWidth, modelRenderer.textureHeight);
		// z-
		this.quadList[4] = new TexturedSquare(new PositionTextureVertex[] { point4, point3, point2, point1 }, textureX, textureY + depth, textureX + width, textureY + depth + height2, modelRenderer.textureWidth, modelRenderer.textureHeight);
		// z+
		this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] { point5, point6, point7, point8 }, textureX + width, textureY + depth, textureX + width + width, textureY + depth + height2, modelRenderer.textureWidth, modelRenderer.textureHeight);

		if (modelRenderer.mirror) {
			TexturedQuad[] qList = this.quadList;
			int length = qList.length;

			for (int i = 0; i < length; ++i) {
				TexturedQuad quad = qList[i];
				quad.flipFace();
			}
		}
	}

	/**
	 * Draw the six sided box defined by this ModelBox
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void render(BufferBuilder vb, float par2) {
		TexturedQuad[] qList = this.quadList;
		int length = qList.length;

		for (int var5 = 0; var5 < length; ++var5) {
			TexturedQuad quad = qList[var5];
			quad.draw(vb, par2);
		}
	}

	public ModelPrism setName(String par1Str) {
		this.name = par1Str;
		return this;
	}
}
