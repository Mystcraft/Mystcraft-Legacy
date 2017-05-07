package com.xcompwiz.mystcraft.api.hook;

import com.xcompwiz.mystcraft.api.client.ILinkPanelEffect;
import com.xcompwiz.mystcraft.api.util.Color;

import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Exposes functions for rendering Mystcraft elements, such as symbols, words, and colors
 * @author xcompwiz
 */
public interface RenderAPI {

	/**
	 * Use this to register your own rendering effects on the linkpanel
	 * @param The renderer to register
	 */
	@SideOnly(Side.CLIENT)
	void registerRenderEffect(ILinkPanelEffect renderer);

	/**
	 * Draws a Narayan word from the word list. If the word does not exist, it will be generated based on the string. Note that you can also draw D'ni numbers
	 * by using for example "1" as the word. This works for 0-26.
	 * @param x The x location in pixels
	 * @param y The y location in pixels
	 * @param zLevel The zLevel we're drawing on
	 * @param scale The size in pixels of the square
	 * @param word The word to draw
	 */
	@SideOnly(Side.CLIENT)
	public void drawWord(float x, float y, float zLevel, float scale, String word);

	/**
	 * Can be used to draw a symbol somewhere
	 * @param x The x location in pixels
	 * @param y The y location in pixels
	 * @param zLevel The zLevel we're drawing on
	 * @param scale The size in pixels of the square
	 * @param identifier The string identifier of the symbol to draw
	 */
	@SideOnly(Side.CLIENT)
	public void drawSymbol(float x, float y, float zLevel, float scale, String identifier);

	/**
	 * Draws a D'ni color "eye". The eye will be in and represent the provided color.
	 * @param x The x location in pixels
	 * @param y The y location in pixels
	 * @param zLevel The zLevel we're drawing on
	 * @param radius The size in pixels of the square
	 * @param color The color object to use when drawing the eye
	 */
	@SideOnly(Side.CLIENT)
	public void drawColorEye(float x, float y, float zLevel, float radius, Color color);
}
