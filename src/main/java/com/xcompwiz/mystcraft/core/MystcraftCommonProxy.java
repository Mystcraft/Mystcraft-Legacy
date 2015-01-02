package com.xcompwiz.mystcraft.core;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.FMLCommonHandler;

public class MystcraftCommonProxy {
	/**
	 * Writes a compressed NBTTagCompound to the OutputStream
	 * @throws IOException
	 */
	//XXX: This might belong in a helper
	public static void writeNBTTagCompound(NBTTagCompound par0NBTTagCompound, ByteBuf data) throws IOException {
		if (par0NBTTagCompound == null) {
			data.writeShort(-1);
		} else {
			byte[] var2 = CompressedStreamTools.compress(par0NBTTagCompound);
			data.writeShort((short) var2.length);
			data.writeBytes(var2);
		}
	}

	public Entity getEntityByID(World worldObj, int id) {
		if (worldObj instanceof WorldServer) return ((WorldServer) worldObj).getEntityByID(id);
		return null;
	}

	public MinecraftServer getMCServer() {
		return FMLCommonHandler.instance().getMinecraftServerInstance(); // Works when remote?
	}

	//XXX: This might belong in a helper
	public static int findInInventory(IInventory inventory, ItemStack stack) {
		if (inventory == null) return -1;
		if (stack == null) return -1;
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			if (inventory.getStackInSlot(i) == stack) return i;
		}
		return -1;
	}

	public void spawnParticle(String particle, double x, double y, double z, double motionX, double motionY, double motionZ) {}

	public void preinit() {}

	public void init() {}

	public void postInit() {}

	public void createCreativeTabs() {}
}
