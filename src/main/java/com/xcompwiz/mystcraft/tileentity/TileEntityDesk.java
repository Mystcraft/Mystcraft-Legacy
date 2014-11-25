package com.xcompwiz.mystcraft.tileentity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.block.BlockWritingDesk;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.fluids.FluidUtils;
import com.xcompwiz.mystcraft.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.item.IItemPageProvider;
import com.xcompwiz.mystcraft.item.IItemRenameable;
import com.xcompwiz.mystcraft.item.IItemWritable;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.network.IMessageReceiver;
import com.xcompwiz.mystcraft.network.MPacketMessage;
import com.xcompwiz.mystcraft.page.IItemPageAcceptor;
import com.xcompwiz.mystcraft.page.IItemPageCollection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityDesk extends TileEntity implements IFluidHandler, ISidedInventory, IMessageReceiver {

	private ItemStack			itemstacks[];
	private ItemStack			tabitems[];
	private FluidTankFiltered	inkwell;

	private static final int	slot_wrt	= 0;
	private static final int	slot_pap	= 1;
	private static final int	slot_ctn	= 2;
	private static final int	slot_out	= 3;

	private static final int[]	isidedslots	= { slot_pap };

	public TileEntityDesk() {
		itemstacks = new ItemStack[4];
		tabitems = new ItemStack[25];
		inkwell = new FluidTankFiltered(FluidContainerRegistry.BUCKET_VOLUME);
		inkwell.setPermittedFluids(Mystcraft.validInks);
	}

	public int getMainInventorySize() {
		return itemstacks.length;
	}

	public int getMaxSurfaceTabCount() {
		return tabitems.length;
	}

	public int getPaperCount() {
		ItemStack itemstack = this.getStackInSlot(slot_pap);
		if (itemstack != null) { return itemstack.stackSize; }
		return 0;
	}

	public ItemStack getDisplayItem() {
		return this.getStackInSlot(slot_wrt);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		ItemStack itemstack = null;
		itemstack = this.getStackInSlot(slot_wrt);
		if (itemstack != null) {
			nbttagcompound.setTag("DisItem", itemstack.writeToNBT(new NBTTagCompound()));
		}
		itemstack = this.getStackInSlot(slot_pap);
		if (itemstack != null) {
			nbttagcompound.setTag("PaperItem", itemstack.writeToNBT(new NBTTagCompound()));
		}
		return MPacketMessage.createPacket(this, nbttagcompound);
	}

	@Override
	public void processMessageData(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKey("DisItem")) {
			this.setInventorySlotContents(slot_wrt, ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("DisItem")));
		} else {
			this.setInventorySlotContents(slot_wrt, null);
		}
		if (nbttagcompound.hasKey("PaperItem")) {
			this.setInventorySlotContents(slot_pap, ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("PaperItem")));
		} else {
			this.setInventorySlotContents(slot_pap, null);
		}
		markDirty();
	}

	public ItemStack getTabItem(byte activeslot) {
		if (activeslot < 0 || activeslot >= tabitems.length) return null;
		ItemStack itemstack = tabitems[activeslot];
		if (itemstack == null) return null;
		if (itemstack.getItem() instanceof IItemPageCollection) return itemstack;
		if (itemstack.getItem() instanceof IItemWritable) return itemstack;
		return null;
	}

	@Override
	public boolean isItemValidForSlot(int slotIndex, ItemStack itemstack) {
		if (itemstack == null || itemstack.getItem() == null) return false;
		if (slotIndex == slot_wrt && itemstack.getItem() instanceof IItemWritable) return true;
		if (slotIndex == slot_wrt && itemstack.getItem() instanceof IItemRenameable) return true;
		if (slotIndex == slot_pap && itemstack.getItem() == Items.paper) return true;
		if (slotIndex == slot_ctn && FluidContainerRegistry.isContainer(itemstack)) return true;
		// if (slotIndex == slot_ctn && itemstack.getItem() instanceof
		// IFluidContainerItem) return true; //TODO: (Fluids) Handle IFluidContainerItem
		slotIndex -= itemstacks.length;
		if (slotIndex < 0) return false;
		if (slotIndex >= tabitems.length) return false;
		if (itemstack.getItem() instanceof IItemPageCollection) return true;
		if (itemstack.getItem() instanceof IItemWritable) return true;
		return false;
	}

	@Override
	public int getSizeInventory() {
		return itemstacks.length + tabitems.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		ItemStack[] inv = itemstacks;
		if (index >= inv.length) {
			index -= inv.length;
			inv = tabitems;
		}
		if (index > inv.length) return null;
		return inv[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int amount) {
		ItemStack[] inv = itemstacks;
		if (index >= inv.length) {
			index -= inv.length;
			inv = tabitems;
		}
		if (inv[index] != null) {
			if (inv[index].stackSize <= amount) {
				ItemStack itemstack = inv[index];
				inv[index] = null;
				handleItemChange(inv[index]);
				return itemstack;
			}
			ItemStack itemstack1 = inv[index].splitStack(amount);
			if (inv[index].stackSize == 0) {
				inv[index] = null;
			}
			handleItemChange(inv[index]);
			return itemstack1;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack itemstack) {
		ItemStack[] inv = itemstacks;
		if (index >= inv.length) {
			index -= inv.length;
			inv = tabitems;
		}
		inv[index] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		handleItemChange(inv[index]);
	}

	@Override
	public String getInventoryName() {
		return "Writing Desk";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) { return false; }
		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	public boolean hasTop() {
		if (this.worldObj.getBlock(this.xCoord, this.yCoord + 1, this.zCoord) != ModBlocks.writingdesk) return false;
		if (!BlockWritingDesk.isBlockTop(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord + 1, this.zCoord))) return false;
		return true;
	}

	public boolean isLeftCovered() {
		Block block = this.worldObj.getBlock(this.xCoord, this.yCoord + 1, this.zCoord);
		if (!worldObj.isAirBlock(this.xCoord, this.yCoord + 1, this.zCoord) && block != ModBlocks.writingdesk) return true;
		if (!BlockWritingDesk.isBlockTop(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord + 1, this.zCoord))) return true;
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		if (nbttagcompound.hasKey("Fluid")) {
			inkwell.fill(FluidStack.loadFluidStackFromNBT(nbttagcompound.getCompoundTag("Fluid")), true);
		}
		itemstacks = new ItemStack[4];
		tabitems = new ItemStack[25];
		NBTUtils.readInventoryArray(nbttagcompound.getTagList("Items", Constants.NBT.TAG_COMPOUND), itemstacks);
		NBTUtils.readInventoryArray(nbttagcompound.getTagList("Notebooks", Constants.NBT.TAG_COMPOUND), tabitems);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		FluidStack fluid = inkwell.getFluid();
		if (fluid != null) {
			nbttagcompound.setTag("Fluid", fluid.writeToNBT(new NBTTagCompound()));
		}
		nbttagcompound.setTag("Items", NBTUtils.writeInventoryArray(new NBTTagList(), itemstacks));
		nbttagcompound.setTag("Notebooks", NBTUtils.writeInventoryArray(new NBTTagList(), tabitems));
	}

	public void handleItemChange(ItemStack itemstack) {}

	public String getTargetString(EntityPlayer player) {
		ItemStack itemstack = itemstacks[slot_wrt];
		if (itemstack == null) return "";
		if (!(itemstack.getItem() instanceof IItemRenameable)) return "";
		String name = ((IItemRenameable) itemstack.getItem()).getDisplayName(player, itemstack);
		if (name == null) return "";
		return name;
	}

	public void setBookTitle(EntityPlayer player, String bookname) {
		ItemStack itemstack = itemstacks[slot_wrt];
		if (itemstack == null) return;
		if (!(itemstack.getItem() instanceof IItemRenameable)) return;
		((IItemRenameable) itemstack.getItem()).setDisplayName(player, itemstack, bookname);
	}

	public List<ItemStack> getBookPageList(EntityPlayer player) {
		ItemStack itemstack = itemstacks[slot_wrt];
		if (itemstack == null) return null;
		if (!(itemstack.getItem() instanceof IItemPageProvider)) return null;
		return ((IItemPageProvider) itemstack.getItem()).getPageList(player, itemstack);
	}

	public void writeSymbol(EntityPlayer player, String symbol) {
		if (worldObj.isRemote) return;
		if (!hasEnoughInk()) return;

		// If nothing in slot, check fill slot
		if (itemstacks[slot_wrt] == null && itemstacks[slot_pap] != null) {
			itemstacks[slot_wrt] = ItemPage.createItemstack(itemstacks[slot_pap]);
			if (itemstacks[slot_pap].stackSize <= 0) itemstacks[slot_pap] = null;
		}

		// If nothing in slot, we're done
		if (itemstacks[slot_wrt] == null) return;

		// Do writing
		ItemStack target = itemstacks[slot_wrt];
		if (target == null) return;
		if (target.getItem() instanceof IItemWritable && ((IItemWritable) target.getItem()).writeSymbol(player, target, symbol)) {
			useink();
			player.addStat(ModAchievements.write, 1);
			return;
		}
		ItemStack paperstack = itemstacks[slot_pap];
		if (paperstack != null && (target.getItem() instanceof IItemPageAcceptor)) {
			ItemStack page = paperstack.copy();
			page.stackSize = 1;
			page = ItemPage.createItemstack(page);
			if (page != null) {
				InternalAPI.page.setPageSymbol(page, symbol);
				if (((IItemPageAcceptor) target.getItem()).addPage(player, target, page) == null) {
					useink();
					--paperstack.stackSize;
				}
			}
			if (itemstacks[slot_pap].stackSize <= 0) itemstacks[slot_pap] = null;
			return;
		}
	}

	public ItemStack removePageFromSurface(EntityPlayer player, ItemStack itemstack, int index) {
		if (itemstack == null) return null;
		ItemStack result = null;
		if (itemstack.getItem() instanceof IItemOrderablePageProvider) result = ((IItemOrderablePageProvider) itemstack.getItem()).removePage(player, itemstack, index);
		if (result == null) return result;
		this.markDirty();
		return result;
	}

	public ItemStack removePageFromSurface(EntityPlayer player, ItemStack itemstack, ItemStack page) {
		if (itemstack == null) return null;
		ItemStack result = null;
		if (itemstack.getItem() instanceof IItemPageCollection) result = ((IItemPageCollection) itemstack.getItem()).remove(player, itemstack, page);
		if (result == null) return result;
		this.markDirty();
		return result;
	}

	public ItemStack addPageToCollection(EntityPlayer player, ItemStack itemstack, ItemStack page) {
		if (itemstack == null) return page;
		ItemStack result = page;
		if (itemstack.getItem() instanceof IItemPageCollection) result = ((IItemPageCollection) itemstack.getItem()).addPage(player, itemstack, page);
		if (result == page) return result;
		this.markDirty();
		return result;
	}

	public ItemStack placePageOnSurface(EntityPlayer player, ItemStack itemstack, ItemStack page, int index) {
		if (itemstack == null) return page;
		ItemStack result = page;
		if (itemstack.getItem() instanceof IItemPageCollection) result = ((IItemPageCollection) itemstack.getItem()).addPage(player, itemstack, page);
		if (itemstack.getItem() instanceof IItemOrderablePageProvider) result = ((IItemOrderablePageProvider) itemstack.getItem()).setPage(player, itemstack, page, index);
		if (result == page) return result;
		this.markDirty();
		return result;
	}

	public ItemStack getTarget() {
		return itemstacks[slot_wrt];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		ItemStack[] inv = itemstacks;
		if (index >= itemstacks.length) {
			inv = tabitems;
			index -= itemstacks.length;
		}

		if (inv[index] != null) {
			ItemStack itemstack = inv[index];
			inv[index] = null;
			return itemstack;
		}
		return null;
	}

	public FluidStack getInk() {
		return inkwell.getFluid();
	}

	public void setInk(FluidStack fluid) {
		inkwell.setFluid(fluid);
	}

	private boolean hasEnoughInk() {
		FluidStack fluid = inkwell.getFluid();
		if (fluid == null) return false;
		if (fluid.amount < Mystcraft.inkcost) return false;
		return true;
	}

	private void useink() {
		inkwell.drain(Mystcraft.inkcost, true);
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (resource == null) return 0;
		return inkwell.fill(resource, doFill);
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return inkwell.drain(maxDrain, doDrain);
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if (resource == null) return null;
		if (resource.isFluidEqual(inkwell.getFluid())) return inkwell.drain(resource.amount, doDrain);
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		if (fluid == null) return false;
		return inkwell.isFluidPermitted(fluid);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		if (fluid == null) return false;
		return inkwell.isFluidPermitted(fluid);
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		return new FluidTankInfo[] { inkwell.getInfo() };
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public void updateEntity() {
		if (worldObj.isRemote) return;
		if (BlockWritingDesk.isBlockFoot(worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord))) this.tileEntityInvalid = true;
		// If we can pull from the in container and the out slot is either empty or contains the empty form of the container
		// XXX: (Fluids) Clean this up and move into external helpers
		if (itemstacks[slot_ctn] != null) {
			ItemStack container = itemstacks[slot_ctn];
			ItemStack emptycontainer = container.getItem().getContainerItem(container);
			if (emptycontainer == null || mergeItemStacksLeft(itemstacks[slot_out], emptycontainer) != itemstacks[slot_out]) {
				ItemStack result = FluidUtils.fillTankWithContainer(inkwell, container);
				if (result != null) {
					itemstacks[slot_out] = mergeItemStacksLeft(itemstacks[slot_out], result);
					if (container.stackSize == 0) itemstacks[slot_ctn] = null;
				}
			}
		}
		if (itemstacks[slot_ctn] != null) {
			ItemStack container = itemstacks[slot_ctn];
			FluidStack tankFluid = inkwell.getFluid();
			ItemStack result = FluidContainerRegistry.fillFluidContainer(tankFluid, container);
			if (mergeItemStacksLeft(itemstacks[slot_out], result) != itemstacks[slot_out]) {
				itemstacks[slot_out] = mergeItemStacksLeft(itemstacks[slot_out], FluidUtils.drainTankIntoContainer(inkwell, container));
				if (container.stackSize == 0) itemstacks[slot_ctn] = null;
			}
		}
	}

	//XXX: (Fluids) Improve how I handle fluid containers and items
	private static ItemStack mergeItemStacksLeft(ItemStack left, ItemStack right) {
		if (right == null) return left;
		if (left == null) return right;
		if (left.getItem() != right.getItem()) {
			return left;
		} else if (left.hasTagCompound() != right.hasTagCompound()) {
			return left;
		} else if (left.hasTagCompound() && !left.getTagCompound().equals(right.getTagCompound())) {
			return left;
		} else if (left.getItem().getHasSubtypes() && left.getItemDamage() != right.getItemDamage()) {
			return left;
		} else if (left.stackSize + right.stackSize > left.getMaxStackSize()) { return left; }
		left = left.copy();
		left.stackSize += right.stackSize;
		right.stackSize = 0;
		return left;
	}

	public void link(Entity player) {
		ItemStack book = getTarget();
		if (book == null) return;
		if (!(book.getItem() instanceof ItemLinking)) return;
		LinkController.travelEntity(worldObj, player, ((ItemLinking) book.getItem()).getLinkInfo(book));
	}

	/**
	 * Get the size of the side inventory.
	 */
	@Override
	public int[] getAccessibleSlotsFromSide(int par1) {
		return isidedslots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack itemstack, int par3) {
		return this.isItemValidForSlot(slot, itemstack);
	}

	@Override
	public boolean canExtractItem(int par1, ItemStack par2ItemStack, int par3) {
		return false;
	}

	/**
	 * Return an {@link AxisAlignedBB} that controls the visible scope of a {@link TileEntitySpecialRenderer} associated with this {@link TileEntity} Defaults
	 * to the collision bounding box {@link Block#getCollisionBoundingBoxFromPool(World, int, int, int)} associated with the block at this location.
	 * @return an appropriately size {@link AxisAlignedBB} for the {@link TileEntity}
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord - 1, yCoord, zCoord - 1, xCoord + 2, yCoord + 2, zCoord + 2);
	}
}
