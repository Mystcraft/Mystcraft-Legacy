package com.xcompwiz.mystcraft.tileentity;

import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.world.storage.MapData;

import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.item.ItemPage;

public class TileEntityLectern extends TileEntityBookDisplay {

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		if (nbttagcompound.hasKey("Rotation")) {
			int rot = 360 - nbttagcompound.getInteger("Rotation") + 270;
			this.setYaw(rot);
		}
	}

	@Override
	public void setYaw(int rotation) {
		rotation = rotation - (rotation % 90);
		super.setYaw(rotation);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		if (itemstack == null) return false;
		if (itemstack.getItem() instanceof ItemLinking) return true;
		if (itemstack.getItem() == ItemPage.instance) return true;
		if (itemstack.getItem() == Items.filled_map) return true;
		return false;
	}

	@Override
	public String getInventoryName() {
		return "Lectern";
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote) {
			ItemStack displayed = this.getDisplayItem();

			if (displayed != null && displayed.getItem() instanceof ItemMap) {
				MapData mapdata = Items.filled_map.getMapData(displayed, this.worldObj);
				Iterator<EntityPlayer> iter = worldObj.playerEntities.iterator();

				while (iter.hasNext()) {
					EntityPlayer player = iter.next();
					EntityPlayerMP playerMP = (EntityPlayerMP) player;
					mapdata.func_82568_a(playerMP);

					Packet packet = Items.filled_map.func_150911_c(displayed, this.worldObj, playerMP);
					if (packet != null) {
						playerMP.playerNetServerHandler.sendPacket(packet);
					}
				}
			}
		}
	}
}
