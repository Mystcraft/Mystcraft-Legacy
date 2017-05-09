package com.xcompwiz.mystcraft.tileentity;

/**
 * Implement this on a tileentity that should receive updates from the underlying {@link IOInventory}
 * Apply the listener upon inventory construction with {@link IOInventory#setListener(InventoryUpdateListener)}
 */
public interface InventoryUpdateListener {

    public void onChange();

}
