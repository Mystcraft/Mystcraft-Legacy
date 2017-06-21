package com.xcompwiz.mystcraft.client.gui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.data.Assets.GUIs;
import com.xcompwiz.mystcraft.data.Assets.Vanilla;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.page.Page;
import com.xcompwiz.mystcraft.seasonal.SeasonalManager;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public final class GuiUtils {

	//XXX: Probably need to move the symbol drawing code elsewhere
	@SideOnly(Side.CLIENT)
	public static void drawPage(TextureManager renderEngine, float zLevel, @Nonnull ItemStack page, float xSize, float ySize, float x, float y) {
		drawPageBackground(renderEngine, zLevel, page, xSize, ySize, x, y);
		if (Page.getSymbol(page) != null) {
			IAgeSymbol symbol = SymbolManager.getAgeSymbol(Page.getSymbol(page));
			GuiUtils.drawSymbol(renderEngine, zLevel, symbol, xSize - 1, x + 0.5F, y + (ySize + 1 - xSize) / 2);
		} else if (Page.isLinkPanel(page)) {
			drawGradientRect(x + xSize * 0.15F, y + ySize * 0.15F, x + xSize * 0.85F, y + ySize * 0.5F, 0xFF000000, 0xFF000000, zLevel);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void drawPageBackground(TextureManager renderEngine, float zLevel, @Nonnull ItemStack page, float xSize, float ySize, float x, float y) {
		GlStateManager.disableBlend();
		renderEngine.bindTexture(GUIs.book_page_left);
		GlStateManager.color(1F, 1F, 1F, 1F);
		if (page.isEmpty()) {
			GlStateManager.color(0.2F, 0.2F, 0.2F, 0.2F);
		}
		drawTexturedModalRect(x, y, 156, 0, 30, 40, zLevel, xSize, ySize);
	}

	@SideOnly(Side.CLIENT)
	public static void drawSymbol(TextureManager renderEngine, float zLevel, IAgeSymbol symbol, float scale, float x, float y) {
		if (SeasonalManager.drawSymbol(renderEngine, zLevel, symbol, scale, x, y)) {
			return;
		}
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
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
		if (words.length > 0) drawWord(renderEngine, zLevel, DrawableWordManager.getDrawableWord(words[0]), 2 * s, x + o,        y);
		if (words.length > 1) drawWord(renderEngine, zLevel, DrawableWordManager.getDrawableWord(words[1]), 2 * s, x + o * 2, y + o);
		if (words.length > 2) drawWord(renderEngine, zLevel, DrawableWordManager.getDrawableWord(words[2]), 2 * s, x + o,     y + o * 2);
		if (words.length > 3) drawWord(renderEngine, zLevel, DrawableWordManager.getDrawableWord(words[3]), 2 * s,    x,         y + o);
		GlStateManager.disableBlend();
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
		if (imagesource == null) {
			imagesource = DrawableWord.word_components;
		}
		renderEngine.bindTexture(imagesource);
		if (components == null || components.size() == 0) { // No drawable -> ? image
			components = new ArrayList<>();
			components.add(0);
			colors = new ArrayList<>();
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
		GlStateManager.color(fRed, fGreen, fBlue, 1F);
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		vb.pos(x + 0,         y + drawscale, zLevel).tex((iconX + 0)        * transform, (iconY + iconSize) * transform).endVertex();
		vb.pos(x + drawscale, y + drawscale, zLevel).tex((iconX + iconSize) * transform, (iconY + iconSize) * transform).endVertex();
		vb.pos(x + drawscale, y + 0,         zLevel).tex((iconX + iconSize) * transform, (iconY + 0)        * transform).endVertex();
		vb.pos(x + 0,         y + 0,         zLevel).tex((iconX + 0)        * transform, (iconY + 0)        * transform).endVertex();
		tes.draw();
	}

	//XXX: This doesn't really belong here
	public static String getHoverText(IAgeSymbol symbol) {
		if (symbol != null) {
			return symbol.getLocalizedName();
		}
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
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos((x + 0),     (y + ySize), zLevel).tex(((u + 0)     * var7), ((v + height) * var8)).endVertex();
        vb.pos((x + xSize), (y + ySize), zLevel).tex(((u + width) * var7), ((v + height) * var8)).endVertex();
        vb.pos((x + xSize), (y + 0),     zLevel).tex(((u + width) * var7), ((v + 0)      * var8)).endVertex();
        vb.pos((x + 0),     (y + 0),     zLevel).tex(((u + 0)     * var7), ((v + 0)      * var8)).endVertex();
		tes.draw();
	}

	@SideOnly(Side.CLIENT)
	public static TextureAtlasSprite getIconSafe(TextureAtlasSprite tas) {
		if (tas == null) {
			tas = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
		}
		return tas;
	}

	@SideOnly(Side.CLIENT)
	public static void drawIcon(int x, int y, TextureAtlasSprite tas, int xSize, int ySize, double zLevel) {
		if (tas == null) {
			LoggerUtils.warn("Error attepting to render icon: null icon object.");
			return;
		}
		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos((x + 0),     (y + ySize), zLevel).tex(tas.getMinU(), tas.getMaxV()).endVertex();
        vb.pos((x + xSize), (y + ySize), zLevel).tex(tas.getMaxU(), tas.getMaxV()).endVertex();
        vb.pos((x + xSize), (y + 0),     zLevel).tex(tas.getMaxU(), tas.getMinV()).endVertex();
        vb.pos((x + 0),     (y + 0),     zLevel).tex(tas.getMinU(), tas.getMinV()).endVertex();
		tes.draw();
	}

	@SideOnly(Side.CLIENT)
	public static void drawIconRepeating(TextureManager render, TextureAtlasSprite tas, int color, int left, int top, int width, int height, float zLevel) {
		float red, green, blue;
		red = (color >> 16 & 255) / 255.0F;
		green = (color >> 8 & 255) / 255.0F;
		blue = (color & 255) / 255.0F;
		int x = 0;
		int y = 0;
		int drawHeight = 0;
		int drawWidth = 0;
		GlStateManager.color(red, green, blue, 1F);
		for (x = 0; x < width; x += 16) {
			for (y = 0; y < height; y += 16) {
				drawWidth = Math.min(width - x, 16);
				drawHeight = Math.min(height - y, 16);
				drawIcon(left + x, top + y, tas, drawWidth, drawHeight, zLevel);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public static void drawFluid(TextureManager render, Fluid fluid, int left, int top, int width, int height, float zLevel) {
        TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getStill().toString());
		if (tas == null) {
			LoggerUtils.warn("Error attepting to render fluid (%s): null icon object.", fluid.getName());
			return;
		}
		int color = fluid.getColor();
		render.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		drawIconRepeating(render, tas, color, left, top, width, height, zLevel);
	}

	@SideOnly(Side.CLIENT)
	public static void drawFluid(TextureManager render, FluidStack fluidstack, int left, int top, int width, int height, float zLevel) {
		Fluid fluid = fluidstack.getFluid();
        TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(fluid.getStill(fluidstack).toString());
		if (tas == null) {
			LoggerUtils.warn("Error attepting to render fluid (%s): null icon object.", fluid.getName());
			return;
		}
		int color = fluid.getColor(fluidstack);
		render.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		drawIconRepeating(render, tas, color, left, top, width, height, zLevel);
	}

	@SideOnly(Side.CLIENT)
	public static void drawTooltip(FontRenderer fontRenderer, int xcoord, int ycoord, float zLevel, List<String> list, int maxwidth, int maxheight) {
		if (list.size() > 0) {
		    GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();

			int width = 0;
            for (String aList : list) {
                int strLength = fontRenderer.getStringWidth(aList);
                if (strLength > width) {
                    width = strLength;
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
            for (String str : list) {
                str = (new StringBuilder()).append("\247F").append(str).toString();
                fontRenderer.drawStringWithShadow(str, xcoord, ycoord, -1);
                ycoord += 10;
            }

			zLevel = 0.0F;
			// itemRenderer.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
			RenderHelper.enableGUIStandardItemLighting();
		}
	}

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors.
	 */
	@SideOnly(Side.CLIENT)
	public static void drawGradientRect(float par1, float par2, float par3, float par4, int color1, int color2, float zLevel) {
		float alpha1  = (color1 >> 24 & 255) / 255.0F;
		float red1  = (color1 >> 16 & 255) / 255.0F;
		float green1  = (color1 >> 8 & 255) / 255.0F;
		float blue1 = (color1 & 255) / 255.0F;

		float alpha2 = (color2 >> 24 & 255) / 255.0F;
		float red2 = (color2 >> 16 & 255) / 255.0F;
		float green2 = (color2 >> 8 & 255) / 255.0F;
		float blue2 = (color2 & 255) / 255.0F;

		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		Tessellator tes = Tessellator.getInstance();
		VertexBuffer vb = tes.getBuffer();
		vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vb.pos(par3, par2, zLevel).color(red1, green1, blue1, alpha1).endVertex();
        vb.pos(par1, par2, zLevel).color(red1, green1, blue1, alpha1).endVertex();
        vb.pos(par1, par4, zLevel).color(red2, green2, blue2, alpha2).endVertex();
        vb.pos(par3, par4, zLevel).color(red2, green2, blue2, alpha2).endVertex();
		tes.draw();

        GlStateManager.shadeModel(GL11.GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
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
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos((x + 0),  (y + 18), zLevel).tex(((xOffset + 0)  * 0.0078125F), ((yOffset + 18) * 0.0078125F)).endVertex();
        vb.pos((x + 18), (y + 18), zLevel).tex(((xOffset + 18) * 0.0078125F), ((yOffset + 18) * 0.0078125F)).endVertex();
        vb.pos((x + 18), (y + 0),  zLevel).tex(((xOffset + 18) * 0.0078125F), ((yOffset + 0)  * 0.0078125F)).endVertex();
        vb.pos((x + 0),  (y + 0),  zLevel).tex(((xOffset + 0)  * 0.0078125F), ((yOffset + 0)  * 0.0078125F)).endVertex();
        tes.draw();
	}

	@SideOnly(Side.CLIENT)
	public static void drawSprite(int x, int y, int xSize, int ySize, int xOffset, int yOffset, float zLevel) {
		Minecraft.getMinecraft().renderEngine.bindTexture(Vanilla.slot_tex);
        Tessellator tes = Tessellator.getInstance();
        VertexBuffer vb = tes.getBuffer();
        vb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        vb.pos((x + 0),     (y + ySize), zLevel).tex(((xOffset + 0)  * 0.0078125F), ((yOffset + 18) * 0.0078125F)).endVertex();
        vb.pos((x + xSize), (y + ySize), zLevel).tex(((xOffset + 18) * 0.0078125F), ((yOffset + 18) * 0.0078125F)).endVertex();
        vb.pos((x + xSize), (y + 0),     zLevel).tex(((xOffset + 18) * 0.0078125F), ((yOffset + 0)  * 0.0078125F)).endVertex();
        vb.pos((x + 0),     (y + 0),     zLevel).tex(((xOffset + 0)  * 0.0078125F), ((yOffset + 0)  * 0.0078125F)).endVertex();
        tes.draw();
	}

	// TODO: (Visuals) Padding and alignment for scaled text
	@SideOnly(Side.CLIENT)
	public static void drawScaledText(String text, int x, int y, int width, int height, int textcolor) {
	    GlStateManager.pushMatrix();
		float scale = 1;
		int xPad = 0, yPad = 0;
		int textWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text);
		if (textWidth > width) {
			scale = (float) width / (float) textWidth;
		}
		GlStateManager.translate(x + xPad, y + yPad, 0);
		GlStateManager.scale(scale, scale, 1);
		Minecraft.getMinecraft().fontRendererObj.drawString(text, 0, 0, textcolor);
		GlStateManager.popMatrix();
	}

	private static List<int[]>	scissors	= new LinkedList<>();

	/**
	 * Clips rendering from top left corner, using Minecraft GUI coords. Don't forget to call endGlScissor after rendering. Edited by XCompWiz to support
	 * push/pop registry style usage.
	 * @authors iChun, XCompWiz
	 */
	//TODO: use a push/pop system, allowing for nested clipping settings
	public static void startGlScissor(int guiLeft, int guiTop, int xSize, int ySize) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution reso = new ScaledResolution(mc);
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
