package com.xcompwiz.mystcraft.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

/**
 * @author Soaryn For Rendering textures on a patterns
 */
public class ItemRendererMask implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		if (type == ItemRenderType.INVENTORY) return true;
		if (type == ItemRenderType.ENTITY) return true;
		if (type == ItemRenderType.EQUIPPED) return true;
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) return true;
		return false;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		if (type == ItemRenderType.INVENTORY) return false;
		if (type == ItemRenderType.ENTITY) return true;
		if (type == ItemRenderType.EQUIPPED) return false;
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) return false;
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		if (type.equals(ItemRenderType.ENTITY)) {
			GL11.glRotated(90, 0, -1, 0);
			GL11.glTranslated(-.5, -.5, 0);
		} else if (type.equals(ItemRenderType.INVENTORY)) {
			GL11.glScaled(-16, -16, 1);
			GL11.glTranslated(-1, -1, 0);
		}

		GL11.glPushMatrix();
		int passes = stack.getItem().getRenderPasses(stack.getItemDamage());
		for (int i = 0; i < passes; ++i) {
			renderIcon(stack.getItemSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture, stack.getItem().getIcon(stack, 0));
		}
		GL11.glPopMatrix();

		if (stack.getItem() instanceof IMaskRender) {
			IMaskRender mask = (IMaskRender) stack.getItem();
			renderMask(mask.getMaskResource(stack), mask.getMaskIcon(stack), mask.getSubbedResource(stack), mask.getSubbedIcon(stack), mask.getColor(stack), type);
		}

	}

	public void renderIcon(ResourceLocation resloc, IIcon icon) {
		GL11.glPushMatrix();
		GL11.glColor4f(1, 1, 1, 1);
		Minecraft.getMinecraft().renderEngine.bindTexture(resloc);
		renderItemIcon(icon);
		GL11.glPopMatrix();

	}

	public static void renderItemIcon(IIcon icon) {
		renderItemIcon(icon, 0.0625F);
	}

	public static void renderItemIcon(IIcon icon, float z) {
		if (icon == null) { return; }
		ItemRenderer.renderItemIn2D(Tessellator.instance, icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(), icon.getIconWidth(), icon.getIconHeight(), z);
	}

	public static void renderMask(ResourceLocation maskResource, IIcon maskIcon, ResourceLocation maskedResource, IIcon maskedIcon, int maskColor, ItemRenderType type) {
		if (maskIcon == null || maskedIcon == null) { return; }

		GL11.glPushMatrix();
		{
			if (type.equals(ItemRenderType.INVENTORY)) {
				GL11.glScaled(1, 1, .995);
			} else {
				double d = 1.05;
				GL11.glScaled(.996, .996, d);
				GL11.glTranslated(0, 0, (0.0625 * d - 0.0625) / 2);
			}
			GL11.glColor4ub((byte) ((maskColor >> 24) & 0xFF), (byte) ((maskColor >> 16) & 0xFF), (byte) ((maskColor >> 8) & 0xFF), (byte) (maskColor & 0xFF));
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			Minecraft.getMinecraft().renderEngine.bindTexture(maskResource);
			renderItemIcon(maskIcon);

			GL11.glDepthFunc(GL11.GL_EQUAL);
			GL11.glMatrixMode(GL11.GL_TEXTURE);

			Minecraft.getMinecraft().renderEngine.bindTexture(maskedResource);
			renderItemIcon(maskedIcon);

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL11.glPopMatrix();
		GL11.glColor4f(1, 1, 1, 1);
	}

}
