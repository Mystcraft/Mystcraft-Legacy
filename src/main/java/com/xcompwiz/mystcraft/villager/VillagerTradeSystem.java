package com.xcompwiz.mystcraft.villager;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.client.gui.GuiHandlerManager;
import com.xcompwiz.mystcraft.client.gui.GuiVillagerShop;
import com.xcompwiz.mystcraft.inventory.ContainerVillagerShop;
import com.xcompwiz.mystcraft.network.NetworkUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class VillagerTradeSystem {

	public static class GuiHandlerVillager extends GuiHandlerManager.GuiHandler {
		@Override
		public Container getContainer(EntityPlayerMP player, World worldObj, Entity entity) {
			return new ContainerVillagerShop(player.inventory, (EntityVillager) entity);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public GuiScreen getGuiScreen(EntityPlayer player, ByteBuf data) {
			int entityId = data.readInt();
			Entity entity = Mystcraft.sidedProxy.getEntityByID(player.worldObj, entityId);
			if (entity != null && entity instanceof EntityVillager) { return new GuiVillagerShop(player.inventory, (EntityVillager) entity); }
			return null;
		}
	}

	private static final int	GuiID	= GuiHandlerManager.registerGuiNetHandler(new GuiHandlerVillager());

	public static boolean onVillagerInteraction(EntityInteractEvent event) {
		if (event.entityPlayer.worldObj.isRemote) return false;
		if (!(event.target instanceof EntityVillager)) return false;
		EntityVillager villager = (EntityVillager) event.target;
		if (villager.getProfession() != Mystcraft.archivistId) return false;
		NetworkUtils.displayGui(event.entityPlayer, event.entityPlayer.worldObj, GuiID, event.target);
		return true;
	}

}
