package com.xcompwiz.mystcraft.symbol.symbols;

import java.util.Random;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.world.AgeDirector;
import com.xcompwiz.mystcraft.api.world.logic.ISkyDoodad;
import com.xcompwiz.mystcraft.client.render.RenderRainbow;
import com.xcompwiz.mystcraft.symbol.SymbolBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SymbolDoodadRainbow extends SymbolBase {

	@Override
	public void registerLogic(AgeDirector controller, long seed) {
		Number angle = controller.popModifier(ModifierUtils.ANGLE).asNumber();
		controller.registerInterface(new CelestialObject(controller, seed, angle));
	}

	@Override
	public String identifier() {
		return "Rainbow";
	}

	private class CelestialObject implements ISkyDoodad {
		private Random	rand;

		private float	angle;

		private boolean	initialized;
		private Integer	rainbowGLCallList	= null;

		CelestialObject(AgeDirector controller, long seed, Number angle) {
			rand = new Random(seed);
			if (angle == null) {
				angle = rand.nextDouble() * 360.0F;
			}
			this.angle = -angle.floatValue();
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void render(TextureManager eng, World worldObj, float partial) {
			if (!initialized) initialize();

			GL11.glEnable(GL11.GL_BLEND);
			// GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glPushMatrix();

			GL11.glRotatef(angle, 0.0F, 1.0F, 0.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GL11.glDisable(GL11.GL_LIGHTING);
			if (rainbowGLCallList == null) {
				RenderRainbow.renderRainbow(0.0F, 50);
			} else {
				GL11.glCallList(this.rainbowGLCallList);
			}
			GL11.glEnable(GL11.GL_LIGHTING);

			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
			GL11.glPopMatrix();
		}

		@SideOnly(Side.CLIENT)
		private void initialize() {
			initialized = true;
			if (Mystcraft.fastRainbows) {
				this.rainbowGLCallList = GLAllocation.generateDisplayLists(1);
				GL11.glNewList(this.rainbowGLCallList, GL11.GL_COMPILE);
				GL11.glPushMatrix();
				RenderRainbow.renderRainbow(0.0F, 50);
				GL11.glPopMatrix();
				GL11.glEndList();
			}
		}

	}
}
