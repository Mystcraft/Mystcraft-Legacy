package com.xcompwiz.mystcraft.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.seasonal.SeasonalManager;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class GuiUtils {

	public static Collection<String> getRegisteredIcons(TextureMap texturemap) {
		try {
			Map<String, IIcon> local = ObfuscationReflectionHelper.getPrivateValue(TextureMap.class, texturemap, "mapUploadedSprites", "field_" + "94252_e");
			return local.keySet();
		} catch (Exception e) {
		}
		return null;
	}

	//XXX: Probably need to move the symbol drawing code elsewhere
	@SideOnly(Side.CLIENT)
	public static void drawPage(TextureManager renderEngine, float zLevel, ItemStack page, float xSize, float ySize, float x, float y) {
		drawPageBackground(renderEngine, zLevel, page, xSize, ySize, x, y);
		if (Page.getSymbol(page) != null) {
			IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(page));
			GuiUtils.drawSymbol(renderEngine, zLevel, symbol, xSize - 1, x + 0.5F, y + (ySize + 1 - xSize) / 2);
		} else if (Page.isLinkPanel(page)) {
			drawGradientRect(x + xSize * 0.15F, y + ySize * 0.15F, x + xSize * 0.85F, y + ySize * 0.5F, 0xFF000000, 0xFF000000, zLevel);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void drawPageBackground(TextureManager renderEngine, float zLevel, ItemStack page, float xSize, float ySize, float x, float y) {
		GL11.glDisable(GL11.GL_BLEND);
		renderEngine.bindTexture(GUIs.book_page_left);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (page == null) GL11.glColor4f(0.2F, 0.2F, 0.2F, 0.2F);
		drawTexturedModalRect(x, y, 156, 0, 30, 40, zLevel, xSize, ySize);
	}

	@SideOnly(Side.CLIENT)
	public static void drawSymbol(TextureManager renderEngine, float zLevel, IAgeSymbol symbol, float scale, float x, float y) {
		if (SeasonalManager.drawSymbol(renderEngine, zLevel, symbol, scale, x, y)) return;
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		if (symbol == null) {
			drawWord(renderEngine, zLevel, null, scale, x, y);
			return;
		}
		scale /= 2;
		float s = scale / (2.4142135623730950488016887242097F);
		float o = s * 1.4142135623730950488016887242097F;
		String[] words = symbol.getPoem();
		if (words == null) {
			drawWord(renderEngine, zLevel, DrawableWordManager.getDrawableWord(null), s * 2, x + o, y + o);
			return;
		}
		if (words.length > 0) drawWord(renderEngine, zLevel, DrawableWordManager.getDrawableWord(words[0]), 2 * s, x + o, y);
		if (words.length > 1) drawWord(renderEngine, zLevel, DrawableWordManager.getDrawableWord(words[1]), 2 * s, x + o * 2, y + o);
		if (words.length > 2) drawWord(renderEngine, zLevel, DrawableWordManager.getDrawableWord(words[2]), 2 * s, x + o, y + o * 2);
		if (words.length > 3) drawWord(renderEngine, zLevel, DrawableWordManager.getDrawableWord(words[3]), 2 * s, x, y + o);
		GL11.glDisable(GL11.GL_BLEND);
	}

	@SideOnly(Side.CLIENT)
	public static void drawWord(TextureManager renderEngine, float zLevel, DrawableWord word, float scale, float x, float y) {
		ArrayList<Integer> components = null;
		ArrayList<Integer> colors = null;
		ResourceLocation imagesource = null;
		if (word != null) {
			components = word.components();
			colors = word.colors();
			imagesource = word.imageSource();
		}
		if (imagesource == null) imagesource = DrawableWord.word_components;
		renderEngine.bindTexture(imagesource);
		if (components == null || components.size() == 0) { // No drawable -> ? image
			components = new ArrayList<Integer>();
			components.add(0);
			colors = new ArrayList<Integer>();
		}
		for (int c = 0; c < components.size(); ++c) {
			int color = 0;
			if (c < colors.size()) {
				color = colors.get(c);
			} else if (colors.size() > 0) {
				color = colors.get(0);
			}
			drawComponent(scale, zLevel, components.get(c), color, x, y);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void drawComponent(float drawscale, float zLevel, int iconIndex, int color, float x, float y) {
		final int iconSize = 64;
		final int imgSize = 512;
		final float transform = 1F / imgSize;
		int iconX = (iconIndex % (imgSize / iconSize)) * iconSize;
		int iconY = (iconIndex / (imgSize / iconSize)) * iconSize;
		float fRed = (color >> 16 & 0xff) / 255F;
		float fGreen = (color >> 8 & 0xff) / 255F;
		float fBlue = (color & 0xff) / 255F;
		GL11.glColor4f(fRed, fGreen, fBlue, 1.0F);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x + 0, y + drawscale, zLevel, (iconX + 0) * transform, (iconY + iconSize) * transform);
		tessellator.addVertexWithUV(x + drawscale, y + drawscale, zLevel, (iconX + iconSize) * transform, (iconY + iconSize) * transform);
		tessellator.addVertexWithUV(x + drawscale, y + 0, zLevel, (iconX + iconSize) * transform, (iconY + 0) * transform);
		tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (iconX + 0) * transform, (iconY + 0) * transform);
		tessellator.draw();
	}

	//XXX: This doesn't really belong here
	public static String getHoverText(IAgeSymbol symbol) {
		if (symbol != null) return symbol.displayName();
		return "?";
	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width, height
	 * @param xSize
	 * @param ySize
	 */
	@SideOnly(Side.CLIENT)
	public static void drawTexturedModalRect(float x, float y, float u, float v, float width, float height, float zLevel, float xSize, float ySize) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((x + 0), (y + ySize), zLevel, ((u + 0) * var7), ((v + height) * var8));
		var9.addVertexWithUV((x + xSize), (y + ySize), zLevel, ((u + width) * var7), ((v + height) * var8));
		var9.addVertexWithUV((x + xSize), (y + 0), zLevel, ((u + width) * var7), ((v + 0) * var8));
		var9.addVertexWithUV((x + 0), (y + 0), zLevel, ((u + 0) * var7), ((v + 0) * var8));
		var9.draw();
	}

	@SideOnly(Side.CLIENT)
	public static IIcon getIconSafe(IIcon icon) {
		if (icon == null) {
			icon = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture)).getAtlasSprite("missingno");
		}
		return icon;
	}

	@SideOnly(Side.CLIENT)
	public static void drawIcon(int x, int y, IIcon icon, int xSize, int ySize, double zLevel) {
		if (icon == null) {
			LoggerUtils.warn("Error attepting to render icon: null icon object.");
			return;
		}
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((x + 0), (y + ySize), zLevel, icon.getMinU(), icon.getMaxV());
		tessellator.addVertexWithUV((x + xSize), (y + ySize), zLevel, icon.getMaxU(), icon.getMaxV());
		tessellator.addVertexWithUV((x + xSize), (y + 0), zLevel, icon.getMaxU(), icon.getMinV());
		tessellator.addVertexWithUV((x + 0), (y + 0), zLevel, icon.getMinU(), icon.getMinV());
		tessellator.draw();
	}

	@SideOnly(Side.CLIENT)
	public static void drawIconRepeating(TextureManager render, IIcon icon, int color, int left, int top, int width, int height, float zLevel) {
		float red, green, blue;
		red = (color >> 16 & 255) / 255.0F;
		green = (color >> 8 & 255) / 255.0F;
		blue = (color & 255) / 255.0F;
		int x = 0;
		int y = 0;
		int drawHeight = 0;
		int drawWidth = 0;
		GL11.glColor4f(red, green, blue, 1.0F);
		for (x = 0; x < width; x += 16) {
			for (y = 0; y < height; y += 16) {
				drawWidth = Math.min(width - x, 16);
				drawHeight = Math.min(height - y, 16);
				drawIcon(left + x, top + y, icon, drawWidth, drawHeight, zLevel);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void drawFluid(TextureManager render, Fluid fluid, int left, int top, int width, int height, float zLevel) {
		IIcon icon = fluid.getIcon();
		if (icon == null) {
			LoggerUtils.warn("Error attepting to render fluid (%s): null icon object.", new Object[] { fluid.getName() });
			return;
		}
		int color = fluid.getColor();
		render.bindTexture(fluid.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
		drawIconRepeating(render, icon, color, left, top, width, height, zLevel);
	}

	@SideOnly(Side.CLIENT)
	public static void drawFluid(TextureManager render, FluidStack fluidstack, int left, int top, int width, int height, float zLevel) {
		Fluid fluid = fluidstack.getFluid();
		IIcon icon = fluid.getIcon(fluidstack);
		if (icon == null) {
			LoggerUtils.warn("Error attepting to render fluid (%s): null icon object.", new Object[] { fluid.getName() });
			return;
		}
		int color = fluid.getColor(fluidstack);
		render.bindTexture(fluid.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
		drawIconRepeating(render, icon, color, left, top, width, height, zLevel);
	}

	@SideOnly(Side.CLIENT)
	public static void drawTooltip(FontRenderer fontRenderer, int xcoord, int ycoord, float zLevel, List<String> list, int maxwidth, int maxheight) {
		if (list.size() > 0) {
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);

			int width = 0;
			for (int k2 = 0; k2 < list.size(); k2++) {
				int i3 = fontRenderer.getStringWidth(list.get(k2));
				if (i3 > width) {
					width = i3;
				}
			}

			int height = 8;
			if (list.size() > 1) {
				height += (list.size() - 1) * 10;
			}

			if ((xcoord + width + 28) >= maxwidth) {
				xcoord -= width + 28;
			}
			if ((ycoord + height + 6) >= maxheight) {
				ycoord -= height + 6;
			}

			zLevel = 300F;
			// itemRenderer.zLevel = 300F;
			int color = 0xf0100010;
			drawGradientRect(xcoord - 3, ycoord - 4, xcoord + width + 3, ycoord - 3, color, color, zLevel);
			drawGradientRect(xcoord - 3, ycoord + height + 3, xcoord + width + 3, ycoord + height + 4, color, color, zLevel);
			drawGradientRect(xcoord - 3, ycoord - 3, xcoord + width + 3, ycoord + height + 3, color, color, zLevel);
			drawGradientRect(xcoord - 4, ycoord - 3, xcoord - 3, ycoord + height + 3, color, color, zLevel);
			drawGradientRect(xcoord + width + 3, ycoord - 3, xcoord + width + 4, ycoord + height + 3, color, color, zLevel);
			int color1 = 0x505000ff;
			int color2 = (color1 & 0xfefefe) >> 1 | color1 & 0xff000000;
			drawGradientRect(xcoord - 3, (ycoord - 3) + 1, (xcoord - 3) + 1, (ycoord + height + 3) - 1, color1, color2, zLevel);
			drawGradientRect(xcoord + width + 2, (ycoord - 3) + 1, xcoord + width + 3, (ycoord + height + 3) - 1, color1, color2, zLevel);
			drawGradientRect(xcoord - 3, ycoord - 3, xcoord + width + 3, (ycoord - 3) + 1, color1, color1, zLevel);
			drawGradientRect(xcoord - 3, ycoord + height + 2, xcoord + width + 3, ycoord + height + 3, color2, color2, zLevel);
			for (int i = 0; i < list.size(); i++) {
				String str = list.get(i);
				str = (new StringBuilder()).append("\247F").append(str).toString();
				fontRenderer.drawStringWithShadow(str, xcoord, ycoord, -1);
				ycoord += 10;
			}

			zLevel = 0.0F;
			// itemRenderer.zLevel = 0.0F;
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			RenderHelper.enableGUIStandardItemLighting();
		}
	}

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors.
	 */
	@SideOnly(Side.CLIENT)
	public static void drawGradientRect(float par1, float par2, float par3, float par4, int color1, int color2, float zLevel) {
		float var7 = (color1 >> 24 & 255) / 255.0F;
		float var8 = (color1 >> 16 & 255) / 255.0F;
		float var9 = (color1 >> 8 & 255) / 255.0F;
		float var10 = (color1 & 255) / 255.0F;
		float var11 = (color2 >> 24 & 255) / 255.0F;
		float var12 = (color2 >> 16 & 255) / 255.0F;
		float var13 = (color2 >> 8 & 255) / 255.0F;
		float var14 = (color2 & 255) / 255.0F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator var15 = Tessellator.instance;
		var15.startDrawingQuads();
		var15.setColorRGBA_F(var8, var9, var10, var7);
		var15.addVertex(par3, par2, zLevel);
		var15.addVertex(par1, par2, zLevel);
		var15.setColorRGBA_F(var12, var13, var14, var11);
		var15.addVertex(par1, par4, zLevel);
		var15.addVertex(par3, par4, zLevel);
		var15.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static boolean contains(int mouseX, int mouseY, int guiLeft, int guiTop, int width, int height) {
		if (mouseX < guiLeft) return false;
		if (mouseX >= guiLeft + width) return false;
		if (mouseY < guiTop) return false;
		if (mouseY >= guiTop + height) return false;
		return true;
	}

	/**
	 * Draws a sprite from /gui/slot.png.
	 */
	@SideOnly(Side.CLIENT)
	public static void drawSprite(int x, int y, int xOffset, int yOffset, float zLevel) {
		Minecraft.getMinecraft().renderEngine.bindTexture(Vanilla.slot_tex);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((x + 0), (y + 18), zLevel, ((xOffset + 0) * 0.0078125F), ((yOffset + 18) * 0.0078125F));
		tessellator.addVertexWithUV((x + 18), (y + 18), zLevel, ((xOffset + 18) * 0.0078125F), ((yOffset + 18) * 0.0078125F));
		tessellator.addVertexWithUV((x + 18), (y + 0), zLevel, ((xOffset + 18) * 0.0078125F), ((yOffset + 0) * 0.0078125F));
		tessellator.addVertexWithUV((x + 0), (y + 0), zLevel, ((xOffset + 0) * 0.0078125F), ((yOffset + 0) * 0.0078125F));
		tessellator.draw();
	}

	@SideOnly(Side.CLIENT)
	public static void drawSprite(int x, int y, int xSize, int ySize, int xOffset, int yOffset, float zLevel) {
		Minecraft.getMinecraft().renderEngine.bindTexture(Vanilla.slot_tex);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((x + 0), (y + ySize), zLevel, ((xOffset + 0) * 0.0078125F), ((yOffset + 18) * 0.0078125F));
		tessellator.addVertexWithUV((x + xSize), (y + ySize), zLevel, ((xOffset + 18) * 0.0078125F), ((yOffset + 18) * 0.0078125F));
		tessellator.addVertexWithUV((x + xSize), (y + 0), zLevel, ((xOffset + 18) * 0.0078125F), ((yOffset + 0) * 0.0078125F));
		tessellator.addVertexWithUV((x + 0), (y + 0), zLevel, ((xOffset + 0) * 0.0078125F), ((yOffset + 0) * 0.0078125F));
		tessellator.draw();
	}

	// TODO: (Visuals) Padding and alignment for scaled text
	@SideOnly(Side.CLIENT)
	public static void drawScaledText(String text, int x, int y, int width, int height, int textcolor) {
		GL11.glPushMatrix();
		float scale = 1;
		int xPad = 0, yPad = 0;
		int textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
		if (textWidth > width) {
			scale = (float) width / (float) textWidth;
		}
		GL11.glTranslatef(x + xPad, y + yPad, 0);
		GL11.glScalef(scale, scale, 1);
		Minecraft.getMinecraft().fontRenderer.drawString(text, 0, 0, textcolor);
		GL11.glPopMatrix();
	}

	private static List<int[]>	scissors	= new LinkedList<int[]>();

	/**
	 * Clips rendering from top left corner, using Minecraft GUI coords. Don't forget to call endGlScissor after rendering. Edited by XCompWiz to support
	 * push/pop registry style usage.
	 * @authors iChun, XCompWiz
	 */
	//TODO: use a push/pop system, allowing for nested clipping settings
	public static void startGlScissor(int guiLeft, int guiTop, int xSize, int ySize) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		double scaleW = mc.displayWidth / reso.getScaledWidth_double();
		double scaleH = mc.displayHeight / reso.getScaledHeight_double();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		int x = (int) Math.floor(guiLeft * scaleW);
		int y = (int) Math.floor(mc.displayHeight - ((guiTop + ySize) * scaleH));
		int width = (int) Math.floor((guiLeft + xSize) * scaleW) - (int) Math.floor(guiLeft * scaleW);
		int height = (int) Math.floor(mc.displayHeight - (guiTop * scaleH)) - (int) Math.floor(mc.displayHeight - ((guiTop + ySize) * scaleH));
		if (scissors.size() > 0) {
			int c[] = scissors.get(0);
			width = Math.min(width + x, c[2] + c[0]);
			height = Math.min(height + y, c[3] + c[1]);
			x = Math.max(x, c[0]);
			y = Math.max(y, c[1]);
			width -= x;
			height -= y;
		}
		scissors.add(0, new int[] { x, y, width, height });
		GL11.glScissor(x, y, width, height); //starts from lower left corner (Minecraft starts from upper left)
	}

	public static void endGlScissor() {
		scissors.remove(0);
		if (scissors.size() == 0) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		} else {
			int c[] = scissors.get(0);
			GL11.glScissor(c[0], c[1], c[2], c[3]); //starts from lower left corner (Minecraft starts from upper left)
		}

	}
}
