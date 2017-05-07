package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Adds a celestial body to the age. Ages can have any number of these.
 * @author XCompWiz
 */
public interface ICelestial {

	/**
	 * This is used to indicate if the object is a "solar" object or not. If set to true, the object will provide as much light as a sun at the reported
	 * celestial angle.   Only read once on registration, do not treat as a dynamic field.
	 * @return True if it should be treated as a sun object.
	 */
	public abstract boolean providesLight();

	/**
	 * Returns the position in the period of the (light-giving) celestial body. 0 is noon, 0.5 is midnight. As the object progresses thought the sky, the number
	 * increases. At midnight it reaches 0.5, after which it continues increasing until it reaches noon, where it loops back to 0. In practice, this is used to
	 * determine how bright the world should be. Note that this should map to the altitude angle of the body, so declination can affect the value.
	 * @return The normalized period position of the object
	 */
	public abstract float getAltitudeAngle(long time, float partialTime);

	/**
	 * Returns the number of ticks until this solar object is at 0.75 (celestial angle position = dawn). Can return null if the celestial doesn't provide light, never
	 * rises (this high), or if the rising time is unknown.
	 * @return The number of ticks from now before the solar object will rise to 0.75 celestial altitude
	 */
	public abstract Long getTimeToDawn(long time);

	/**
	 * The celestial body's render pass
	 */
	@SideOnly(Side.CLIENT)
	public abstract void render(TextureManager textureManager, World worldObj, float partialTicks);
}
