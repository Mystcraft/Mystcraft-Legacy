package com.xcompwiz.mystcraft.client.gui;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.logging.LoggerUtils;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class GuiHandlerManager {

	public static abstract class GuiHandler {
		public TileEntity getTileEntity(EntityPlayerMP player, World worldObj, int i, int j, int k) {
			return null;
		}

		public Container getContainer(EntityPlayerMP player, World worldObj, TileEntity tileentity, int i, int j, int k) {
			return null;
		}

		public Container getContainer(EntityPlayerMP player, World worldObj, ItemStack itemstack, int slot) {
			return null;
		}

		public Container getContainer(EntityPlayerMP player, World worldObj, Entity entity) {
			return null;
		}

		@SideOnly(Side.CLIENT)
		public abstract GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data);
	}

	private static int							nextHandler	= 0;
	private static HashMap<Integer, GuiHandler>	GuiHandlers	= new HashMap<Integer, GuiHandler>();

	public static int registerGuiNetHandler(GuiHandler handler) {
		int id = nextHandler++;
		GuiHandlers.put(id, handler);
		return id;
	}

	public static GuiHandler getGuiNetHandler(int id) {
		return GuiHandlers.get(id);
	}

	@SideOnly(Side.CLIENT)
	public static void displayGui(EntityPlayer player, int guiID, ByteBuf data) {
		GuiHandler handler = GuiHandlers.get(guiID);
		if (handler == null) {
			LoggerUtils.warn(String.format("Unrecognized gui type id %s", guiID));
			return;
		}
		GuiScreen guiscreen = handler.getGuiScreen(player, data);
		if (guiscreen == null) {
			LoggerUtils.warn(String.format("Gui handler %s did not return a gui screen instance", guiID));
			return;
		}
		FMLClientHandler.instance().getClient().displayGuiScreen(guiscreen);
	}
}
