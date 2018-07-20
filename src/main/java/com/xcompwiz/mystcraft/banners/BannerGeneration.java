package com.xcompwiz.mystcraft.banners;

import java.awt.Rectangle;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.client.render.PageBuilder;
import com.xcompwiz.mystcraft.client.render.RenderUtils;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BannerGeneration {

	private static final Rectangle rectangle = new Rectangle(0, 20, 64, 64);

	private static BufferedImage buildBackground() {
		ResourceLocation originalBackground = BannerTextures.BANNER_BASE_TEXTURE;
		try (InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(originalBackground).getInputStream()) {
			BufferedImage image = ImageIO.read(is);
			return RenderUtils.scale(image, 3D, AffineTransformOp.TYPE_NEAREST_NEIGHBOR, BufferedImage.TYPE_4BYTE_ABGR);
		} catch (IOException exc) {
			throw new RuntimeException("Couldn't find or open the page background image.", exc);
		}
	}

	public static BufferedImage createBufferedImage(String word) {
		if (word == null || word.isEmpty())
			return null;

		DrawableWord wordDef = DrawableWordManager.getDrawableWord(word);
		if (wordDef == null)
			return null;

		BufferedImage baseImage = buildBackground();
		BufferedImage wordSource = PageBuilder.buildSymbolImage(DrawableWord.word_components);
		if (baseImage == null) {
			throw new IllegalStateException("Called texture loading outside of TextureManager's loading cycle!");
		}
		PageBuilder.PageSprite.stitchWord(baseImage, wordDef, rectangle, wordSource);

		BufferedImage down = RenderUtils.scale(baseImage, 0.3334D, AffineTransformOp.TYPE_BILINEAR, BufferedImage.TYPE_4BYTE_ABGR);

		for (int y = 0; y < down.getHeight(); ++y) {
			for (int x = 0; x < down.getWidth(); ++x) {
				int rgb = down.getRGB(x, y);
				down.setRGB(x, y, (0xFFFFFFFF - rgb) | (0xFF000000 & rgb));
			}
		}

		return down;
	}

}
