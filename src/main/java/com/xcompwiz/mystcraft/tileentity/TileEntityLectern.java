package com.xcompwiz.mystcraft.tileentity;

import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.item.ItemLinking;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.ITickable;
import net.minecraft.world.storage.MapData;

import javax.annotation.Nonnull;

public class TileEntityLectern extends TileEntityBookRotateable implements ITickable {

	@Override
	public void setYaw(int rotation) {
		rotation = rotation - (rotation % 90);
		super.setYaw(rotation);
	}

	@Override
	public boolean canAcceptItem(int slot, @Nonnull ItemStack stack) {
		if (stack.isEmpty())
			return false;
		return stack.getItem() instanceof ItemLinking || stack.getItem() == ModItems.page || stack.getItem() == Items.FILLED_MAP;
	}

	@Override
	public void update() {
		if (!world.isRemote) {
			ItemStack display = getDisplayItem();
			if (!display.isEmpty() && display.getItem() instanceof ItemMap) {
				MapData md = Items.FILLED_MAP.getMapData(display, world);
				if (md != null) {
					for (EntityPlayer playerEntity : world.playerEntities) {
						EntityPlayerMP pl = (EntityPlayerMP) playerEntity;
						md.getMapInfo(pl);

						Packet<?> update = Items.FILLED_MAP.createMapDataPacket(display, world, pl);
						if (update != null) {
							pl.connection.sendPacket(update);
						}
					}
				}
			}
		}
	}

}
