package com.xcompwiz.mystcraft.tileentity;

import java.util.List;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.item.IItemOrderablePageProvider;
import com.xcompwiz.mystcraft.api.item.IItemPageAcceptor;
import com.xcompwiz.mystcraft.api.item.IItemPageCollection;
import com.xcompwiz.mystcraft.api.item.IItemPageProvider;
import com.xcompwiz.mystcraft.api.item.IItemRenameable;
import com.xcompwiz.mystcraft.api.item.IItemWritable;
import com.xcompwiz.mystcraft.block.BlockWritingDesk;
import com.xcompwiz.mystcraft.data.ModAchievements;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.fluids.FluidUtils;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.nbt.NBTUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityDesk extends TileEntityBase implements InventoryFilter, ITickable {

	private IOInventory inventoryStacks;
	private IOInventory inventoryTabItems;

	private FluidTankFiltered	inkwell;

	private static final int	slot_wrt	= 0;
	private static final int	slot_pap	= 1;
	private static final int	slot_ctn	= 2;
	private static final int	slot_out	= 3;

	public TileEntityDesk() {
		inkwell = new FluidTankFiltered(Fluid.BUCKET_VOLUME);
		inkwell.setPermittedFluids(Mystcraft.validInks);

		this.inventoryStacks = buildWorkInventory();
		this.inventoryTabItems = buildTabInventory();
	}

	protected IOInventory buildWorkInventory() {
		return new IOInventory(this, new int[] { slot_pap }, new int[] {}, EnumFacing.VALUES)
				.setMiscSlots(slot_wrt, slot_ctn, slot_out)
				.setListener(() -> onChange(true))
				.applyFilter(this, slot_pap); //Doesn't matter for any other slots anyway
	}

	protected IOInventory buildTabInventory() {
        IOInventory inv = new IOInventory(this, new int[0], new int[0], EnumFacing.VALUES)
                .setListener(() -> onChange(false));
        for (int i = 0; i < 25; i++) {
            inv.setMiscSlots(i);
        }
        return inv;
    }

	public int getMainInventorySize() {
		return inventoryStacks.getSlots();
	}

	public int getMaxSurfaceTabCount() {
		return inventoryTabItems.getSlots();
	}

	public int getPaperCount() {
		ItemStack itemstack = this.inventoryStacks.getStackInSlot(slot_pap);
		if (!itemstack.isEmpty()) {
		    return itemstack.getCount();
		}
		return 0;
	}

	@Nonnull
	public ItemStack getDisplayItem() {
	    return this.inventoryStacks.getStackInSlot(slot_wrt);
	}

	@Nonnull
	public ItemStack getTabItem(byte activeslot) {
		if (activeslot < 0 || activeslot >= inventoryTabItems.getSlots()) return ItemStack.EMPTY;
		ItemStack itemstack = this.inventoryTabItems.getStackInSlot(activeslot);
		if (itemstack.isEmpty()) return ItemStack.EMPTY;
		if (itemstack.getItem() instanceof IItemPageCollection) return itemstack;
		if (itemstack.getItem() instanceof IItemWritable) return itemstack;
		return ItemStack.EMPTY;
	}

    public void onChange(boolean isWorkInv) {
	    if(isWorkInv) {
            for (int i = 0; i < inventoryStacks.getSlots(); i++) {
                handleItemChange(inventoryStacks.getStackInSlot(i));
            }
        } else {
            for (int i = 0; i < inventoryTabItems.getSlots(); i++) {
                handleItemChange(inventoryTabItems.getStackInSlot(i));
            }
        }
    }

    @Override
    public boolean canAcceptItem(int slot, @Nonnull ItemStack stack) {
	    if(stack.isEmpty()) return false;
        if (slot == slot_wrt && stack.getItem() instanceof IItemWritable) return true;
        if (slot == slot_wrt && stack.getItem() instanceof IItemRenameable) return true;
        if (slot == slot_pap && stack.getItem().equals(Items.PAPER)) return true;
        if (slot == slot_ctn) {
            FluidStack fluidStack = FluidUtil.getFluidContained(stack);
            if(fluidStack != null) {
                return inkwell.isFluidPermitted(fluidStack.getFluid());
            }
        }

        slot -= inventoryStacks.getSlots();
        if (slot < 0) return false;
        if (slot >= inventoryTabItems.getSlots()) return false;
        if (stack.getItem() instanceof IItemPageCollection) return true;
        if (stack.getItem() instanceof IItemWritable) return true;
        return false;
    }

	public boolean hasTop() {
	    if(!world.getBlockState(pos.up()).getBlock().equals(ModBlocks.writingdesk)) return false;
	    if(!BlockWritingDesk.isBlockTop(world.getBlockState(pos.up()))) return false;
		return true;
	}

	public boolean isLeftCovered() {
        IBlockState state = world.getBlockState(pos.up());
        if(!world.isAirBlock(pos.up()) && !state.getBlock().equals(ModBlocks.writingdesk)) return true;
        if(!BlockWritingDesk.isBlockTop(state)) return true;
		return false;
	}

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);
        this.inkwell.readFromNBT(compound.getCompoundTag("fluid"));
        this.inventoryStacks = IOInventory.deserialize(this, compound.getCompoundTag("items"));
        this.inventoryTabItems = IOInventory.deserialize(this, compound.getCompoundTag("notebooks"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        NBTTagCompound tag = new NBTTagCompound();
        this.inkwell.writeToNBT(tag);
        compound.setTag("fluid", tag);
        compound.setTag("items", this.inventoryStacks.writeNBT());
        compound.setTag("notebooks", this.inventoryTabItems.writeNBT());
    }

	public void handleItemChange(@Nonnull ItemStack itemstack) {}

	@Nonnull
	public String getTargetString(EntityPlayer player) {
		ItemStack itemstack = this.inventoryStacks.getStackInSlot(slot_wrt);
		if (itemstack.isEmpty()) return "";
		if (!(itemstack.getItem() instanceof IItemRenameable)) return "";
		String name = ((IItemRenameable) itemstack.getItem()).getDisplayName(player, itemstack);
		if (name == null) return "";
		return name;
	}

	public void setBookTitle(EntityPlayer player, String bookname) {
		ItemStack itemstack = this.inventoryStacks.getStackInSlot(slot_wrt);
		if (itemstack.isEmpty()) return;
		if (!(itemstack.getItem() instanceof IItemRenameable)) return;
		((IItemRenameable) itemstack.getItem()).setDisplayName(player, itemstack, bookname);
	}

	@Nullable
	public List<ItemStack> getBookPageList(EntityPlayer player) {
		ItemStack itemstack = this.inventoryStacks.getStackInSlot(slot_wrt);
		if (itemstack.isEmpty()) return null;
		if (!(itemstack.getItem() instanceof IItemPageProvider)) return null;
		return ((IItemPageProvider) itemstack.getItem()).getPageList(player, itemstack);
	}

	public void writeSymbol(EntityPlayer player, String symbol) {
		if (world.isRemote) return;
		if (!hasEnoughInk()) return;

		// If nothing in slot, check fill slot
		if (this.inventoryStacks.getStackInSlot(slot_wrt).isEmpty() && !this.inventoryStacks.getStackInSlot(slot_pap).isEmpty()) {
		    this.inventoryStacks.setStackInSlot(slot_wrt, ItemPage.createItemstack(this.inventoryStacks.getStackInSlot(slot_pap)));
		    if(this.inventoryStacks.getStackInSlot(slot_pap).getCount() <= 0) {
		        this.inventoryStacks.setStackInSlot(slot_pap, ItemStack.EMPTY);
            }
		}

		// If nothing in slot, we're done
		if (this.inventoryStacks.getStackInSlot(slot_wrt).isEmpty()) return;

		// Do writing
		ItemStack target = this.inventoryStacks.getStackInSlot(slot_wrt);
		if (target.isEmpty()) return;
		if (target.getItem() instanceof IItemWritable && ((IItemWritable) target.getItem()).writeSymbol(player, target, symbol)) {
			useink();
			player.addStat(ModAchievements.write, 1);
			return;
		}
		ItemStack paperstack = this.inventoryStacks.getStackInSlot(slot_pap);
		if (!paperstack.isEmpty() && (target.getItem() instanceof IItemPageAcceptor)) {
			ItemStack page = paperstack.copy();
			page.setCount(1);
			page = ItemPage.createItemstack(page);
			if (!page.isEmpty()) {
				InternalAPI.page.setPageSymbol(page, symbol);
				if (((IItemPageAcceptor) target.getItem()).addPage(player, target, page).isEmpty()) {
					useink();
					paperstack.shrink(1);
				}
			}
			if(this.inventoryStacks.getStackInSlot(slot_pap).getCount() <= 0) {
                this.inventoryStacks.setStackInSlot(slot_pap, ItemStack.EMPTY);
            }
		}
	}

	@Nonnull
	public ItemStack removePageFromSurface(EntityPlayer player, @Nonnull ItemStack itemstack, int index) {
		if (itemstack.isEmpty()) return ItemStack.EMPTY;
		ItemStack result = ItemStack.EMPTY;
		if (itemstack.getItem() instanceof IItemOrderablePageProvider) result = ((IItemOrderablePageProvider) itemstack.getItem()).removePage(player, itemstack, index);
		if (result.isEmpty()) return result;
		this.markForUpdate();
		return result;
	}

    @Nonnull
    public ItemStack removePageFromSurface(EntityPlayer player, @Nonnull ItemStack itemstack, @Nonnull ItemStack page) {
        if (itemstack.isEmpty()) return ItemStack.EMPTY;
		ItemStack result = ItemStack.EMPTY;
		if (itemstack.getItem() instanceof IItemPageCollection) result = ((IItemPageCollection) itemstack.getItem()).remove(player, itemstack, page);
		if (result.isEmpty()) return result;
        this.markForUpdate();
		return result;
	}

    @Nonnull
    public ItemStack addPageToTab(EntityPlayer player, @Nonnull ItemStack itemstack, @Nonnull ItemStack page) {
        if (itemstack.isEmpty()) return page;
        ItemStack result = page;
        if (itemstack.getItem() instanceof IItemPageAcceptor) result = ((IItemPageAcceptor) itemstack.getItem()).addPage(player, itemstack, page);
        if (result == page) return result;
        this.markForUpdate();
        return result;
    }

    @Nonnull
    public ItemStack placePageOnSurface(EntityPlayer player, @Nonnull ItemStack itemstack, @Nonnull ItemStack page, int index) {
        if (itemstack.isEmpty()) return page;
		ItemStack result = page;
		if (itemstack.getItem() instanceof IItemPageCollection) result = ((IItemPageCollection) itemstack.getItem()).addPage(player, itemstack, page);
		if (itemstack.getItem() instanceof IItemOrderablePageProvider) result = ((IItemOrderablePageProvider) itemstack.getItem()).setPage(player, itemstack, page, index);
		if (result == page) return result;
        this.markForUpdate();
		return result;
	}

	@Nonnull
	public ItemStack getTarget() {
	    return this.inventoryStacks.getStackInSlot(slot_wrt);
	}

	@Nullable
	public FluidStack getInk() {
		return inkwell.getFluid();
	}

	public void setInk(@Nullable FluidStack fluid) {
		inkwell.setFluid(fluid);
	}

	private boolean hasEnoughInk() {
        FluidStack fluid = inkwell.getFluid();
        return fluid != null && fluid.amount >= Mystcraft.inkcost;
    }

	private void useink() {
		inkwell.drain(Mystcraft.inkcost, true);
	}

    @Override
    public void update() {
        if(world.isRemote) return;
        if(BlockWritingDesk.isBlockFoot(world.getBlockState(pos))) {
            this.tileEntityInvalid = true; //Uh... that doesn't change anything but.. SURE..
        }
        if(!this.inventoryStacks.getStackInSlot(slot_ctn).isEmpty()) {
            ItemStack fluidContainer = this.inventoryStacks.getStackInSlot(slot_ctn);
            ItemStack emptyContainer = fluidContainer.getItem().getContainerItem(fluidContainer);
            if(emptyContainer.isEmpty() || mergeItemStacksLeft(this.inventoryStacks.getStackInSlot(slot_out), emptyContainer) != this.inventoryStacks.getStackInSlot(slot_out)) {
                ItemStack result = FluidUtils.fillTankWithContainer(inkwell, fluidContainer);
                if(!result.isEmpty()) {
                    this.inventoryStacks.setStackInSlot(slot_out, mergeItemStacksLeft(this.inventoryStacks.getStackInSlot(slot_out), result));
                    if(fluidContainer.getCount() <= 0) {
                        this.inventoryStacks.setStackInSlot(slot_ctn, ItemStack.EMPTY);
                    }
                }
            }
        }
        if(!this.inventoryStacks.getStackInSlot(slot_ctn).isEmpty()) {
            ItemStack container = this.inventoryStacks.getStackInSlot(slot_ctn);
            FluidActionResult far = FluidUtil.tryFillContainer(container, inkwell, Fluid.BUCKET_VOLUME, null, false);
            if(far.isSuccess()) {
                //TODO Hellfire> test fluid stuff tank -> item and vice versa
                if(mergeItemStacksLeft(this.inventoryStacks.getStackInSlot(slot_out), far.getResult()) != this.inventoryStacks.getStackInSlot(slot_out)) {
                    this.inventoryStacks.setStackInSlot(slot_out,
                            mergeItemStacksLeft(this.inventoryStacks.getStackInSlot(slot_out), FluidUtil.tryFillContainer(container, inkwell, Fluid.BUCKET_VOLUME, null, true).getResult()));
                    if(container.getCount() <= 0) {
                        this.inventoryStacks.setStackInSlot(slot_ctn, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

	//XXX: (Fluids) Improve how I handle fluid containers and items
    // Hellfire> +1 that ^ - will do. eventually. TODO use forge's FluidUtil to do fluids 'better'
    //Lol... duplicate code from inkMixer.
    @Nonnull
	private static ItemStack mergeItemStacksLeft(@Nonnull ItemStack left, @Nonnull ItemStack right) {
		if (right.isEmpty()) return left;
		if (left.isEmpty()) return right;
		if (left.getItem() != right.getItem()) {
			return left;
		} else if (left.hasTagCompound() != right.hasTagCompound()) {
			return left;
		} else if (left.hasTagCompound() && !left.getTagCompound().equals(right.getTagCompound())) {
			return left;
		} else if (left.getItem().getHasSubtypes() && left.getItemDamage() != right.getItemDamage()) {
			return left;
		} else if (left.getCount() + right.getCount() > left.getMaxStackSize()) {
		    return left;
		}
		left = left.copy();
		left.grow(right.getCount());
		right.setCount(0);
		return left;
	}

	public void link(Entity player) {
		ItemStack book = getTarget();
		if (book.isEmpty()) return;
		if (!(book.getItem() instanceof ItemLinking)) return;
		((ItemLinking) book.getItem()).activate(book, world, player);
	}

	public IOInventory getContainerItemHandler() {
        IOInventory inv = IOInventory.mergeBuild(this, inventoryStacks, inventoryTabItems);
        inv.allowAnySlots = true;
        return inv;
    }

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventoryStacks.getCapability(facing);
        }
        if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return (T) inkwell;
        }
        return null;
    }

	public FluidTankFiltered getInkwell() {
		return inkwell;
	}

	@Override
	@Nonnull
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(-1, 0, -1, 2, 2, 2).offset(pos);
	}
}
