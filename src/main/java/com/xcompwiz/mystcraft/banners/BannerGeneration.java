package com.xcompwiz.mystcraft.banners;

import java.awt.Rectangle;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

import javax.imageio.ImageIO;

import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.client.render.PageBuilder;
import com.xcompwiz.mystcraft.client.render.RenderUtils;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.BufferedImageLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BannerGeneration {

	private static final Rectangle rectangle = new Rectangle(48, 48, 64, 64);

	private static BufferedImage buildBackground() {
		ResourceLocation originalBackground = BannerTextures.BANNER_BASE_TEXTURE;
		try (InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(originalBackground).getInputStream()) {
			BufferedImage image = ImageIO.read(is);
			return RenderUtils.scale(image, 5D, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		} catch (IOException exc) {
			throw new RuntimeException("Couldn't find or open the page background image.", exc);
		}
	}

//	@SubscribeEvent
//	public void onTextureStitch(TextureStitchEvent.Pre event) {
//		TextureMap tm = event.getMap();
//		Map<String, DrawableWord> wordDefs = DrawableWordManager.getWords();
//		Set<String> words = wordDefs.keySet();
//		for (String word : words) {
//			String res = getFilePathForWord(word);
//			tm.setTextureEntry(new NarayanWordSprite(res, word));
//		}
//	}
//
//	@SubscribeEvent
//	public void onTextureStitchPost(TextureStitchEvent.Post event) {
//	}
//
//	private String getFilePathForWord(String word) {
//		return "minecraft:textures/entity/banner/" + MystObjects.MystcraftModId + "_" + word;
//	}

	public static class NarayanWordSprite extends TextureAtlasSprite {

		private String word;

		protected NarayanWordSprite(String path, String word) {
			super(path);
			this.word = word;
			this.width = 128;
			this.height = 128;
		}

		@Override
		public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
			return true;
		}

		@Override
		public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
			BufferedImage baseImage = buildBackground();
			BufferedImage wordSource = PageBuilder.buildSymbolImage(DrawableWord.word_components);
			if (baseImage == null) {
				throw new IllegalStateException("Called texture loading outside of TextureManager's loading cycle!");
			}
			ColorModel cm = baseImage.getColorModel();
			//160x160 here
			BufferedImage copy = new BufferedImage(cm, baseImage.copyData(null), cm.isAlphaPremultiplied(), null);

			DrawableWord wordDef = DrawableWordManager.getDrawableWord(word);
			PageBuilder.PageSprite.stitchWord(copy, wordDef, rectangle, wordSource);

			BufferedImage down = RenderUtils.scale(copy, 0.4, AffineTransformOp.TYPE_BILINEAR);
			int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
			pixels[0] = new int[down.getWidth() * down.getHeight()];
			down.getRGB(0, 0, down.getWidth(), down.getHeight(), pixels[0], 0, down.getWidth());
			clearFramesTextureData();
			this.framesTextureData.add(pixels);
			return false; //false = stitch onto atlas
		}
	}

	@SubscribeEvent
	public void getBufferedImage(BufferedImageLoadEvent event) {
		if (!event.resourceLocation.contains("mystcraft_"))
			return;
		
		String word = event.resourceLocation.substring(33).replace(".png", "");
		if (word == null)
			return;

		DrawableWord wordDef = DrawableWordManager.getDrawableWord(word);
		if (wordDef == null)
			return;

		BufferedImage baseImage = buildBackground();
		BufferedImage wordSource = PageBuilder.buildSymbolImage(DrawableWord.word_components);
		if (baseImage == null) {
			throw new IllegalStateException("Called texture loading outside of TextureManager's loading cycle!");
		}
		ColorModel cm = baseImage.getColorModel();
		//160x160 here
		BufferedImage copy = new BufferedImage(cm, baseImage.copyData(null), cm.isAlphaPremultiplied(), null);

		PageBuilder.PageSprite.stitchWord(copy, wordDef, rectangle, wordSource);

		BufferedImage down = RenderUtils.scale(copy, 0.4, AffineTransformOp.TYPE_BILINEAR);
		event.setResultBufferedImage(down);
	}
}
