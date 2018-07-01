package com.xcompwiz.mystcraft.item;

import com.xcompwiz.mystcraft.core.MystcraftCommonProxy;
import com.xcompwiz.mystcraft.data.ModFluids;
import com.xcompwiz.mystcraft.data.ModItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemInkVial extends Item {

	public ItemInkVial() {
		setMaxStackSize(16);
		setUnlocalizedName("myst.vial");
		setCreativeTab(MystcraftCommonProxy.tabMystCommon);
		setContainerItem(Items.GLASS_BOTTLE);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new InkVialFluidHandler(stack);
	}

	public static class InkVialFluidHandler implements IFluidHandlerItem, ICapabilityProvider {

		@Nonnull
		private ItemStack container;

		InkVialFluidHandler(@Nonnull ItemStack container) {
			this.container = container;
		}

		@Nonnull
		@Override
		public ItemStack getContainer() {
			return container;
		}

		@Nullable
		Fluid getContainedFluid() {
			if (container.isEmpty() || !container.getItem().equals(ModItems.inkvial))
				return null;
			return ModFluids.black_ink;
		}

		@Nullable
		public FluidStack getFluid() {
			if (getContainedFluid() == null) {
				return null;
			}
			return new FluidStack(getContainedFluid(), Fluid.BUCKET_VOLUME);
		}

		void setFluid(@Nullable Fluid f) {
			if (f == null) {
				container = new ItemStack(Items.GLASS_BOTTLE);
			} else {
				container = new ItemStack(ModItems.inkvial);
			}
		}

		@Override
		public IFluidTankProperties[] getTankProperties() {
			return new IFluidTankProperties[] { new FluidTankProperties(getFluid(), Fluid.BUCKET_VOLUME) };
		}

		@Override
		public int fill(FluidStack resource, boolean doFill) {
			if (container.getCount() != 1 || resource == null || resource.amount < Fluid.BUCKET_VOLUME || container.getItem() instanceof ItemInkVial || getFluid() != null || resource.getFluid() != ModFluids.black_ink) {
				return 0;
			}

			if (doFill) {
				setFluid(resource.getFluid());
			}

			return Fluid.BUCKET_VOLUME;
		}

		@Nullable
		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain) {
			if (resource == null || resource.amount < Fluid.BUCKET_VOLUME) {
				return null;
			}
			FluidStack fluidStack = getFluid();
			if (fluidStack != null && fluidStack.isFluidEqual(resource)) {
				if (doDrain) {
					setFluid(null);
				}
				return fluidStack;
			}
			return null;
		}

		@Nullable
		@Override
		public FluidStack drain(int maxDrain, boolean doDrain) {
			if (maxDrain < Fluid.BUCKET_VOLUME) {
				return null;
			}
			FluidStack fluidStack = getFluid();
			if (fluidStack != null) {
				if (doDrain) {
					setFluid(null);
				}
				return fluidStack;
			}

			return null;
		}

		@Override
		public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
			return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY;
		}

		@Nullable
		@Override
		public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
			if (capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY) {
				return CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY.cast(this);
			}
			return null;
		}
	}

}
