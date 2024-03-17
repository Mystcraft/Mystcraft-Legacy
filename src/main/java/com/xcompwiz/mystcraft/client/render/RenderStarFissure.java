package com.xcompwiz.mystcraft.client.render;

import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.tileentity.TileEntityStarFissure;

public class RenderStarFissure extends TileEntitySpecialRenderer {
	FloatBuffer	field_40448_a;

	public RenderStarFissure() {
		field_40448_a = GLAllocation.createDirectFloatBuffer(16);
	}

	public void func_40446_a(TileEntityStarFissure tileentity, double d, double d1, double d2, float f) {
		float f1 = (float) field_147501_a.field_147560_j;
		float f2 = (float) field_147501_a.field_147561_k;
		float f3 = (float) field_147501_a.field_147558_l;
		GL11.glDisable(2896 /* GL_LIGHTING */);
		Random random = new Random(31100L);
		float f4 = 0.0F;
		for (int i = 0; i < 8; i++) {
			GL11.glPushMatrix();
			float f5 = 16 - i;
			float f6 = 0.04F;
			float f7 = 1.0F / (f5 + 1.0F);
			if (i == 0) {
				this.bindTexture(Vanilla.end_sky);
				f7 = 0.1F;
				f5 = 65F;
				f6 = 0.125F;
				GL11.glEnable(3042 /* GL_BLEND */);
				GL11.glBlendFunc(770, 771);
			}
			if (i == 1) {
				this.bindTexture(Vanilla.end_portal);
				GL11.glEnable(3042 /* GL_BLEND */);
				GL11.glBlendFunc(1, 1);
				f6 = 0.5F;
			}
			float f8 = (float) (-(d1 + f4));
			float f9 = f8 + ActiveRenderInfo.objectY;
			float f10 = f8 + f5 + ActiveRenderInfo.objectY;
			float f11 = f9 / f10;
			f11 = (float) (d1 + f4) + f11;
			GL11.glTranslatef(f1, f11, f3);
			GL11.glTexGeni(8192 /* GL_S */, 9472 /* GL_TEXTURE_GEN_MODE */, 9217 /* GL_OBJECT_LINEAR */);
			GL11.glTexGeni(8193 /* GL_T */, 9472 /* GL_TEXTURE_GEN_MODE */, 9217 /* GL_OBJECT_LINEAR */);
			GL11.glTexGeni(8194 /* GL_R */, 9472 /* GL_TEXTURE_GEN_MODE */, 9217 /* GL_OBJECT_LINEAR */);
			GL11.glTexGeni(8195 /* GL_Q */, 9472 /* GL_TEXTURE_GEN_MODE */, 9216 /* GL_EYE_LINEAR */);
			GL11.glTexGen(8192 /* GL_S */, 9473 /* GL_OBJECT_PLANE */, addToBuffer(1.0F, 0.0F, 0.0F, 0.0F));
			GL11.glTexGen(8193 /* GL_T */, 9473 /* GL_OBJECT_PLANE */, addToBuffer(0.0F, 0.0F, 1.0F, 0.0F));
			GL11.glTexGen(8194 /* GL_R */, 9473 /* GL_OBJECT_PLANE */, addToBuffer(0.0F, 0.0F, 0.0F, 1.0F));
			GL11.glTexGen(8195 /* GL_Q */, 9474 /* GL_EYE_PLANE */, addToBuffer(0.0F, 1.0F, 0.0F, 0.0F));
			GL11.glEnable(3168 /* GL_TEXTURE_GEN_S */);
			GL11.glEnable(3169 /* GL_TEXTURE_GEN_T */);
			GL11.glEnable(3170 /* GL_TEXTURE_GEN_R */);
			GL11.glEnable(3171 /* GL_TEXTURE_GEN_Q */);
			GL11.glPopMatrix();
			GL11.glMatrixMode(5890 /* GL_TEXTURE */);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, (System.currentTimeMillis() % 0xaae60L) / 700000F, 0.0F);
			GL11.glScalef(f6, f6, f6);
			GL11.glTranslatef(0.5F, 0.5F, 0.0F);
			GL11.glRotatef((i * i + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
			GL11.glTranslatef(-f1, -f3, -f2);
			f9 = f8 + ActiveRenderInfo.objectY;
			GL11.glTranslatef((ActiveRenderInfo.objectX * f5) / f9, (ActiveRenderInfo.objectZ * f5) / f9, -f2);
			drawTopFace(random, i, d, d1 + f4, d2, f7);
			drawBottomFace(random, i, d, d1, d2, f7);
			GL11.glPopMatrix();
			GL11.glMatrixMode(5888 /* GL_MODELVIEW0_ARB */);
		}

		GL11.glDisable(3042 /* GL_BLEND */);
		GL11.glDisable(3168 /* GL_TEXTURE_GEN_S */);
		GL11.glDisable(3169 /* GL_TEXTURE_GEN_T */);
		GL11.glDisable(3170 /* GL_TEXTURE_GEN_R */);
		GL11.glDisable(3171 /* GL_TEXTURE_GEN_Q */);
		GL11.glEnable(2896 /* GL_LIGHTING */);
	}

	private void drawTopFace(Random random, int i, double d, double d1, double d2, float f7) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float f11 = random.nextFloat() * 0.5F + 0.1F;
		float f12 = random.nextFloat() * 0.5F + 0.4F;
		float f13 = random.nextFloat() * 0.5F + 0.5F;
		if (i == 0) {
			f11 = f12 = f13 = 1.0F;
		}
		tessellator.setColorRGBA_F(f11 * f7, f12 * f7, f13 * f7, 1.0F);
		tessellator.addVertex(d, d1, d2);
		tessellator.addVertex(d, d1, d2 + 1.0d);
		tessellator.addVertex(d + 1.0d, d1, d2 + 1.0d);
		tessellator.addVertex(d + 1.0d, d1, d2);
		tessellator.draw();
	}

	private void drawBottomFace(Random random, int i, double d, double d1, double d2, float f7) {
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		float f11 = random.nextFloat() * 0.5F + 0.1F;
		float f12 = random.nextFloat() * 0.5F + 0.4F;
		float f13 = random.nextFloat() * 0.5F + 0.5F;
		if (i == 0) {
			f11 = f12 = f13 = 1.0F;
		}
		tessellator.setColorRGBA_F(f11 * f7, f12 * f7, f13 * f7, 1.0F);
		tessellator.addVertex(d + 1.0d, d1, d2);
		tessellator.addVertex(d + 1.0d, d1, d2 + 1.0d);
		tessellator.addVertex(d, d1, d2 + 1.0d);
		tessellator.addVertex(d, d1, d2);
		tessellator.draw();
	}

	private FloatBuffer addToBuffer(float f, float f1, float f2, float f3) {
		field_40448_a.clear();
		field_40448_a.put(f).put(f1).put(f2).put(f3);
		field_40448_a.flip();
		return field_40448_a;
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		func_40446_a((TileEntityStarFissure) tileentity, d, d1, d2, f);
	}
}
