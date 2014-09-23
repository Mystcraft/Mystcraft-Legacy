package com.xcompwiz.mystcraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;

import com.xcompwiz.mystcraft.network.IGuiMessageHandler;

public class ContainerNotebook extends ContainerBase implements IGuiMessageHandler {
	private InventoryNotebook	itemInventory;

	public ContainerNotebook(InventoryPlayer inventoryplayer, int slot /* , IPageHandler external */) {
		this.itemInventory = new InventoryNotebook(inventoryplayer, slot);

		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new SlotNotebook(itemInventory, i * 9 + j, 8 + j * 18, 22 + i * 18));
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18, 99 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			if (slot == j) {
				addSlotToContainer(new SlotBanned(inventoryplayer, j, 8 + j * 18, 157));
			}
			addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18, 157));
		}

		SlotCollection internal = null;
		SlotCollection maininv = null;
		SlotCollection hotbar = null;
		internal = new SlotCollection(this, 0, 36);
		maininv = new SlotCollection(this, 36, 36 + 27);
		hotbar = new SlotCollection(this, 36 + 27, 36 + 27 + 9);

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

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return itemInventory.isUseableByPlayer(player);
	}

	public int rowCount() {
		return (itemInventory.getSizeInventory() / 9) + 1;
	}

	public void scrollTo(int row) {
		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 9; ++j) {
				((SlotNotebook) this.inventorySlots.get(i * 9 + j)).slotIndex = (i + row) * 9 + j;
				// this.inventoryItemStacks.set(i * 9 + j, null);
			}
		}
	}

	@Override
	public void processMessage(EntityPlayer player, NBTTagCompound data) {
		if (data.hasKey("ScrollTo")) {
			this.scrollTo(data.getInteger("ScrollTo"));
			this.detectAndSendChanges();
		}
	}

	public String getNotebookName() {
		return itemInventory.getNotebookName();
	}
}
