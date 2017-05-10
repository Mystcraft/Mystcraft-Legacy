package com.xcompwiz.mystcraft.inventory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.util.ColorGradient;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityInkMixer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class ContainerInkMixer extends ContainerBase implements IGuiMessageHandler {

	public static class Messages {

		public static final String	SetSeed			= "SetSeed";
		public static final String	SetInk			= "SetInk";
		public static final String	SetProperties	= "SetProperties";
		public static final String	Consume			= "Consume";
	}

	private static int				shift				= 0;

	private InvWrapper				craftResult			= new InvWrapper(new InventoryCraftResult());

	private TileEntityInkMixer		tileentity;
	private boolean					cached_hasink		= false;
	private long					cached_seed			= 0;

	private HashMap<String, Float>	properties			= new HashMap<>();
	private List<IMessage>			packets				= new LinkedList<>();

	private ColorGradient			propertyGradient	= null;

	public ContainerInkMixer(InventoryPlayer inventoryplayer, TileEntityInkMixer tileentity) {
		this.tileentity = tileentity;
		addSlotToContainer(new SlotFiltered((IItemHandlerModifiable) tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), tileentity, 0, 8 + shift, 27));
		addSlotToContainer(new SlotFiltered((IItemHandlerModifiable) tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), tileentity, 1, 8 + shift, 48));
		addSlotToContainer(new SlotFiltered((IItemHandlerModifiable) tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), tileentity, 2, 152 + shift, 27));

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18 + shift, 99 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18 + shift, 157));
		}

		this.addSlotToContainer(new SlotCraftCustom(inventoryplayer.player, tileentity, this.craftResult, 0, 152 + shift, 48));

		SlotCollection internal = null;
		SlotCollection maininv = null;
		SlotCollection hotbar = null;
		internal = new SlotCollection(this, 0, 3);
		maininv = new SlotCollection(this, 3, 3 + 27);
		hotbar = new SlotCollection(this, 3 + 27, 3 + 27 + 9);

		internal.pushTargetFront(maininv);
		internal.pushTargetFront(hotbar);
		maininv.pushTargetFront(hotbar);
		maininv.pushTargetFront(internal);
		hotbar.pushTargetFront(maininv);
		hotbar.pushTargetFront(internal);

		collections.add(internal);
		collections.add(maininv);
		collections.add(hotbar);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory par1IInventory) {
		super.onCraftMatrixChanged(par1IInventory);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (cached_hasink != tileentity.getHasInk()) {
			cached_hasink = tileentity.getHasInk();

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setBoolean(Messages.SetInk, cached_hasink);
            packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
		}
		if (cached_seed != tileentity.getNextSeed()) {
			cached_seed = tileentity.getNextSeed();

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setLong(Messages.SetSeed, cached_seed);
            packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
		}

		Map<String, Float> ink_probs = tileentity.getInkProperties();
		boolean changed = false;
		if (ink_probs.size() != properties.size()) {
			changed = true;
		} else {
			for (Entry<String, Float> entry : ink_probs.entrySet()) {
				Float cached = this.properties.get(entry.getKey());
				if (cached == null || !cached.equals(entry.getValue())) {
					changed = true;
					break;
				}
			}
		}
		if (changed) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			NBTTagCompound probabilities = new NBTTagCompound();
			this.properties.clear();
			for (Entry<String, Float> entry : ink_probs.entrySet()) {
				properties.put(entry.getKey(), entry.getValue());
				probabilities.setFloat(entry.getKey(), entry.getValue());
			}
			nbttagcompound.setTag(Messages.SetProperties, probabilities);
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
		this.updateCraftResult();
	}

	public void updateCraftResult() {
	    this.craftResult.setStackInSlot(0, this.tileentity.getCraftedItem());
	}

	@Override
	public void updateProgressBar(int i, int j) {}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void processMessage(@Nonnull EntityPlayer player, @Nonnull NBTTagCompound data) {
		if (data.hasKey(Messages.SetInk)) {
			this.cached_hasink = data.getBoolean(Messages.SetInk);
			this.tileentity.setHasInk(cached_hasink);
		}
		if (data.hasKey(Messages.SetSeed)) {
			this.cached_seed = data.getLong(Messages.SetSeed);
			this.tileentity.setNextSeed(cached_seed);
		}
		if (data.hasKey(Messages.SetProperties)) {
			this.properties.clear();
			NBTTagCompound probabilities = data.getCompoundTag(Messages.SetProperties);
			for (String tagname : probabilities.getKeySet()) {
				this.properties.put(tagname, probabilities.getFloat(tagname));
			}
			rebuildGradient();
		}
		if (data.hasKey(Messages.Consume)) {
			if (player.inventory.getItemStack().isEmpty()) return;
			if (!tileentity.getHasInk()) return;
			ItemStack itemstack = player.inventory.getItemStack();

			int amount = itemstack.getCount();
			if (data.getBoolean("Single")) amount = 1;

			player.inventory.setItemStack(this.tileentity.addItems(itemstack, amount));
		}
	}

	private void rebuildGradient() {
		this.propertyGradient = InternalAPI.linkProperties.getPropertiesGradient(properties);
	}

    @Override
    public boolean canMergeSlot(@Nonnull ItemStack stack, Slot slot) {
        return slot.inventory != this.craftResult && super.canMergeSlot(stack, slot);
    }

	public boolean hasInk() {
		return cached_hasink;
	}

	public ColorGradient getPropertyGradient() {
		return this.propertyGradient;
	}
}
