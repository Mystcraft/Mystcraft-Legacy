package com.xcompwiz.mystcraft.client.model;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.Tessellator;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModelBox extends ModelElement {
	/**
	 * The (x,y,z) vertex positions and (u,v) texture coordinates for each of the 8 points on a cube
	 */
	private PositionTextureVertex[]	vertexPositions;

	/** An array of 6 TexturedQuads, one for each face of a cube */
	private TexturedQuad[]			quadList;

	/** X vertex coordinate of lower box corner */
	public final float				posX1;

	/** Y vertex coordinate of lower box corner */
	public final float				posY1;

	/** Z vertex coordinate of lower box corner */
	public final float				posZ1;

	/** X vertex coordinate of upper box corner */
	public final float				posX2;

	/** Y vertex coordinate of upper box corner */
	public final float				posY2;

	/** Z vertex coordinate of upper box corner */
	public final float				posZ2;
	public String					name;

	public ModelBox(ModelRendererAdvanced modelRenderer, int textureX, int textureY, float x, float y, float z, int width, int height, int depth, float scalar) {
		this.posX1 = x;
		this.posY1 = y;
		this.posZ1 = z;
		this.posX2 = x + width;
		this.posY2 = y + height;
		this.posZ2 = z + depth;
		this.vertexPositions = new PositionTextureVertex[8];
		this.quadList = new TexturedQuad[6];
		float x2 = x + width;
		float y2 = y + height;
		float z2 = z + depth;
		x -= scalar;
		y -= scalar;
		z -= scalar;
		x2 += scalar;
		y2 += scalar;
		z2 += scalar;

		if (modelRenderer.mirror) {
			float swap = x2;
			x2 = x;
			x = swap;
		}

		PositionTextureVertex origin = new PositionTextureVertex(x, y, z, 0.0F, 0.0F);
		PositionTextureVertex point2 = new PositionTextureVertex(x2, y, z, 0.0F, 8.0F);
		PositionTextureVertex point3 = new PositionTextureVertex(x2, y2, z, 8.0F, 8.0F);
		PositionTextureVertex point4 = new PositionTextureVertex(x, y2, z, 8.0F, 0.0F);
		PositionTextureVertex point5 = new PositionTextureVertex(x, y, z2, 0.0F, 0.0F);
		PositionTextureVertex point6 = new PositionTextureVertex(x2, y, z2, 0.0F, 8.0F);
		PositionTextureVertex point7 = new PositionTextureVertex(x2, y2, z2, 8.0F, 8.0F);
		PositionTextureVertex point8 = new PositionTextureVertex(x, y2, z2, 8.0F, 0.0F);
		this.vertexPositions[0] = origin;
		this.vertexPositions[1] = point2;
		this.vertexPositions[2] = point3;
		this.vertexPositions[3] = point4;
		this.vertexPositions[4] = point5;
		this.vertexPositions[5] = point6;
		this.vertexPositions[6] = point7;
		this.vertexPositions[7] = point8;
		this.quadList[0] = new TexturedQuad(new PositionTextureVertex[] { point6, point2, point3, point7 }, textureX + depth + width, textureY + depth, textureX + depth + width + depth, textureY + depth + height, modelRenderer.textureWidth, modelRenderer.textureHeight);
		this.quadList[1] = new TexturedQuad(new PositionTextureVertex[] { origin, point5, point8, point4 }, textureX, textureY + depth, textureX + depth, textureY + depth + height, modelRenderer.textureWidth, modelRenderer.textureHeight);
		this.quadList[2] = new TexturedQuad(new PositionTextureVertex[] { point6, point5, origin, point2 }, textureX + depth, textureY, textureX + depth + width, textureY + depth, modelRenderer.textureWidth, modelRenderer.textureHeight);
		this.quadList[3] = new TexturedQuad(new PositionTextureVertex[] { point3, point4, point8, point7 }, textureX + depth + width, textureY + depth, textureX + depth + width + width, textureY, modelRenderer.textureWidth, modelRenderer.textureHeight);
		this.quadList[4] = new TexturedQuad(new PositionTextureVertex[] { point2, origin, point4, point3 }, textureX + depth, textureY + depth, textureX + depth + width, textureY + depth + height, modelRenderer.textureWidth, modelRenderer.textureHeight);
		this.quadList[5] = new TexturedQuad(new PositionTextureVertex[] { point5, point6, point7, point8 }, textureX + depth + width + depth, textureY + depth, textureX + depth + width + depth + width, textureY + depth + height, modelRenderer.textureWidth, modelRenderer.textureHeight);

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
	public void render(Tessellator par1Tessellator, float par2) {
		TexturedQuad[] qList = this.quadList;
		int length = qList.length;

		for (int var5 = 0; var5 < length; ++var5) {
			TexturedQuad quad = qList[var5];
			quad.draw(par1Tessellator, par2);
		}
	}

	public ModelBox setName(String par1Str) {
		this.name = par1Str;
		return this;
	}
}
