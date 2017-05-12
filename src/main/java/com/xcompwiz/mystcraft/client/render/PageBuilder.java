package com.xcompwiz.mystcraft.client.render;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.words.DrawableWordManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class PageBuilder {

    private static final Gson GSON = (new GsonBuilder())
            .registerTypeAdapter(TRSRTransformation.class, ForgeBlockStateV1.TRSRDeserializer.INSTANCE)
            .create();

    private static BufferedImage pageImage = null;
    private static Map<ResourceLocation, BufferedImage> customSymbolSources = new HashMap<>();

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        TextureMap tm = event.getMap();
        if(Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
            pageImage = buildBackground();
            buildSymbolImage(DrawableWord.word_components);
            for (IAgeSymbol symbol : SymbolManager.getAgeSymbols()) {
                ModelResourceLocation mrl = ModItems.PageMeshDefinition.instance.getModelLocationForSymbol(symbol);
                tm.setTextureEntry(new PageSprite(mrl, symbol));
            }
            ModelResourceLocation mrl = ModItems.PageMeshDefinition.instance.getModelLocationForSymbol(null);
            tm.setTextureEntry(new PageSprite(mrl, null));
        }
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        if(Loader.instance().hasReachedState(LoaderState.POSTINITIALIZATION)) {
            for (IAgeSymbol symbol : SymbolManager.getAgeSymbols()) {
                ModelResourceLocation mrl = ModItems.PageMeshDefinition.instance.getModelLocationForSymbol(symbol);
                IBakedModel model = (new ItemLayerModel(ImmutableList.of(mrl)))
                        .bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM,
                                location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
                event.getModelRegistry().putObject(mrl, model);
            }
            //ModelResourceLocation mrl = ModItems.PageMeshDefinition.instance.getModelLocationForSymbol(null);

        }
    }

    private static BufferedImage buildSymbolImage(ResourceLocation src) {
        if(customSymbolSources.containsKey(src)) {
            return customSymbolSources.get(src);
        }
        try(InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(src).getInputStream()) {
            BufferedImage in = ImageIO.read(is);
            customSymbolSources.put(src, in);
            return in;
        } catch (IOException exc) {
            throw new RuntimeException("Couldn't find or open the symbol-containing image.", exc);
        }
    }

    private static BufferedImage buildBackground() {
        ResourceLocation originalBackground = new ResourceLocation(MystObjects.MystcraftModId, "textures/items/page_background.png");
        try(InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(originalBackground).getInputStream()) {
            //The size for each symbol is 64x64 by default.
            //Original is 32x32 - We scale it up to 160x160 so we can fit the symbols properly. we scale it back to 128x128 for stitching later
            BufferedImage image = ImageIO.read(is);
            return scale(image, 5D, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        } catch (IOException exc) {
            throw new RuntimeException("Couldn't find or open the page background image.", exc);
        }
    }

    private static BufferedImage scale(BufferedImage in, double scale, int scaleOperation) {
        int w = in.getWidth();
        int h = in.getHeight();
        BufferedImage after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, scaleOperation);
        return scaleOp.filter(in, after);
    }

    @SubscribeEvent
    public void onTextureStitchPost(TextureStitchEvent.Post event) {
        if(pageImage != null) {
            pageImage = null; //Free memory.
        }
        customSymbolSources.clear();
    }

    public static class PageSprite extends TextureAtlasSprite {

        @Nullable
        private final IAgeSymbol symbol;

        private PageSprite(ResourceLocation res, @Nullable IAgeSymbol symbol) {
            super(res.toString());
            this.symbol = symbol;
        }

        @Override
        public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
            return true;
        }

        @Override
        public boolean load(IResourceManager manager, ResourceLocation location) {
            if(pageImage == null) {
                throw new IllegalStateException("Called texture loading outside of TextureManager's loading cycle!");
            }
            ColorModel cm = pageImage.getColorModel();
            //160x160 here
            BufferedImage copy =  new BufferedImage(cm, pageImage.copyData(null), cm.isAlphaPremultiplied(), null);

            if(symbol != null) {
                String[] words = symbol.getPoem();
                Graphics2D g = copy.createGraphics();
                if(words == null) {
                    stitchWord(g, null, -1, buildSymbolImage(DrawableWord.word_components));
                } else {
                    DrawableWord word;
                    BufferedImage source = buildSymbolImage(DrawableWord.word_components);
                    if(words.length > 0) {
                        word = DrawableWordManager.getDrawableWord(words[0]);
                        stitchWord(g, word, 0, source);
                    }
                    if(words.length > 1) {
                        word = DrawableWordManager.getDrawableWord(words[1]);
                        stitchWord(g, word, 1, source);
                    }
                    if(words.length > 2) {
                        word = DrawableWordManager.getDrawableWord(words[2]);
                        stitchWord(g, word, 2, source);
                    }
                    if(words.length > 3) {
                        word = DrawableWordManager.getDrawableWord(words[3]);
                        stitchWord(g, word, 3, source);
                    }
                }
                g.dispose();
            }

            //128x128 here
            BufferedImage down = scale(copy, 0.8, AffineTransformOp.TYPE_BILINEAR);
            int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
            pixels[0] = new int[down.getWidth() * down.getHeight()];
            down.getRGB(0, 0, down.getWidth(), down.getHeight(), pixels[0], 0, down.getWidth());
            clearFramesTextureData();
            this.framesTextureData.add(pixels);
            return true; //true = stitch onto atlas
        }

        private void stitchWord(Graphics2D target, DrawableWord word, int index, BufferedImage source) {
            List<Integer> components = null;
            List<Integer> colors = null;
            if(word != null) {
                ResourceLocation src = word.imageSource();
                if(src != null) {
                    source = buildSymbolImage(src);
                }
                components = word.components();
                colors = word.colors();
            }
            if(components == null || components.isEmpty()) {
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

                float fRed =   (color >> 16 & 0xff) / 255F;
                float fGreen = (color >> 8  & 0xff) / 255F;
                float fBlue =  (color       & 0xff) / 255F;
                Rectangle targetRct = getPageTarget(index);
                target.drawImage(source,
                        targetRct.x, targetRct.y, targetRct.x + 64, targetRct.y + 64,
                        iconX, iconY, iconX + 64, iconY + 64,
                        new Color(MathHelper.clamp(fRed, 0, 1), MathHelper.clamp(fGreen, 0, 1), MathHelper.clamp(fBlue, 0, 1), 1F),
                        (img, infoflags, x, y, width, height) -> false); //FIXME Hellfire> check if null can be savely passed into this?... double check this
            }
        }

        //48 offset is centered
        //96 is offset for mov

        //0 = up
        //1 = right
        //2 = down
        //3 = left
        private Rectangle getPageTarget(int index) {
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

}
