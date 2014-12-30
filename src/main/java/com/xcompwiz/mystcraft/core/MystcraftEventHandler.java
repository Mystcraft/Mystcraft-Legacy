package com.xcompwiz.mystcraft.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.data.ModFluids;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DebugUtils;
import com.xcompwiz.mystcraft.effects.EffectCrumble;
import com.xcompwiz.mystcraft.entity.EntityUtils;
import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MystcraftEventHandler {

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void textureStitching(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 0) {
			ModFluids.initIcons(event.map);
		}
	}

	@SubscribeEvent
	public void registerOre(OreRegisterEvent event) {
		if (event.Name.startsWith("ore") || event.Name.startsWith("gem") || event.Name.startsWith("dust")) {
			if (event.Ore.getItem() instanceof ItemBlock) {
				ItemBlock itemblock = ((ItemBlock) event.Ore.getItem());
				EffectCrumble.registerMapping(itemblock.field_150939_a, Blocks.stone);
			}
		}
	}

	@SubscribeEvent
	public void bucketFix(FillBucketEvent event) {
		MovingObjectPosition movingobjectposition = event.target;
		if (movingobjectposition.typeOfHit != MovingObjectType.BLOCK) return;
		int i = movingobjectposition.blockX;
		int j = movingobjectposition.blockY;
		int k = movingobjectposition.blockZ;
		if (event.world.getBlock(i, j, k) == ModBlocks.black_ink) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void entityInteract(EntityInteractEvent event) {
		if (VillagerTradeSystem.onVillagerInteraction(event)) event.setCanceled(true);
	}

	@SubscribeEvent
	public void bottleFix(PlayerInteractEvent event) {
		if (event.action != Action.RIGHT_CLICK_AIR) return;
		ItemStack itemstack = event.entityPlayer.inventory.getCurrentItem();
		if (itemstack == null) return;
		if (!(itemstack.getItem() instanceof ItemGlassBottle)) return;
		MovingObjectPosition movingobjectposition = EntityUtils.getMovingObjectPositionFromPlayer(event.world, event.entityPlayer, true);
		if (movingobjectposition == null) return;
		if (movingobjectposition.typeOfHit != MovingObjectType.BLOCK) return;
		int i = movingobjectposition.blockX;
		int j = movingobjectposition.blockY;
		int k = movingobjectposition.blockZ;
		if (event.world.getBlock(i, j, k) == ModBlocks.black_ink) {
			event.useItem = Event.Result.DENY;
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event) {
		WorldProvider provider = event.entity.worldObj.provider;
		if (provider instanceof WorldProviderMyst) {
			if (event.source.getSourceOfDamage() instanceof EntityPlayer && event.entityLiving instanceof EntityPlayer) {
				if (!((WorldProviderMyst) provider).isPvPEnabled()) event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void handleWorldLoadEvent(WorldEvent.Load event) {
		WorldProvider provider = event.world.provider;
		if (provider instanceof WorldProviderMyst) {
			((WorldProviderMyst) provider).setWorldInfo();
			if (event.world.isRemote) return;
			DebugNode node = DebugUtils.getDebugNodeForAge(((WorldProviderMyst) provider).agedata);
			((WorldProviderMyst) provider).getAgeController().registerDebugInfo(node);
		}
	}

	@SubscribeEvent
	public void handleWorldUnloadEvent(WorldEvent.Unload event) {
		WorldProvider provider = event.world.provider;
		if (provider instanceof WorldProviderMyst) {
			if (event.world.isRemote) return;
			DebugNode node = DebugUtils.getDebugNodeForAge(((WorldProviderMyst) provider).agedata);
			node.parent.removeChild(node);
		}
	}
}
