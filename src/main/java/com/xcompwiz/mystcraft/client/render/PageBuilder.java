package com.xcompwiz.mystcraft.client.render;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.vecmath.Vector3f;

import com.google.common.collect.ImmutableList;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.words.DrawableWordManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PageBuilder {

	private static final TransformWrapper HELD_ITEM_TRANSFORMS;

	private static BufferedImage pageImage = null;
	private static Map<ResourceLocation, BufferedImage> customSymbolSources = new HashMap<>();

	@SubscribeEvent
	public void renderItemFrameEvent(RenderItemInFrameEvent event) {
		ItemStack itemstack = event.getItem();
		if (!(itemstack.getItem() instanceof ItemPage))
			return;
		event.setCanceled(true);

		GlStateManager.scale(1F, 1F, 1F);
		GlStateManager.rotate(180.f, 0.0F, 1.0F, 0.0F);
		GlStateManager.pushAttrib();
		RenderHelper.enableStandardItemLighting();
		Minecraft.getMinecraft().getRenderItem().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED);
		RenderHelper.disableStandardItemLighting();
		GlStateManager.popAttrib();
	}

	@SubscribeEvent
	public void onTextureStitch(TextureStitchEvent.Pre event) {
		TextureMap tm = event.getMap();
		pageImage = buildBackground();
		buildSymbolImage(DrawableWord.word_components);
		for (IAgeSymbol symbol : SymbolManager.getAgeSymbols()) {
			ModelResourceLocation mrl = ModItems.PageMeshDefinition.instance.getModelLocationForSymbol(symbol);
			ResourceLocation unwrapped = new ResourceLocation(mrl.getResourceDomain(), mrl.getResourcePath());
			tm.setTextureEntry(new PageSprite(unwrapped, symbol));
		}
		{
			ModelResourceLocation mrl = ModItems.PageMeshDefinition.instance.getModelLocationForSymbol(null);
			ResourceLocation unwrapped = new ResourceLocation(mrl.getResourceDomain(), mrl.getResourcePath());
			tm.setTextureEntry(new PageSprite(unwrapped, null));
		}
		{
			ModelResourceLocation mrl = ModItems.PageMeshDefinition.instance.getModelLocationForLinkPanel();
			ResourceLocation unwrapped = new ResourceLocation(mrl.getResourceDomain(), mrl.getResourcePath());
			tm.setTextureEntry(new BasicPageSprite(unwrapped, true));
		}
		{
			ModelResourceLocation mrl = ModItems.PageMeshDefinition.instance.getModelLocationForBlankPage();
			ResourceLocation unwrapped = new ResourceLocation(mrl.getResourceDomain(), mrl.getResourcePath());
			tm.setTextureEntry(new BasicPageSprite(unwrapped, false));
		}
	}

	@SubscribeEvent
	public void onModelBake(ModelBakeEvent event) {
		for (IAgeSymbol symbol : SymbolManager.getAgeSymbols()) {
			bake(event, ModItems.PageMeshDefinition.instance.getModelLocationForSymbol(symbol));
		}
		bake(event, ModItems.PageMeshDefinition.instance.getModelLocationForSymbol(null));
		bake(event, ModItems.PageMeshDefinition.instance.getModelLocationForLinkPanel());
		bake(event, ModItems.PageMeshDefinition.instance.getModelLocationForBlankPage());
	}

	private void bake(ModelBakeEvent event, ModelResourceLocation mrl) {
		ResourceLocation unwrapped = new ResourceLocation(mrl.getResourceDomain(), mrl.getResourcePath());
		IBakedModel model = (new ItemLayerModel(ImmutableList.of(unwrapped))).bake(HELD_ITEM_TRANSFORMS, DefaultVertexFormats.ITEM, location -> Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(unwrapped.toString()));
		event.getModelRegistry().putObject(mrl, model);
	}

	public static BufferedImage buildSymbolImage(ResourceLocation src) {
		if (customSymbolSources.containsKey(src)) {
			return customSymbolSources.get(src);
		}
		try (InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(src).getInputStream()) {
			BufferedImage in = ImageIO.read(is);
			ColorModel cm = in.getColorModel();
			in = new BufferedImage(cm, in.copyData(null), cm.isAlphaPremultiplied(), null);
			customSymbolSources.put(src, in);
			return in;
		} catch (IOException exc) {
			throw new RuntimeException("Couldn't find or open the symbol-containing image.", exc);
		}
	}

	private static BufferedImage buildBackground() {
		ResourceLocation originalBackground = new ResourceLocation(MystObjects.MystcraftModId, "textures/items/page_background.png");
		try (InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(originalBackground).getInputStream()) {
			//The size for each symbol is 64x64 by default.
			//Original is 32x32 - We scale it up to 160x160 so we can fit the symbols properly. we scale it back to 128x128 for stitching later
			BufferedImage image = ImageIO.read(is);
			return RenderUtils.scale(image, 5D, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		} catch (IOException exc) {
			throw new RuntimeException("Couldn't find or open the page background image.", exc);
		}
	}

	public static void freeMemory() {
		if (pageImage != null) {
			pageImage = null;
		}
		customSymbolSources.clear();
	}

	@SubscribeEvent
	public void onTextureStitchPost(TextureStitchEvent.Post event) {
		freeMemory();
	}

	public static class BasicPageSprite extends TextureAtlasSprite {

		private final boolean isPanel;

		private BasicPageSprite(ResourceLocation res, boolean panel) {
			super(res.toString());
			this.width = 128;
			this.height = 128;
			this.isPanel = panel;
		}

		@Override
		public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
			return true;
		}

		@Override
		public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
			boolean unloadAfter = false;
			if (pageImage == null) {
				pageImage = buildBackground();
				unloadAfter = true;
			}
			ColorModel cm = pageImage.getColorModel();
			//160x160 here
			BufferedImage copy = new BufferedImage(cm, pageImage.copyData(null), cm.isAlphaPremultiplied(), null);

			if (isPanel) {
				int width = 110;
				int height = 45;
				int startX = 25;
				int startZ = 30;

				for (int xx = startX; xx <= startX + width; xx++) {
					for (int zz = startZ; zz <= startZ + height; zz++) {
						copy.setRGB(xx, zz, 0xFF000000); //Black
					}
				}
			}

			//128x128 here
			BufferedImage down = RenderUtils.scale(copy, 0.8, AffineTransformOp.TYPE_BILINEAR);
			int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
			pixels[0] = new int[down.getWidth() * down.getHeight()];
			down.getRGB(0, 0, down.getWidth(), down.getHeight(), pixels[0], 0, down.getWidth());
			clearFramesTextureData();
			this.framesTextureData.add(pixels);
			
			if (unloadAfter)
				freeMemory();

			return false; //false = stitch onto atlas
		}

	}

	public static class PageSprite extends TextureAtlasSprite {

		@Nullable
		private final IAgeSymbol symbol;

		private PageSprite(ResourceLocation res, @Nullable IAgeSymbol symbol) {
			super(res.toString());
			this.symbol = symbol;
			this.width = 128;
			this.height = 128;
		}

		@Override
		public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
			return true;
		}

		@Override
		public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
			boolean unloadAfter = false;
			if (pageImage == null) {
				pageImage = buildBackground();
				unloadAfter = true;
			}
			ColorModel cm = pageImage.getColorModel();
			//160x160 here
			BufferedImage copy = new BufferedImage(cm, pageImage.copyData(null), cm.isAlphaPremultiplied(), null);

			if (symbol == null) {
				stitchWord(copy, null, getPageTarget(-1), buildSymbolImage(DrawableWord.word_components));
			} else {
				String[] words = symbol.getPoem();
				if (words == null) {
					stitchWord(copy, null, getPageTarget(-1), buildSymbolImage(DrawableWord.word_components));
				} else {
					DrawableWord word;
					BufferedImage source = buildSymbolImage(DrawableWord.word_components);
					if (words.length > 0) {
						word = DrawableWordManager.getDrawableWord(words[0]);
						stitchWord(copy, word, getPageTarget(0), source);
					}
					if (words.length > 1) {
						word = DrawableWordManager.getDrawableWord(words[1]);
						stitchWord(copy, word, getPageTarget(1), source);
					}
					if (words.length > 2) {
						word = DrawableWordManager.getDrawableWord(words[2]);
						stitchWord(copy, word, getPageTarget(2), source);
					}
					if (words.length > 3) {
						word = DrawableWordManager.getDrawableWord(words[3]);
						stitchWord(copy, word, getPageTarget(3), source);
					}
				}
			}

			//128x128 here
			BufferedImage down = RenderUtils.scale(copy, 0.8, AffineTransformOp.TYPE_BILINEAR);
			int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
			pixels[0] = new int[down.getWidth() * down.getHeight()];
			down.getRGB(0, 0, down.getWidth(), down.getHeight(), pixels[0], 0, down.getWidth());
			clearFramesTextureData();
			this.framesTextureData.add(pixels);
			
			if (unloadAfter)
				freeMemory();

			return false; //false = stitch onto atlas
		}

		public static void stitchWord(BufferedImage targetImage, DrawableWord word, Rectangle targetRct, BufferedImage source) {
			List<Integer> components = null;
			List<Integer> colors = null;
			if (word != null) {
				ResourceLocation src = word.imageSource();
				if (src != null) {
					source = buildSymbolImage(src);
				}
				components = word.components();
				colors = word.colors();
			}
			if (components == null || components.isEmpty()) {
				components = new LinkedList<>();
				components.add(0); //? part of the symbol page.
				colors = new LinkedList<>(); //Black
			}
			for (int i = 0; i < components.size(); i++) {
				int color = 0;
				if (i < colors.size()) {
					color = colors.get(i);
				} else if (colors.size() > 0) {
					color = colors.get(0);
				}
				Integer component = components.get(i);
				int iconX = (component % 8) * 64;
				int iconY = (component / 8) * 64;

				for (int x = 0; x < 64; x++) {
					if (targetRct.x + x < 0)
						continue;
					for (int y = 0; y < 64; y++) {
						if (targetRct.y + y < 0)
							continue;
						int argb = source.getRGB(iconX + x, iconY + y);
						Color c = new Color(argb, true);
						if (c.getAlpha() > 0) {
							int currentArgb = targetImage.getRGB(targetRct.x + x, targetRct.y + y);
							int targetColor = blend(color, argb, currentArgb);
							targetImage.setRGB(targetRct.x + x, targetRct.y + y, targetColor);
						}
					}
				}
			}
		}

		//In order: what we want ideally (white + fully opaque -> this color),
		//          what the symbol image currently has (to be fair, we only care about the alpha value)
		//          what the target image currently has
		private static int blend(int targetInitialColor, int currentColor, int targetColor) {
			// Source/Goal part
			float srcAlpha = (currentColor >> 24 & 0xFF) / 255F;

			float fRed = (targetInitialColor >> 16 & 0xFF) / 255F;
			float fGreen = (targetInitialColor >> 8 & 0xFF) / 255F;
			float fBlue = (targetInitialColor & 0xFF) / 255F;

			// Existing/Limiting part
			float targetRed = (targetColor >> 16 & 0xFF) / 255F;
			float targetGreen = (targetColor >> 8 & 0xFF) / 255F;
			float targetBlue = (targetColor & 0xFF) / 255F;

			float resRed = fRed * srcAlpha + targetRed * (1 - srcAlpha);
			float resGreen = fGreen * srcAlpha + targetGreen * (1 - srcAlpha);
			float resBlue = fBlue * srcAlpha + targetBlue * (1 - srcAlpha);
			resRed = MathHelper.clamp(resRed, 0, 1) * 255;
			resGreen = MathHelper.clamp(resGreen, 0, 1) * 255;
			resBlue = MathHelper.clamp(resBlue, 0, 1) * 255;
			return new Color(MathHelper.clamp((int) resRed, 0, 255), MathHelper.clamp((int) resGreen, 0, 255), MathHelper.clamp((int) resBlue, 0, 255), 255).getRGB();
		}

		//48 offset is centered
		//96 is offset for mov

		//0 = up
		//1 = right
		//2 = down
		//3 = left
		private static Rectangle getPageTarget(int index) {
			switch (index) {
			case 3:
				return new Rectangle(0, 48, 64, 64);
			case 2:
				return new Rectangle(48, 96, 64, 64);
			case 1:
				return new Rectangle(96, 48, 64, 64);
			case 0:
				return new Rectangle(48, 0, 64, 64);
			default:
				return new Rectangle(48, 48, 64, 64); //Centralized
			}
		}
	}

	static {
		TRSRTransformation leftHandFlipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);
		TRSRTransformation thirdPerson = getTransform(0, 3, 1, 0, 0, 0, 0.55f);
		TRSRTransformation firstPerson = getTransform(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);

		TRSRTransformation leftThird = TRSRTransformation.blockCenterToCorner(leftHandFlipX.compose(TRSRTransformation.blockCornerToCenter(thirdPerson)).compose(leftHandFlipX));
		TRSRTransformation leftFirst = TRSRTransformation.blockCenterToCorner(leftHandFlipX.compose(TRSRTransformation.blockCornerToCenter(firstPerson)).compose(leftHandFlipX));

		Map<ItemCameraTransforms.TransformType, TRSRTransformation> transformationMap = new HashMap<>();
		transformationMap.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, leftThird);
		transformationMap.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		transformationMap.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, leftFirst);
		transformationMap.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, firstPerson);
		transformationMap.put(ItemCameraTransforms.TransformType.HEAD, getTransform(0, 13, 7, 0, 180, 0, 1));
		transformationMap.put(ItemCameraTransforms.TransformType.GUI, TRSRTransformation.identity());
		transformationMap.put(ItemCameraTransforms.TransformType.GROUND, getTransform(0, 2, 0, 0, 0, 0, 0.5f));
		transformationMap.put(ItemCameraTransforms.TransformType.FIXED, TRSRTransformation.identity());
		HELD_ITEM_TRANSFORMS = new TransformWrapper(Collections.unmodifiableMap(transformationMap));
	}

	private static TRSRTransformation getTransform(float tx, float ty, float tz, float rx, float ry, float rz, float s) {
		return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(rx, ry, rz)), new Vector3f(s, s, s), null));
	}

	private static class TransformWrapper implements IModelState {

		private final Map<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

		public TransformWrapper(Map<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {
			this.transforms = transforms;
		}

		@Override
		public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
			if (!part.isPresent() || !(part.get() instanceof ItemCameraTransforms.TransformType) || !transforms.containsKey(part.get())) {
				return Optional.empty();
			}
			return Optional.of(transforms.get(part.get()));
		}
	}

}
