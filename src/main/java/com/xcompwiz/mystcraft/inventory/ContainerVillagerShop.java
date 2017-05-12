package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.List;

import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.villager.VillagerTradeSystem;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerVillagerShop extends ContainerBase implements IGuiMessageHandler {

	public static class Messages {

		public static final String	UpdateVillagerCollection	= "UVC";
		public static final String	PurchaseBooster				= "PB";
		public static final String	PurchaseItem				= "PI";

	}

	private IItemHandlerModifiable handlerPlayer;
	private InventoryPlayer inventoryPlayer;
	private EntityVillager		villager;
	private InventoryVillager	villagerinv;

	private long				villagerinv_timestamp;
	private Integer				playerEmeralds;
	private long				lastEmeraldsUpdate;

	public ContainerVillagerShop(EntityPlayer player, EntityVillager villager) {
	    this.handlerPlayer = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN); //Get main
        this.inventoryPlayer = player.inventory;
		this.villager = villager;
		this.villagerinv = VillagerTradeSystem.getVillagerInventory(villager);
		updateSlots();
	}

	public void updateSlots() {
		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();
		int i;
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new SlotItemHandler(handlerPlayer, j + i * 9 + 9, 8 + j * 18, 99 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new SlotItemHandler(handlerPlayer, i, 8 + i * 18, 157));
		}

		collections.clear();
        SlotCollection maininv = new SlotCollection(this, 0, 0 + 27);
        SlotCollection hotbar = new SlotCollection(this, 0 + 27, 0 + 27 + 9);

		maininv.pushTargetFront(hotbar);
		hotbar.pushTargetFront(maininv);

		collections.add(maininv);
		collections.add(hotbar);
	}

	@Override
	public void detectAndSendChanges() {
		List<IMessage> packets = new ArrayList<>();
		for (int slotId = 0; slotId < this.inventorySlots.size(); ++slotId) {
			ItemStack actual = this.inventorySlots.get(slotId).getStack();
			ItemStack stored = this.inventoryItemStacks.get(slotId);

			if (!ItemStack.areItemStacksEqual(stored, actual)) {
				stored = actual.isEmpty() ? ItemStack.EMPTY : actual.copy();
				this.inventoryItemStacks.set(slotId, stored);

				for (IContainerListener listener : this.listeners) {
				    listener.sendSlotContents(this, slotId, stored);
                }
			}
		}
		if (this.villagerinv_timestamp != villagerinv.getLastChanged()) {
			villagerinv_timestamp = villagerinv.getLastChanged();
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTTagCompound inventorynbt = new NBTTagCompound();
			villagerinv.writeToNBT(inventorynbt);
			nbttagcompound.setTag(Messages.UpdateVillagerCollection, inventorynbt);
			packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
		}
        if (packets.size() > 0) {
            for (IContainerListener listener : this.listeners) {
                if(listener instanceof EntityPlayerMP) {
                    for (IMessage message : packets) {
                        MystcraftPacketHandler.CHANNEL.sendTo(message, (EntityPlayerMP) listener);
                    }
                }
            }
        }
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		if (villager.isDead) return false;
		return entityplayer.getDistanceSq(villager.posX, villager.posY, villager.posZ) <= 64D;
	}

	@Override
	public void processMessage(@Nonnull EntityPlayer player, @Nonnull NBTTagCompound data) {
		if (data.hasKey(Messages.UpdateVillagerCollection)) {
			if (this.villager.world.isRemote) { //Only allow the updates to the client this way
				this.villagerinv.readFromNBT(data.getCompoundTag(Messages.UpdateVillagerCollection));
			}
			return;
		}
		if (data.hasKey(Messages.PurchaseBooster)) {
			if (villagerinv.purchaseBooster(handlerPlayer, inventoryPlayer)) getPlayerEmeralds(true);
			return;
		}
		if (data.hasKey(Messages.PurchaseItem)) {
			int index = data.getInteger(Messages.PurchaseItem);
			if (villagerinv.purchaseShopItem(handlerPlayer, inventoryPlayer, index)) getPlayerEmeralds(true);
        }
	}

	/**
	 * Called when the container is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		VillagerTradeSystem.release(villagerinv);
	}

	@Nonnull
	public ItemStack getShopItem(int index) {
		return villagerinv.getShopItem(index);
	}

	public int getShopItemPrice(int index) {
		return villagerinv.getShopItemPrice(index);
	}

	public int getBoosterCount() {
		return villagerinv.getBoosterCount();
	}

	public int getBoosterCost() {
		return villagerinv.getBoosterCost();
	}

	public int getPlayerEmeralds() {
		return getPlayerEmeralds(false);
	}

	private int getPlayerEmeralds(boolean forceupdate) {
		if (playerEmeralds == null || forceupdate || lastEmeraldsUpdate + 100 < villager.world.getTotalWorldTime()) {
			lastEmeraldsUpdate = villager.world.getTotalWorldTime();
			playerEmeralds = villagerinv.getPlayerEmeralds(handlerPlayer);
		}
		return playerEmeralds;
	}
}
