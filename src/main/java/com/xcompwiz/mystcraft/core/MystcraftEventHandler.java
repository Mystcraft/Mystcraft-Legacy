package com.xcompwiz.mystcraft.core;

import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.data.ModFluids;
import com.xcompwiz.mystcraft.debug.DebugHierarchy.DebugNode;
import com.xcompwiz.mystcraft.debug.DebugUtils;
import com.xcompwiz.mystcraft.effects.EffectCrumble;
import com.xcompwiz.mystcraft.entity.EntityUtils;
import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

public class MystcraftEventHandler {

	@SubscribeEvent
	public void registerOre(OreRegisterEvent event) {
		if (event.getName().startsWith("ore") || event.getName().startsWith("gem") || event.getName().startsWith("dust")) {
			if (event.getOre().getItem() instanceof ItemBlock) {
				ItemBlock itemblock = ((ItemBlock) event.getOre().getItem());
				EffectCrumble.registerMapping(itemblock.block, Blocks.STONE.getDefaultState());
			}
		}
	}

	@SubscribeEvent
	public void bucketFix(FillBucketEvent event) {
		RayTraceResult rtr = event.getTarget();
		if (rtr == null || rtr.typeOfHit != RayTraceResult.Type.BLOCK) return;
		BlockPos hit = rtr.getBlockPos();
		if(event.getWorld().getBlockState(hit).getBlock().equals(ModBlocks.black_ink)) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void entityInteract(PlayerInteractEvent.EntityInteract event) {
		if (VillagerTradeSystem.onVillagerInteraction(event)) event.setCanceled(true);
	}

	@SubscribeEvent
	public void bottleFix(PlayerInteractEvent.RightClickItem event) {
		ItemStack itemstack = event.getItemStack();
		if (itemstack.isEmpty()) return;
		if (!(itemstack.getItem() instanceof ItemGlassBottle)) return;
		RayTraceResult rtr = EntityUtils.getMovingObjectPositionFromPlayer(event.getWorld(), event.getEntityPlayer(), true);
		if (rtr == null || rtr.typeOfHit != RayTraceResult.Type.BLOCK) return;
		BlockPos hit = rtr.getBlockPos();
		if (event.getWorld().getBlockState(hit).getBlock().equals(ModBlocks.black_ink)) {
			event.setResult(Event.Result.DENY);
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.FAIL);
		}
	}

	@SubscribeEvent
	public void onEntityAttack(LivingAttackEvent event) {
		WorldProvider provider = event.getEntity().world.provider;
		if (provider instanceof WorldProviderMyst) {
			if (event.getSource().getSourceOfDamage() instanceof EntityPlayer && event.getEntity() instanceof EntityPlayer) {
				if (!((WorldProviderMyst) provider).isPvPEnabled()) {
					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void handleWorldLoadEvent(WorldEvent.Load event) {
		WorldProvider provider = event.getWorld().provider;
		if (provider instanceof WorldProviderMyst) {
			((WorldProviderMyst) provider).setWorldInfo();
			if (event.getWorld().isRemote) return;

			DebugNode node = DebugUtils.getDebugNodeForAge(((WorldProviderMyst) provider).agedata);
			((WorldProviderMyst) provider).getAgeController().registerDebugInfo(node);
		}
	}

	@SubscribeEvent
	public void handleWorldUnloadEvent(WorldEvent.Unload event) {
		WorldProvider provider = event.getWorld().provider;
		if (provider instanceof WorldProviderMyst) {
			if (event.getWorld().isRemote) return;
			DebugNode node = DebugUtils.getDebugNodeForAge(((WorldProviderMyst) provider).agedata);
			node.parent.removeChild(node);
		}
	}
}
