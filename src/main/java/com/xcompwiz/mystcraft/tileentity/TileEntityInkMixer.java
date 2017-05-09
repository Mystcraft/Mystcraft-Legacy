package com.xcompwiz.mystcraft.tileentity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.InkEffects;
import com.xcompwiz.mystcraft.data.ModLinkEffects;
import com.xcompwiz.mystcraft.fluids.FluidUtils;
import com.xcompwiz.mystcraft.inventory.IItemBuilder;
import com.xcompwiz.mystcraft.item.ItemPage;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.page.Page;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityInkMixer extends TileEntityBase implements IItemBuilder, InventoryFilter, InventoryUpdateListener, ITickable {

	private IOInventory inventory;

	private boolean					hasInk				= false;
	private HashMap<String, Float>	ink_probabilities	= new HashMap<>();

	private long					next_seed;

	private static final int		ink_in				= 0;
	private static final int		ink_out				= 2;
	private static final int		paper				= 1;

	public TileEntityInkMixer() {
		next_seed = new Random().nextLong();
		inventory = buildInventory();
	}

	protected IOInventory buildInventory() {
	    return new IOInventory(this, new int[] { ink_in, paper }, new int[0], EnumFacing.VALUES)
                .setMiscSlots(new int[] { ink_out }) //Not input, can't output from this, so.... misc i guess.
                .setListener(this)
                .applyFilter(this, ink_in, paper);
    }

    @Override
    public void onChange() {
        markForUpdate();
    }

    @Override
    public boolean canAcceptItem(int slot, @Nonnull ItemStack stack) {
	    if(stack.isEmpty()) return false;
	    if(slot == ink_in) {
	        FluidStack fluidStack = FluidUtil.getFluidContained(stack);
	        if(fluidStack != null) {
	            return Mystcraft.validInks.contains(fluidStack.getFluid().getName());
            }
        }
	    if(slot == paper && stack.getItem().equals(Items.PAPER)) return true;
        return false;
    }

    @Override
    public void readCustomNBT(NBTTagCompound compound) {
        super.readCustomNBT(compound);

        this.inventory = IOInventory.deserialize(this, compound.getCompoundTag("inventory"));
        this.hasInk = compound.getBoolean("ink");
        this.ink_probabilities = NBTUtils.readFloatMap(compound.getCompoundTag("probabilities"), new HashMap<>());
    }

    @Override
    public void writeCustomNBT(NBTTagCompound compound) {
        super.writeCustomNBT(compound);
        compound.setTag("inventory", this.inventory.writeNBT());
        compound.setBoolean("ink", this.hasInk);
        compound.setTag("probabilities", NBTUtils.writeFloatMap(new NBTTagCompound(), this.ink_probabilities));
    }

	@Override
	public void buildItem(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer player) {
		if (!canBuildItem()) return;
		if (itemstack.getItem() instanceof ItemPage) {
			Random rand = new Random(next_seed);
			for (Entry<String, Float> entry : ink_probabilities.entrySet()) {
				float f = entry.getValue() * 100;
				int i = rand.nextInt(100);
				if (i < f) {
					Page.addLinkProperty(itemstack, entry.getKey());
				}
			}
			next_seed = rand.nextLong();
			hasInk = false;
			ink_probabilities.clear();
			this.inventory.getStackInSlot(paper).shrink(1);
			markForUpdate();
		} else {
			itemstack.setCount(0);
		}
	}

	private boolean canBuildItem() {
	    return !this.inventory.getStackInSlot(paper).isEmpty() && this.inventory.getStackInSlot(paper).getItem().equals(Items.PAPER) && hasInk;
	}

    @Nonnull
    public ItemStack getCraftedItem() {
		if (!canBuildItem()) return ItemStack.EMPTY;
		return Page.createLinkPage();
	}

	public boolean getHasInk() {
		return hasInk;
	}

	public void setHasInk(boolean b) {
		hasInk = b;
	}

	public long getNextSeed() {
		return next_seed;
	}

	public void setNextSeed(long seed) {
		this.next_seed = seed;
	}

    @Override
    public void update() {
        if(world.isRemote) return;

        if(!this.inventory.getStackInSlot(ink_in).isEmpty() && !hasInk) {
            ItemStack fluidContainer = this.inventory.getStackInSlot(ink_in);
            ItemStack emptyContainer = fluidContainer.getItem().getContainerItem(fluidContainer);
            if(emptyContainer.isEmpty() || mergeItemStacksLeft(this.inventory.getStackInSlot(ink_out), emptyContainer) != this.inventory.getStackInSlot(ink_out)) {
                ItemStack result = fillBasinWithContainer(fluidContainer);
                if(!result.isEmpty()) {
                    this.inventory.setStackInSlot(ink_out, mergeItemStacksLeft(this.inventory.getStackInSlot(ink_out), result));
                    if(fluidContainer.getCount() <= 0) {
                        this.inventory.setStackInSlot(ink_in, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
	// XXX: (Fluids) Handle fluids better
    // Hellfire> +1 that ^ - will do. eventually. TODO use forge's FluidUtil to do fluids 'better'
    @Nonnull
    private ItemStack fillBasinWithContainer(@Nonnull ItemStack containerStack) {
		ItemStack container = containerStack.copy();
		container.setCount(1);
		FluidStack contained = FluidUtil.getFluidContained(container);
		if (contained != null) {
			if (!Mystcraft.validInks.contains(contained.getFluid().getName())) {
			    return ItemStack.EMPTY;
            }
			int used = Fluid.BUCKET_VOLUME;
			if (used == contained.amount) {
			    containerStack.shrink(1);
				hasInk = true;
				container = FluidUtils.emptyContainer(container);
				return container;
			}
		}
		return ItemStack.EMPTY;
	}

    @Nonnull
    private ItemStack mergeItemStacksLeft(@Nonnull ItemStack left, @Nonnull ItemStack right) {
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

	public Map<String, Float> getInkProperties() {
		return Collections.unmodifiableMap(ink_probabilities);
	}

	@Nonnull
	public ItemStack addItems(@Nonnull ItemStack itemstack, int amount) {
        Map<String, Float> itemprobs = InkEffects.getItemEffects(itemstack);

		if (itemprobs == null) return itemstack;

		float total = 0.0F;
		for (Entry<String, Float> entry : itemprobs.entrySet()) {
			if (entry.getKey().equals("")) continue;
			if (!isPropertyAllowed(entry.getKey())) continue;
			total += entry.getValue();
		}
		float inverse = 1 - total;

		if (amount > itemstack.getCount()) {
		    amount = itemstack.getCount();
        }
		for (int i = 0; i < amount; ++i) {
		    itemstack.shrink(1);
			for (Entry<String, Float> entry : ink_probabilities.entrySet()) {
				entry.setValue(entry.getValue() * inverse);
			}
			for (Entry<String, Float> entry : itemprobs.entrySet()) {
				if (entry.getKey().equals("")) continue;
				if (!isPropertyAllowed(entry.getKey())) continue;
				float prob = entry.getValue();
				Float f = ink_probabilities.get(entry.getKey());
				if (f != null) prob += f;
				ink_probabilities.put(entry.getKey(), prob);
			}
		}
		if (itemstack.getCount() <= 0) {
			itemstack = ItemStack.EMPTY;
		}
		return itemstack;
	}

	private boolean isPropertyAllowed(String property) {
		return ModLinkEffects.isPropertyAllowed(property);
	}

	private void addTraitWithProbability(String trait, float prob) {
		float inverse = 1 - prob;
		for (Entry<String, Float> entry : ink_probabilities.entrySet()) {
			entry.setValue(entry.getValue() * inverse);
		}
		if (trait.equals("")) {
		    return;
        }
		Float f = ink_probabilities.get(trait);
		if (f != null) {
		    prob += f;
        }
		ink_probabilities.put(trait, prob);
	}

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && this.inventory.hasCapability(facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory.getCapability(facing);
        }
        return null;
    }
}
