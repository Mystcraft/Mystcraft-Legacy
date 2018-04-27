package com.xcompwiz.mystcraft.seasonal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import com.xcompwiz.mystcraft.api.MystObjects;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SeasonalManager {

	public static final ResourceLocation	egg_components	= new ResourceLocation(MystObjects.MystcraftModId,"textures/eastercomponents.png");

	private static boolean					isEaster;
	private static boolean					isEasterOverride;

	private static Calendar					checkedDate;

	//TODO: This should probably be externalized. I might want some form of event system.
	@SideOnly(Side.CLIENT)
	public static boolean drawSymbol(TextureManager renderEngine, float zLevel, IAgeSymbol symbol, float scale, float x, float y) {
		if (!isEaster()) return false;
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		if (symbol == null) {
			drawEggColors(renderEngine, zLevel, null, scale, x, y, 0xFF000000, new Random());
			return true;
		}
		//TODO: color palette based on symbol id
		//SEE http://stackoverflow.com/questions/43044/algorithm-to-randomly-generate-an-aesthetically-pleasing-color-palette
		drawEggColors(renderEngine, zLevel, null, scale, x, y, 0xFF000000, getRandom(symbol.getRegistryName().toString()));
		String[] words = symbol.getPoem();
		if (words == null) { return true; }
		for (int i = 0; i < 4; ++i) {
			if (words.length > i) drawEggColors(renderEngine, zLevel, "easter_" + words[i], scale, x, y, 0xDA000000, getRandom(words[i]));
		}
		//if (words.length > 1) drawEggColors(renderEngine, zLevel, words[1], scale, x, y, 0xA0000000);
		//if (words.length > 2) drawEggColors(renderEngine, zLevel, words[2], scale, x, y, 0xA0000000);
		//if (words.length > 3) drawEggColors(renderEngine, zLevel, words[3], scale, x, y, 0xA0000000);
		GlStateManager.disableBlend();
		return true;
	}

	private static Random getRandom(String string) {
		if (string != null) return new Random(string.hashCode());
		return new Random();
	}

	@SideOnly(Side.CLIENT)
	public static void drawEggColors(TextureManager renderEngine, float zLevel, String string, float scale, float x, float y, int alpha, Random rand) {
		DrawableWord word = DrawableWordManager.getDrawableWord(string);
		ArrayList<Integer> components = null;
		ArrayList<Integer> colors = null;
		ResourceLocation imagesource = egg_components;
		if (word != null) {
			components = word.components();
			colors = word.colors();
		}
		renderEngine.bindTexture(imagesource);
		if (components == null) components = new ArrayList<>();
		// No drawable -> the 0 component
		if (components.size() == 0) components.add(0);
		if (colors == null) colors = new ArrayList<>();
		for (int c = 0; c < components.size(); ++c) {
			int color = 0;
			if (c < colors.size()) {
				color = colors.get(c);
			} else {
				color = ((rand.nextInt(155) + 100) << 16) | ((rand.nextInt(155) + 100) << 8) | ((rand.nextInt(155) + 100));
				colors.add(color);
			}
			drawComponent(scale, zLevel, components.get(c), color | alpha, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void drawComponent(float drawscale, float zLevel, int iconIndex, int color, float x, float y) {
		final int iconSize = 64;
		final int imgSize = 512;
		final float transform = 1F / imgSize;
		int iconX = (iconIndex % (imgSize / iconSize)) * iconSize;
		int iconY = (iconIndex / (imgSize / iconSize)) * iconSize;
		float fAlpha = (color >> 24 & 0xff) / 255F;
		float fRed = (color >> 16 & 0xff) / 255F;
		float fGreen = (color >> 8 & 0xff) / 255F;
		float fBlue = (color & 0xff) / 255F;
		GlStateManager.color(fRed, fGreen, fBlue, fAlpha);
		Tessellator tes = Tessellator.getInstance();
		BufferBuilder vb = tes.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(x + 0,         y + drawscale, zLevel).tex((iconX + 0)        * transform, (iconY + iconSize) * transform).endVertex();
		vb.pos(x + drawscale, y + drawscale, zLevel).tex((iconX + iconSize) * transform, (iconY + iconSize) * transform).endVertex();
		vb.pos(x + drawscale, y + 0,         zLevel).tex((iconX + iconSize) * transform, (iconY + 0)        * transform).endVertex();
		vb.pos(x + 0,         y + 0,         zLevel).tex((iconX + 0)        * transform, (iconY + 0)        * transform).endVertex();
		tes.draw();
	}

	private static boolean isEaster() {
		checkDateFlags();
		return isEasterOverride || isEaster;
	}

	//TODO: This is too slow a comparison to do many times a render frame. Make an updater.  Only needs to run on date changes, really.
	private static void checkDateFlags() {
		if (checkedDate != null) return;
		GregorianCalendar now = new GregorianCalendar();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
	    if (now.equals(checkedDate)) return;
		checkedDate = now;
		isEaster = now.equals(findEasterDate(now.get(Calendar.YEAR)));
	}

	/**
	 * Compute the day of the year that Easter falls on. Step names E1 E2 etc., are direct references to Knuth, Vol 1, p 155.
	 */
	public static final Calendar findEasterDate(int year) {
		if (year <= 1582) { throw new IllegalArgumentException("Algorithm invalid before April 1583"); }
		int golden, century, x, z, d, epact, n;

		golden = (year % 19) + 1; /* E1: metonic cycle */
		century = (year / 100) + 1; /* E2: e.g. 1984 was in 20th C */
		x = (3 * century / 4) - 12; /* E3: leap year correction */
		z = ((8 * century + 5) / 25) - 5; /* E3: sync with moon's orbit */
		d = (5 * year / 4) - x - 10;
		epact = (11 * golden + 20 + z - x) % 30; /* E5: epact */
		if ((epact == 25 && golden > 11) || epact == 24) epact++;
		n = 44 - epact;
		n += 30 * (n < 21 ? 1 : 0); /* E6: */
		n += 7 - ((d + n) % 7);
		if (n > 31) {/* E7: */
			return new GregorianCalendar(year, 4 - 1, n - 31); /* April */
		}
		return new GregorianCalendar(year, 3 - 1, n); /* March */
	}
}
