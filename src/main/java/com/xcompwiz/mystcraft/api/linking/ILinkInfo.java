package com.xcompwiz.mystcraft.api.linking;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import com.xcompwiz.mystcraft.api.event.LinkEvent;
import com.xcompwiz.mystcraft.api.hook.LinkingAPI;

/**
 * This is an object describing all the information needed to link an entity using the Mystcraft teleport mechanics. Conversion functions to and from
 * NBTTagCompounds are provided by the API so that the description is easy to store. Unless you want to do something very specific with linking, you should not
 * implement this yourself. Instead, get a valid link object using the {@link LinkingAPI}
 */
public interface ILinkInfo {
	/**
	 * Gets the 'name' of the link Primarily used for Mystcraft books and linking items Default is the Dimension's name
	 */
	String getDisplayName();

	/**
	 * Sets the 'name' of the link Primarily used for Mystcraft books and linking items Default is the Dimension's name
	 */
	void setDisplayName(String displayname);

	/**
	 * Gets the destination's Dimension identifier.
	 */
	Integer getDimensionUID();

	/**
	 * Sets the destination's Dimension identifier. This is the dim id in the world provider.
	 */
	void setDimensionUID(int uid);

	/**
	 * Gets the destination's unique identifier. This allows reusing dimension ids within Mystcraft.
	 * @return A UUID instance which is bound to the dimension. May be null.
	 */
	UUID getTargetUUID();

	/**
	 * Sets the destination's unique identifier. Use getDimensionUUID(World worldObj) to get the dimension's UUID.
	 */
	void setTargetUUID(UUID uuid);

	/**
	 * Gets the point which the link targets. If null then the link mechanics will use the dimension's spawn point.
	 */
	ChunkCoordinates getSpawn();

	/**
	 * Sets the point which the link targets. If null then the link mechanics will use the dimension's spawn point.
	 */
	void setSpawn(ChunkCoordinates spawn);

	/**
	 * Gets the direction the linked entity will face once linked.
	 */
	float getSpawnYaw();

	/**
	 * Sets the direction the linked entity will face once linked.
	 */
	void setSpawnYaw(float spawnyaw);

	/**
	 * Gets a flag for the link It is possible to bind flags to the link info Some flags are already listened for, but it is possible to add your own and listen
	 * for them using the {@link LinkEvent} events See ILinkPropertyAPI for some normal link flags
	 * @return Returns the value of the flag, or false if not set
	 */
	boolean getFlag(String flag);

	/**
	 * Sets a flag for the link It is possible to bind flags to the link info Some flags are already listened for, but it is possible to add your own and listen
	 * for them using the {@link LinkEvent} events See ILinkPropertyAPI for some normal link flags
	 */
	void setFlag(String flag, boolean value);

	/**
	 * Gets a property value for the link It is possible to bind properties to the link info Some properties are already listened for, but it is possible to add
	 * your own and listen for them using the {@link LinkEvent} events Currently only PROP_SOUND is used See ILinkPropertyAPI
	 * @return The value of the property. Defaults to null if nothing is set.
	 */
	String getProperty(String flag);

	/**
	 * Sets a property value on the link info It is possible to bind properties to the link info Some properties are already listened for, but it is possible to
	 * add your own and listen for them using the {@link LinkEvent} events Currently only PROP_SOUND is used See ILinkPropertyAPI
	 */
	void setProperty(String flag, String value);

	/**
	 * Returns an NBTTagCompound which represents this link. This is so that you can convert the link info to a save-able format. This function is not used by
	 * the ILinkingAPI, so if you implement this interface you do not need to worry about matching the internal formats.
	 */
	NBTTagCompound getTagCompound();

	/**
	 * Clones the info object, creating a separate set of data (changes to one do not reflect in the other). This is used internally to copy a link object so
	 * that you may implement this interface.
	 * @return A new ILinkInfo object with all of the same properties as this one. The object is not backed by this one.
	 */
	ILinkInfo clone();
}
