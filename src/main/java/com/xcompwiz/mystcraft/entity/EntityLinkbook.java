package com.xcompwiz.mystcraft.entity;

import com.xcompwiz.mystcraft.Mystcraft;
import com.xcompwiz.mystcraft.data.ModGUIs;
import com.xcompwiz.mystcraft.item.ItemLinking;
import com.xcompwiz.mystcraft.linking.LinkOptions;

import com.xcompwiz.mystcraft.tileentity.InventoryFilter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityLinkbook extends EntityLiving {

	private static final DataParameter<ItemStack> BOOK = EntityDataManager.createKey(EntityLinkbook.class, DataSerializers.ITEM_STACK);
	private static final DataParameter<String> AGE_NAME = EntityDataManager.createKey(EntityLinkbook.class, DataSerializers.STRING);

	private int decaytimer;

	public EntityLinkbook(World world) {
		super(world);
		setSize(0.25F, 0.2F);
		newPosRotationIncrements = 0;
	}

	public EntityLinkbook(World world, double d, double d1, double d2) {
		this(world);
		setPosition(d, d1, d2);
	}

	public EntityLinkbook(World world, Entity entity, ItemStack item) {
		this(world);
		dataManager.set(BOOK, item);
		dataManager.set(AGE_NAME, LinkOptions.getDisplayName(item.getTagCompound()));
		setLocationAndAngles(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ, entity.rotationYaw, entity.rotationPitch);
		this.motionX = entity.motionX;
		this.motionY = entity.motionY;
		this.motionZ = entity.motionZ;
		posX -= MathHelper.cos((rotationYaw / 180F) * 3.141593F) * 0.16F;
		posY -= 0.10000000149011612D;
		posZ -= MathHelper.sin((rotationYaw / 180F) * 3.141593F) * 0.16F;
		setPosition(posX, posY, posZ);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(BOOK, ItemStack.EMPTY);
		dataManager.register(AGE_NAME, "");
	}

	@Nonnull
	public ItemStack getBook() {
		return dataManager.get(BOOK);
	}

	public void setBook(@Nonnull ItemStack stack) {
		dataManager.set(BOOK, stack);
	}

	@Nonnull
	public String getAgeName() {
		return dataManager.get(AGE_NAME);
	}

	public void linkEntity(Entity entity) {
		ItemStack book = getBook();
		if (book.isEmpty())
			return;
		if (!(book.getItem() instanceof ItemLinking))
			return;
		((ItemLinking) book.getItem()).activate(book, world, entity);
	}

	@Nullable
	@Override
	protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
		return null;
	}

	@Override
	@Nullable
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	public boolean attackEntityFrom(@Nonnull DamageSource damagesource, float i) {
		if (damagesource == DamageSource.IN_WALL)
			return false;
		if (damagesource.isFireDamage()) {
			i *= 2;
			setFire((int) i);
		}
		return super.attackEntityFrom(damagesource, i);
	}

	@Override
	public void setHealth(float par1) {
		super.setHealth(par1);
		updateBookHealth();
	}

	private void updateBookHealth() {
		if (getBook().isEmpty()) {
			return;
		}
		ItemLinking.setHealth(getBook(), this.getHealth());
	}

	private float getBookHealth() {
		return ItemLinking.getHealth(getBook());
	}

	@Override
	public void knockBack(Entity entity, float i, double d, double d1) {}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("DecayTimer", decaytimer);
		nbttagcompound.setTag("Item", getBook().writeToNBT(new NBTTagCompound()));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);

		decaytimer = nbttagcompound.getInteger("DecayTimer");
		ItemStack book = new ItemStack(nbttagcompound.getCompoundTag("Item"));
		if (book.isEmpty()) {
			setDead();
		}
		dataManager.set(BOOK, book);
		dataManager.set(AGE_NAME, LinkOptions.getDisplayName(book.getTagCompound()));
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		if (entity instanceof EntityMinecartHopper && !world.isRemote) {
			ItemStack book = getBook();
			if (book.isEmpty())
				return;
			EntityMinecartHopper hoppercart = (EntityMinecartHopper) entity;
			ItemStack itemstack = book.copy();
			ItemStack itemstack1 = TileEntityHopper.putStackInInventoryAllSlots(null, hoppercart, itemstack, null);

			if (itemstack1.isEmpty()) {
				this.setDead();
			} else {
				dataManager.set(BOOK, itemstack1);
			}
			return;
		}
		super.collideWithEntity(entity);
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (world.isRemote)
			return true;
		if (player.isSneaking() && player.getHeldItem(hand).isEmpty()) {
			player.setHeldItem(hand, getBook());
			player.inventory.markDirty();
			setDead();
			return true;
		}
		player.openGui(Mystcraft.instance, ModGUIs.BOOK_ENTITY.ordinal(), world, this.getEntityId(), 0, 0);
		return true;
	}

	@Override
	public void onUpdate() {
		this.setHealth(this.getBookHealth());

		super.onUpdate();

		if (world.isRemote)
			return;
		++decaytimer;
		if (decaytimer % 10000 == 0) {
			attackEntityFrom(DamageSource.STARVE, 1);
		}
		if (isWet()) {
			attackEntityFrom(DamageSource.DROWN, 1);
		}
		if (getBook().isEmpty()) {
			setDead();
		}
	}

	/**
	 * Decrements the entity's air supply when underwater
	 */
	@Override
	protected int decreaseAirSupply(int air) {
		return air - 2;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public boolean isNoDespawnRequired() {
		return true;
	}

	@Override
	public boolean canAttackClass(Class cl) {
		return false;
	}

	@Override
	protected void updateAITasks() {}

	@Override
	public void setMoveForward(float par1) {}

	@Override
	public void setJumping(boolean par1) {}

	@Override
	public void setAIMoveSpeed(float par1) {}

	@Override
	public void faceEntity(Entity par1Entity, float par2, float par3) {}

	@Override
	public void setRevengeTarget(EntityLivingBase par1) {}

	@Nullable
	@Override
	public EntityLivingBase getRevengeTarget() {
		return null;
	}

	@Nonnull
	public EntityLinkbookItemWrapper createBookWrapper() {
		return new EntityLinkbookItemWrapper(this);
	}

	private class EntityLinkbookItemWrapper implements IItemHandlerModifiable, InventoryFilter {

		private final EntityLinkbook parent;

		public EntityLinkbookItemWrapper(EntityLinkbook linkbook) {
			this.parent = linkbook;
		}

		@Override
		public boolean canAcceptItem(int slot, @Nonnull ItemStack stack) {
			return slot == 0 && !stack.isEmpty() && stack.getItem() instanceof ItemLinking;
		}

		@Override
		public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
			if (slot == 0) {
				parent.setBook(stack);
			}
		}

		@Override
		public int getSlots() {
			return 1;
		}

		@Nonnull
		@Override
		public ItemStack getStackInSlot(int slot) {
			if (slot == 0) {
				return parent.getBook();
			}
			return ItemStack.EMPTY;
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (!canAcceptItem(slot, stack)) {
				return stack;
			}
			setStackInSlot(0, stack);
			return ItemStack.EMPTY;
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (slot == 0) {
				ItemStack in = getStackInSlot(0); //Book, if present
				if (!simulate) {
					setStackInSlot(0, ItemStack.EMPTY);
				}
				if (parent.getBook().isEmpty()) {
					parent.setDead();
				}
				return in;
			}
			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot) {
			return parent.getBook().getMaxStackSize();
		}
	}

}
