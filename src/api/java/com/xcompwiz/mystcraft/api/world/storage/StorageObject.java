package com.xcompwiz.mystcraft.api.world.storage;

import com.google.common.annotations.Beta;

/**
 * @author xcompwiz
 */
@Beta
public interface StorageObject {

	public abstract boolean getBoolean(String string);

	public abstract void setBoolean(String string, boolean var2);

	public abstract int getInteger(String string);

	public abstract void setInteger(String string, int var2);
}
