package com.xcompwiz.mystcraft.linking;

import java.util.Random;

import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAllow;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventAlter;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventEnd;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventEnterWorld;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventExitWorld;
import com.xcompwiz.mystcraft.api.event.LinkEvent.LinkEventStart;
import com.xcompwiz.mystcraft.api.hook.LinkPropertyAPI;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.entity.EntityLinkbook;
import com.xcompwiz.mystcraft.item.ItemLinkbook;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LinkListenerBasic {

	@SubscribeEvent
	public void isLinkPermitted(LinkEventAllow event) {
		Entity entity = event.entity;
		World world = event.origin;
		ILinkInfo info = event.info;
		if (world.isRemote) { return; }

		Integer dimid = info.getDimensionUID();
		if (dimid == null) {
			event.setCanceled(true); //We'll need to override isLinkPermitted handling for unestablished links
		} else if (entity.isDead || entity.worldObj != world || entity.riddenByEntity != null) {
			event.setCanceled(true);
		} else if (entity.worldObj.provider.dimensionId == dimid && !info.getFlag(LinkPropertyAPI.FLAG_INTRA_LINKING)) {
			event.setCanceled(true);
		} else if (DimensionUtils.isDimensionDead(dimid)) {
			event.setCanceled(true);
		} else if (!DimensionUtils.checkDimensionUUID(dimid, info.getTargetUUID())) {
			event.setCanceled(true);
		} else if (info.getFlag(LinkPropertyAPI.FLAG_DISARM)) {
			if (entity instanceof EntityItem) {
				event.setCanceled(true);
			} else if (entity instanceof EntityLinkbook) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void handleAlterEvent(LinkEventAlter event) {
		Entity entity = event.entity;
		World world = event.origin;
		World newworld = event.destination;
		ILinkInfo info = event.info;

		if (info.getFlag(LinkPropertyAPI.FLAG_RELATIVE)) {
			ChunkCoordinates origin = world.getSpawnPoint();
			float dx = (int) (entity.posX - origin.posX);
			float dy = (int) (entity.posY - origin.posY);
			float dz = (int) (entity.posZ - origin.posZ);
			event.spawn = newworld.getSpawnPoint();
			event.spawn.posX += dx;
			event.spawn.posY += dy;
			event.spawn.posZ += dz;
		}
	}

	@SubscribeEvent
	public void onLinkStart(LinkEventStart event) {
		Entity entity = event.entity;
		ILinkInfo info = event.info;

		if (info.getFlag(LinkPropertyAPI.FLAG_DISARM)) {
			if (entity instanceof EntityPlayer) {
				ejectInventory(entity.worldObj, ((EntityPlayer) entity).inventory, entity.posX, entity.posY, entity.posZ);
			}
			if (entity instanceof IInventory) {
				ejectInventory(entity.worldObj, (IInventory) entity, entity.posX, entity.posY, entity.posZ);
			}
			if (entity instanceof EntityHorse) {
				((EntityHorse) entity).dropChestItems();
			}
			if (entity instanceof EntityLiving) {
				dropEquipment((EntityLiving) entity, new Random());
			}
		}
	}

	@SubscribeEvent
	public void onExitWorld(LinkEventExitWorld event) {
		Entity entity = event.entity;
		ILinkInfo info = event.info;

		handleMomentum(entity, info);
	}

	@SubscribeEvent
	public void onEnterWorld(LinkEventEnterWorld event) {
		Entity entity = event.entity;
		World world = event.destination;

		if (world.provider instanceof WorldProviderMyst && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = player.inventory.getStackInSlot(i);
				if (itemstack != null && itemstack.getItem() instanceof ItemLinkbook) return;
			}
			player.addStat(ModAchievements.quinn, 1);
		}
	}

	@SubscribeEvent
	public void onLinkEnd(LinkEventEnd event) {
		Entity entity = event.entity;
		World world = event.destination;
		ILinkInfo info = event.info;

		ChunkCoordinates spawn = info.getSpawn();
		if (info.getFlag(LinkPropertyAPI.FLAG_GENERATE_PLATFORM) && world.isAirBlock(spawn.posX, spawn.posY - 1, spawn.posZ) && world.isAirBlock(spawn.posX, spawn.posY - 2, spawn.posZ)) {
			world.setBlock(spawn.posX, spawn.posY - 1, spawn.posZ, Blocks.stone, 0, 3);
		}
		if (entity instanceof EntityMinecart) {
			entity.motionX = 0;
			entity.motionZ = 0;
		}
	}

	private static void handleMomentum(Entity entity, ILinkInfo info) {
		if (!info.getFlag(LinkPropertyAPI.FLAG_MAINTAIN_MOMENTUM)) {
			entity.motionX = entity.motionY = entity.motionZ = 0;
			entity.fallDistance = 0;
		} else {
			float yaw = info.getSpawnYaw();
			// Redirect motion based on new orientation
			// x' = cos(a) * x - sin(a) * y
			// y' = sin(a) * x + cos(a) * y
			// Translate to local space
			double cos;
			double sin;
			double tempXmotion;
			double tempZmotion;
			float rotationYaw = (float) ((Math.atan2(entity.motionX, entity.motionZ) * 180D) / Math.PI);
			cos = Math.cos(Math.toRadians(-rotationYaw));
			sin = Math.sin(Math.toRadians(-rotationYaw));
			tempXmotion = cos * entity.motionX - sin * entity.motionZ;
			tempZmotion = sin * entity.motionX + cos * entity.motionZ;
			entity.motionX = tempXmotion;
			entity.motionZ = tempZmotion;
			// Translate to global space using new yaw
			cos = Math.cos(Math.toRadians(yaw));
			sin = Math.sin(Math.toRadians(yaw));
			tempXmotion = cos * entity.motionX - sin * entity.motionZ;
			tempZmotion = sin * entity.motionX + cos * entity.motionZ;
			entity.motionX = tempXmotion;
			entity.motionZ = tempZmotion;
		}
		entity.motionY += 0.2;
	}

	//XXX: (Helper) Should this be made into a general helper? 
	private static void ejectInventory(World worldObj, IInventory inventory, double par2, double par3, double par4) {
		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = inventory.getStackInSlot(i);
			inventory.setInventorySlotContents(i, null);

			if (itemstack == null) {
				continue;
			}

			float f = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			float f1 = worldObj.rand.nextFloat() * 0.8F + 0.1F;
			float f2 = worldObj.rand.nextFloat() * 0.8F + 0.1F;

			while (itemstack.getCount() > 0) {
				int j = worldObj.rand.nextInt(21) + 10;

				if (j > itemstack.getCount()) {
					j = itemstack.getCount();
				}

				itemstack.stackSize -= j;
				EntityItem entityitem = new EntityItem(worldObj, par2 + f, par3 + f1, par4 + f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

				if (itemstack.hasTagCompound()) {
					entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
				}

				float f3 = 0.05F;
				entityitem.motionX = (float) worldObj.rand.nextGaussian() * f3;
				entityitem.motionY = (float) worldObj.rand.nextGaussian() * f3 + 0.2F;
				entityitem.motionZ = (float) worldObj.rand.nextGaussian() * f3;
				worldObj.spawnEntityInWorld(entityitem);
			}
		}
	}

	/**
	 * Drop the equipment for this entity.
	 */
	//XXX: (Helper) Should this be made into a general helper? 
	private static void dropEquipment(EntityLiving entity, Random rand) {
		//entity.dropEquipment(false, 0);
		float[] equipmentDropChances = ObfuscationReflectionHelper.getPrivateValue(EntityLiving.class, entity, "equipmentDropChances", "field" + "_82174_bp");
		for (int j = 0; j < entity.getLastActiveItems().length; ++j) {
			ItemStack itemstack = entity.getEquipmentInSlot(j);
			entity.setCurrentItemOrArmor(j, null);
			float chance = equipmentDropChances[j];
			boolean flag1 = chance > 1.0F;

			if (itemstack != null && flag1 && rand.nextFloat() < chance) {
				if (!flag1 && itemstack.isItemStackDamageable()) {
					int k = Math.max(itemstack.getMaxDamage() - 25, 1);
					int l = itemstack.getMaxDamage() - rand.nextInt(rand.nextInt(k) + 1);

					if (l > k) {
						l = k;
					}
					if (l < 1) {
						l = 1;
					}

					itemstack.setItemDamage(l);
				}

				entity.entityDropItem(itemstack, 0.0F);
			}
		}
	}
}
