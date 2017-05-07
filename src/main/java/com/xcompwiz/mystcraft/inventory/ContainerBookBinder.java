package com.xcompwiz.mystcraft.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;

import com.xcompwiz.mystcraft.data.ModItems;
import com.xcompwiz.mystcraft.network.IGuiMessageHandler;
import com.xcompwiz.mystcraft.network.packet.MPacketGuiMessage;
import com.xcompwiz.mystcraft.tileentity.TileEntityBookBinder;

public class ContainerBookBinder extends ContainerBase implements IGuiMessageHandler {
	public static class Messages {

		public static final String	InsertHeldAt	= "InsertHeldAt";
		public static final String	TakeFromSlider	= "TakeFromSlider";
		public static final String	SetTitle		= "SetTitle";
		public static final String	CSetPage		= "CSetPage";
		public static final String	CSetPageCount	= "CSetPageCount";
		public static final String	CSetHeldItem	= "CSetHeldItem";
		public static final String	CClearHeldItem	= "CClearHeldItem";
	}

	private static int				shift			= 0;

	// private InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
	private IInventory				craftResult		= new InventoryCraftResult();

	private TileEntityBookBinder	tileentity;
	private InventoryPlayer			inventoryplayer;

	// Server/Client caching for communication
	private String					cached_title	= "";
	private List<ItemStack>			page_list		= new ArrayList<ItemStack>();
	private ItemStack				cached_helditem;

	public ContainerBookBinder(InventoryPlayer inventoryplayer, TileEntityBookBinder tileentity) {
		this.tileentity = tileentity;
		this.inventoryplayer = inventoryplayer;
		// addSlotToContainer(new Slot(this.tileentity, 0, 152+shift, 8));
		// addSlotToContainer(new Slot(this.tileentity.getFolderInventory(), 0, 152+shift, 27));
		addSlotToContainer(new SlotFiltered(this.tileentity, 1, 8 + shift, 27));// , Items.leather.getIconFromDamage(0), Items.leather.getTextureFile()));

		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 9; k++) {
				addSlotToContainer(new Slot(inventoryplayer, k + i * 9 + 9, 8 + k * 18 + shift, 99 + i * 18));
			}
		}

		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(inventoryplayer, j, 8 + j * 18 + shift, 157));
		}

		this.addSlotToContainer(new SlotCraftCustom(inventoryplayer.player, tileentity, this.craftResult, 0, 152 + shift, 27));

		ITargetInventory pages = null;
		SlotCollection internal = null;
		SlotCollection maininv = null;
		SlotCollection hotbar = null;
		internal = new SlotCollection(this, 0, 1);
		maininv = new SlotCollection(this, 1, 28);
		hotbar = new SlotCollection(this, 28, 28 + 9);
		pages = new BinderPageReceiver(tileentity);

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
		List<Packet> packets = new ArrayList<Packet>();

		ItemStack helditem = inventoryplayer.getItemStack();
		if (!ItemStack.areItemStacksEqual(helditem, cached_helditem)) {
			cached_helditem = helditem;
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			if (helditem == null) {
				nbttagcompound.setBoolean(Messages.CClearHeldItem, true);
			} else {
				nbttagcompound.setTag(Messages.CSetHeldItem, helditem.writeToNBT(new NBTTagCompound()));
			}
			packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
		}

		String temp = tileentity.getPendingTitle();
		if (this.cached_title != temp) {
			cached_title = temp;

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setString(Messages.SetTitle, cached_title);
			packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
		}
		List<ItemStack> templist = tileentity.getPageList();
		for (int i = 0; i < templist.size(); ++i) {
			ItemStack remote = null, local = templist.get(i);
			if (page_list.size() > i) {
				remote = page_list.get(i);
			}
			if (!ItemStack.areItemStacksEqual(local, remote)) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setInteger(Messages.CSetPage, i);
				nbttagcompound.setTag("Item", local.writeToNBT(new NBTTagCompound()));
				packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
				while (page_list.size() < i) {
					page_list.add(null);
				}
				if (page_list.size() == i) {
					page_list.add(local);
				} else {
					page_list.set(i, local);
				}
			}
		}
		if (templist.size() < page_list.size()) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();
			nbttagcompound.setInteger(Messages.CSetPageCount, templist.size());
			packets.add(MPacketGuiMessage.createPacket(this.windowId, nbttagcompound));
			page_list = page_list.subList(0, templist.size());
		}
		if (packets.size() > 0) {
			for (int var4 = 0; var4 < this.crafters.size(); ++var4) {
				ICrafting crafter = ((ICrafting) this.crafters.get(var4));
				if (crafter instanceof EntityPlayerMP) {
					EntityPlayerMP player = (EntityPlayerMP) crafter;
					for (Packet pkt : packets) {
						player.playerNetServerHandler.sendPacket(pkt);
					}
				}
			}
		}
		updateCraftResult();
	}

	public void updateCraftResult() {
		this.craftResult.setInventorySlotContents(0, this.tileentity.getCraftedItem());
	}

	@Override
	public void updateProgressBar(int i, int j) {}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return tileentity.isUseableByPlayer(entityplayer);
	}

	@Override
	public void processMessage(EntityPlayer player, NBTTagCompound data) {
		if (data.hasKey(Messages.CClearHeldItem)) {
			if (tileentity.getWorldObj().isRemote) {
				player.inventory.setItemStack(null);
			}
		}
		if (data.hasKey(Messages.CSetHeldItem)) {
			if (tileentity.getWorldObj().isRemote) {
				player.inventory.setItemStack(new ItemStack(data.getCompoundTag(Messages.CSetHeldItem)));
			}
		}
		if (data.hasKey(Messages.CSetPageCount)) {
			page_list = page_list.subList(0, data.getInteger(Messages.CSetPageCount));
			tileentity.setPages(page_list);
		}
		if (data.hasKey(Messages.CSetPage)) {
			ItemStack item = new ItemStack(data.getCompoundTag("Item"));
			int i = data.getInteger(Messages.CSetPage);
			while (page_list.size() < i) {
				page_list.add(null);
			}
			if (page_list.size() == i) {
				page_list.add(item);
			} else {
				page_list.set(i, item);
			}
			tileentity.setPages(page_list);
		}
		if (data.hasKey(Messages.SetTitle)) {
			cached_title = data.getString(Messages.SetTitle);
			this.tileentity.setBookTitle(cached_title);
		}
		if (data.hasKey(Messages.TakeFromSlider)) {
			if (player.inventory.getItemStack() != null) return;
			int index = data.getInteger(Messages.TakeFromSlider);
			player.inventory.setItemStack(tileentity.removePage(index));
		}
		if (data.hasKey(Messages.InsertHeldAt)) {
			if (player.inventory.getItemStack() == null) return;
			int index = data.getInteger(Messages.InsertHeldAt);
			if (player.inventory.getItemStack().getItem() == ModItems.folder) { //XXX: Change to using an interface
				tileentity.insertFromFolder(player.inventory.getItemStack(), index);
			} else {
				ItemStack stack = player.inventory.getItemStack();
				boolean single = data.getBoolean("Single");
				if (single) {
					ItemStack clone = stack.copy();
					clone.stackSize = 1;
					if (tileentity.insertPage(clone, index) == null) {
						stack.stackSize -= 1;
						if (stack.getCount() <= 0) stack = null;
						player.inventory.setItemStack(stack);
					}
				} else {
					player.inventory.setItemStack(tileentity.insertPage(stack, index));
				}
			}
		}
	}

	public String getPendingTitle() {
		return cached_title;
	}

	public List<ItemStack> getPageList() {
		return page_list;
	}
}
