package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;

public class TexturedSquare extends TexturedQuad {

	public TexturedSquare(PositionTextureVertex[] par1ArrayOfPositionTextureVertex, int textureX, int textureY, int textureX2, int textureY2, float textureWidth, float textureHeight) {
		super(par1ArrayOfPositionTextureVertex);
		float var8 = 0.0F / textureWidth;
		float var9 = 0.0F / textureHeight;
		par1ArrayOfPositionTextureVertex[0] = par1ArrayOfPositionTextureVertex[0].setTexturePosition(textureX2 / textureWidth - var8, textureY / textureHeight + var9);
		par1ArrayOfPositionTextureVertex[1] = par1ArrayOfPositionTextureVertex[1].setTexturePosition(textureX / textureWidth + var8, textureY2 / textureHeight + var9);
		par1ArrayOfPositionTextureVertex[2] = par1ArrayOfPositionTextureVertex[2].setTexturePosition(textureX / textureWidth + var8, textureY / textureHeight - var9);
		par1ArrayOfPositionTextureVertex[3] = par1ArrayOfPositionTextureVertex[3].setTexturePosition(textureX2 / textureWidth - var8, textureY2 / textureHeight - var9);
	}
}
