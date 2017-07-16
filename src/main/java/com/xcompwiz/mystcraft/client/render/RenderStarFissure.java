package com.xcompwiz.mystcraft.client.render;

import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.tileentity.TileEntityStarFissure;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RenderStarFissure extends TileEntitySpecialRenderer<TileEntityStarFissure> {

    private static final Field fieldPos;

    private static final Random RANDOM = new Random(31100L);
    private FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);


    public RenderStarFissure() {
        buffer = GLAllocation.createDirectFloatBuffer(16);
    }
    
	static {
	    fieldPos = ReflectionHelper.findField(ActiveRenderInfo.class, "position", "field_178811_e");
	    fieldPos.setAccessible(true);
    }

	@Override
	public void render(TileEntityStarFissure te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		float rx = (float) rendererDispatcher.entityX;
        float ry = (float) rendererDispatcher.entityY;
        float rz = (float) rendererDispatcher.entityZ;

        Vec3d pos;
        try {
            pos = (Vec3d) fieldPos.get(null);
        } catch (IllegalAccessException e) {
            return;
        }
        
        GlStateManager.disableLighting();
        RANDOM.setSeed(31100L);

        for (int i = 0; i < 8; ++i) {
            GlStateManager.pushMatrix();
            float f5 = 16 - i;
            float f6 = 0.04F;
            float f7 = 1.0F - (i * 0.1f);
            if (i == 0) {
                this.bindTexture(Vanilla.end_sky);
                f7 = 0.1F;
                f5 = 65F;
                f6 = 0.125F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
            if (i == 1) {
                this.bindTexture(Vanilla.end_portal);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                f6 = 0.5F;
            }
            float f8 = (float) -y;
            float f9 = f8 + (float) pos.y;
            float f10 = f8 + f5 + (float) pos.z;
            float f11 = f9 / f10;
            f11 = (float) y + f11;
            GlStateManager.translate(rx, f11, rz);
            
            GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_LINEAR);

            GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, this.addToBuffer(1, 0, 0, 0));
            GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, this.addToBuffer(0, 0, 1, 0));
            GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, this.addToBuffer(0, 0, 0, 1));
            GlStateManager.texGen(GlStateManager.TexGen.Q, GL11.GL_EYE_PLANE,    this.addToBuffer(0, 1, 0, 0));

            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
            
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, (System.currentTimeMillis() % 0xaae60L) / 200000F, 0.0F);
            GlStateManager.scale(f6, f6, f6);
            
            GlStateManager.translate(0.5F, 0.5F, 0.0F);
            GlStateManager.rotate((i * i + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.0F);
            GlStateManager.translate(-rx, -rz, -ry);

            f9 = (float) (f8 + pos.y);
            GlStateManager.translate((pos.x * f5) / f9, (pos.z * f5) / f9, -ry);

            drawTopFace(RANDOM, i, x, y, z, f7);
            drawBottomFace(RANDOM, i, x, y, z, f7);

            GlStateManager.popMatrix();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.enableLighting();
    }

	private void drawTopFace(Random random, int pass, double x, double y, double z, float colorShift) {
	    Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        float f11 = random.nextFloat() * 0.5F + 0.1F;
        float f12 = random.nextFloat() * 0.5F + 0.4F;
        float f13 = random.nextFloat() * 0.5F + 0.5F;
        if (pass == 0) {
            f11 = f12 = f13 = 1.0F;
        }
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(x,      y, z     ).color(f11 * colorShift, f12 * colorShift, f13 * colorShift, 1.0F).endVertex();
        vb.pos(x,      y, z + 1D).color(f11 * colorShift, f12 * colorShift, f13 * colorShift, 1.0F).endVertex();
        vb.pos(x + 1D, y, z + 1D).color(f11 * colorShift, f12 * colorShift, f13 * colorShift, 1.0F).endVertex();
        vb.pos(x + 1D, y, z     ).color(f11 * colorShift, f12 * colorShift, f13 * colorShift, 1.0F).endVertex();
        tes.draw();
	}

	private void drawBottomFace(Random random, int pass, double x, double y, double z, float colorShift) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder vb = tes.getBuffer();
        float f11 = random.nextFloat() * 0.5F + 0.1F;
        float f12 = random.nextFloat() * 0.5F + 0.4F;
        float f13 = random.nextFloat() * 0.5F + 0.5F;
        if (pass == 0) {
            f11 = f12 = f13 = 1.0F;
        }
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(x + 1D, y, z     ).color(f11 * colorShift, f12 * colorShift, f13 * colorShift, 1.0F).endVertex();
        vb.pos(x + 1D, y, z + 1D).color(f11 * colorShift, f12 * colorShift, f13 * colorShift, 1.0F).endVertex();
        vb.pos(x,      y, z + 1D).color(f11 * colorShift, f12 * colorShift, f13 * colorShift, 1.0F).endVertex();
        vb.pos(x,      y, z     ).color(f11 * colorShift, f12 * colorShift, f13 * colorShift, 1.0F).endVertex();
        tes.draw();
	}

	private FloatBuffer addToBuffer(float f, float f1, float f2, float f3) {
		buffer.clear();
		buffer.put(f).put(f1).put(f2).put(f3);
		buffer.flip();
		return buffer;
	}

}
