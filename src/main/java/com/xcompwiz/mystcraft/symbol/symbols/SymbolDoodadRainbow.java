package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.client.render.RenderRainbow;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SymbolDoodadRainbow extends SymbolBase {

	public SymbolDoodadRainbow(ResourceLocation identifier) {
		super(identifier);
	}

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		Number angle = controller.popModifier(ModifierUtils.ANGLE).asNumber();
		controller.registerInterface(new CelestialObject(controller, seed, angle));
	}

	@Override
	public boolean generatesConfigOption() {
		return true;
	}

	private class CelestialObject extends CelestialBase {

		private Random rand;
		private float angle;
		private boolean initialized;
		private Integer rainbowGLCallList = null;

		CelestialObject(AgeDirector controller, long seed, Number angle) {
			rand = new Random(seed);
			if (angle == null) {
				angle = rand.nextDouble() * 360.0F;
			}
			this.angle = -angle.floatValue();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void render(TextureManager eng, World worldObj, float partial) {
			if (!initialized) {
				initialize();
			}

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.pushMatrix();
			GlStateManager.rotate(angle, 0, 1, 0);
			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.disableLighting();

			if (rainbowGLCallList == null) {
				RenderRainbow.renderRainbow(0.0F, 50);
			} else {
				GlStateManager.callList(this.rainbowGLCallList);
			}
			GlStateManager.enableLighting();

			GlStateManager.color(1F, 1F, 1F, 1F);
			GlStateManager.popMatrix();
		}

		@SideOnly(Side.CLIENT)
		private void initialize() {
			initialized = true;
			if (Mystcraft.fastRainbows) {
				this.rainbowGLCallList = GLAllocation.generateDisplayLists(1);
				GlStateManager.glNewList(this.rainbowGLCallList, GL11.GL_COMPILE);
				GlStateManager.pushMatrix();
				RenderRainbow.renderRainbow(0.0F, 50);
				GlStateManager.popMatrix();
				GlStateManager.glEndList();
			}
		}
	}
}
