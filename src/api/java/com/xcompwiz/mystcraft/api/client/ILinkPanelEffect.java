package com.xcompwiz.mystcraft.api.client;

import com.xcompwiz.mystcraft.api.hook.RenderAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * This interface allows for you to add render layers to the link panel in a book. Register it through the {@link RenderAPI}.
 * @author xcompwiz
 */
@SideOnly(Side.CLIENT)
public interface ILinkPanelEffect {

	/**
	 * Called when the rendering should occur
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param linkInfo The info of the link for the panel being rendered
	 * @param bookclone A clone instance of the book itemstack
	 */
	public void render(int left, int top, int width, int height, ILinkInfo linkInfo, @Nonnull ItemStack bookclone);

	/** Called when the book gui element first opens */
	public void onOpen();
}
