package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.List;

import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.MystcraftPacketHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;

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
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ContainerBookBinder extends ContainerBase implements IGuiMessageHandler {
	public static class Messages {

		public static final String	InsertHeldAt	= "InsertHeldAt";
		public static final String	TakeFromSlider	= "TakeFromSlider";
		public static final String	SetTitle		= "SetTitle";
		//public static final String	CSetPage		= "CSetPage";
		//public static final String	CSetPageCount	= "CSetPageCount";
		//public static final String	CSetHeldItem	= "CSetHeldItem";
		//public static final String	CClearHeldItem	= "CClearHeldItem";
	}

	private static int				shift			= 0;

	// private InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
	private IItemHandlerModifiable craftResult		= new InvWrapper(new InventoryCraftResult());

    public TileEntityBookBinder	tileentity;
	public InventoryPlayer			inventoryplayer;

	// Server/Client caching for communication
	private String					cached_title	= "";
	//private NonNullList<ItemStack>	page_list		= NonNullList.create();
	//@Nonnull
	//private ItemStack				cached_helditem = ItemStack.EMPTY;

	public ContainerBookBinder(InventoryPlayer inventoryplayer, TileEntityBookBinder tileentity) {
		this.tileentity = tileentity;
		this.inventoryplayer = inventoryplayer;
		addSlotToContainer(new SlotFiltered((IItemHandlerModifiable) tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), this.tileentity, 0, 8 + shift, 27));

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18 + shift, 99 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18 + shift, 157));
		}

		this.addSlotToContainer(new SlotCraftCustom(inventoryplayer.player, tileentity, this.craftResult, 0, 152 + shift, 27));

        SlotCollection internal = new SlotCollection(this, 0, 1);
        SlotCollection maininv = new SlotCollection(this, 1, 28);
        SlotCollection hotbar = new SlotCollection(this, 28, 28 + 9);
        ITargetInventory pages = new BinderPageReceiver(tileentity);

		internal.pushTargetFront(maininv);
		internal.pushTargetFront(hotbar);
		maininv.pushTargetFront(hotbar);
		maininv.pushTargetFront(internal);
		maininv.pushTargetFront(pages);
		hotbar.pushTargetFront(maininv);
		hotbar.pushTargetFront(internal);
		hotbar.pushTargetFront(pages);

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
		List<IMessage> packets = new ArrayList<>();

		//ItemStack helditem = inventoryplayer.getItemStack();
		//if (!ItemStack.areItemStacksEqual(helditem, cached_helditem)) {
		//	cached_helditem = helditem;
		//	NBTTagCompound nbttagcompound = new NBTTagCompound();
		//	if (helditem.isEmpty()) {
		//		nbttagcompound.setBoolean(Messages.CClearHeldItem, true);
		//	} else {
		//		nbttagcompound.setTag(Messages.CSetHeldItem, helditem.writeToNBT(new NBTTagCompound()));
		//	}
		//	packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
		//}

		String temp = tileentity.getPendingTitle();
		if (!this.cached_title.equals(temp)) {
			cached_title = temp;
		}
		//List<ItemStack> templist = tileentity.getPageList();
		//for (int i = 0; i < templist.size(); ++i) {
		//	ItemStack remote = ItemStack.EMPTY, local = templist.get(i);
		//	if (page_list.size() > i) {
		//		remote = page_list.get(i);
		//	}
		//	if (!ItemStack.areItemStacksEqual(local, remote)) {
		//		NBTTagCompound nbttagcompound = new NBTTagCompound();
		//		nbttagcompound.setInteger(Messages.CSetPage, i);
		//		nbttagcompound.setTag("Item", local.writeToNBT(new NBTTagCompound()));
		//		packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));
		//		while (page_list.size() < i) {
		//			page_list.add(ItemStack.EMPTY);
		//		}
		//		if (page_list.size() == i) {
		//			page_list.add(local);
		//		} else {
		//			page_list.set(i, local);
		//		}
		//	}
		//}
		//if (templist.size() < page_list.size()) {
		//	NBTTagCompound nbttagcompound = new NBTTagCompound();
		//	nbttagcompound.setInteger(Messages.CSetPageCount, templist.size());
		//	packets.add(new MPacketGuiMessage(this.windowId, nbttagcompound));

        //    NonNullList<ItemStack> list = NonNullList.create();
        //    List<ItemStack> subList = page_list.subList(0, templist.size());
        //    list.addAll(subList);
        //    page_list = list;
		//}
		if (packets.size() > 0) {
			for (IContainerListener listener : this.listeners) {
				if(listener instanceof EntityPlayerMP) {
					for (IMessage message : packets) {
						MystcraftPacketHandler.CHANNEL.sendTo(message, (EntityPlayerMP) listener);
					}
				}
			}
		}
		updateCraftResult();
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
		//if (data.hasKey(Messages.CClearHeldItem)) {
		//	if (tileentity.getWorld().isRemote) {
		//		player.inventory.setItemStack(ItemStack.EMPTY);
		//	}
		//}
		//if (data.hasKey(Messages.CSetHeldItem)) {
		//	if (tileentity.getWorld().isRemote) {
		//		player.inventory.setItemStack(new ItemStack(data.getCompoundTag(Messages.CSetHeldItem)));
		//	}
		//}
		//if (data.hasKey(Messages.CSetPageCount)) {
		//    NonNullList<ItemStack> list = NonNullList.create();
		//	List<ItemStack> subList = page_list.subList(0, data.getInteger(Messages.CSetPageCount));
		//	list.addAll(subList);
		//	tileentity.setPages(list);
		//}
		//if (data.hasKey(Messages.CSetPage)) {
		//	ItemStack item = new ItemStack(data.getCompoundTag("Item"));
		//	int i = data.getInteger(Messages.CSetPage);
		//	while (page_list.size() < i) {
		//		page_list.add(ItemStack.EMPTY);
		//	}
		//	if (page_list.size() == i) {
		//		page_list.add(item);
		//	} else {
		//		page_list.set(i, item);
		//	}
		//	tileentity.setPages(page_list);
		//}
		if (data.hasKey(Messages.SetTitle)) {
			cached_title = data.getString(Messages.SetTitle);
			this.tileentity.setBookTitle(cached_title);
			this.tileentity.markForUpdate();
		}
		if (data.hasKey(Messages.TakeFromSlider)) {
			if (!player.inventory.getItemStack().isEmpty()) return;
			int index = data.getInteger(Messages.TakeFromSlider);
			player.inventory.setItemStack(tileentity.removePage(index));
			player.inventory.markDirty();
		}
		if (data.hasKey(Messages.InsertHeldAt)) {
			if (player.inventory.getItemStack().isEmpty()) return;
			int index = data.getInteger(Messages.InsertHeldAt);
			if (player.inventory.getItemStack().getItem() == ModItems.folder) { //XXX: Change to using an interface
				tileentity.insertFromFolder(player.inventory.getItemStack(), index);
			} else {
				ItemStack stack = player.inventory.getItemStack();
				boolean single = data.getBoolean("Single");
				if (single) {
					ItemStack clone = stack.copy();
					clone.setCount(1);
					if (tileentity.insertPage(clone, index).isEmpty()) {
						stack.shrink(1);
						if (stack.getCount() <= 0) {
							stack = ItemStack.EMPTY;
						}
						player.inventory.setItemStack(stack);
					}
				} else {
					player.inventory.setItemStack(tileentity.insertPage(stack, index));
				}
			}
		}
	}

	@Nullable
	public String getPendingTitle() {
		return tileentity.getPendingTitle();
	}

	public List<ItemStack> getPageList() {
		return tileentity.getPageList();
	}
}
