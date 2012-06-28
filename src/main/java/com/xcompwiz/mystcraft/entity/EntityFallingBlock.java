package com.xcompwiz.mystcraft.entity;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.block.BlockDecay;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityFallingBlock extends Entity implements IEntityAdditionalSpawnData {

	public Block			block;
	public int				metadata;
	public int				fallTime;
	private NBTTagCompound	tileentity;

	public EntityFallingBlock(World world) {
		super(world);
		fallTime = 0;
	}

	public EntityFallingBlock(World world, double d, double d1, double d2, Block block, int j, NBTTagCompound tileentity) {
		super(world);
		fallTime = 0;
		this.block = block;
		metadata = j;
		preventEntitySpawning = true;
		setSize(0.98F, 0.98F);
		yOffset = height / 2.0F;
		setPosition(d, d1, d2);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = d;
		prevPosY = d1;
		prevPosZ = d2;
		this.tileentity = tileentity;
	}

	public static void cascade(World world, int i, int j, int k) {
		if (world.getBlock(i, j, k) == Blocks.leaves) {
			drop(world, i, j, k);
		}
	}

	public static void drop(World world, int i, int j, int k) {
		boolean flag = false;
		Material material = world.getBlock(i, j, k).getMaterial();
		while (material == Material.lava || material == Material.water) {
			flag = true;
			world.setBlock(i, j, k, Blocks.air, 0, 3);
			++j;
			material = world.getBlock(i, j, k).getMaterial();
		}
		if (flag) return;
		if (world.isAirBlock(i, j, k)) return;
		if (!world.isAirBlock(i, j - 1, k)) return;
		if (!world.isRemote) {
			NBTTagCompound data = null;
			if (world.getTileEntity(i, j, k) != null) {
				data = new NBTTagCompound();
				world.getTileEntity(i, j, k).writeToNBT(data);
				world.removeTileEntity(i, j, k);
			}
			EntityFallingBlock entityfalling = new EntityFallingBlock(world, i + 0.5F, j + 0.5F, k + 0.5F, world.getBlock(i, j, k), world.getBlockMetadata(i, j, k), data);
			world.setBlock(i, j, k, Blocks.air, 0, 3);
			world.spawnEntityInWorld(entityfalling);
		}
	}

	@Override
	protected boolean canTriggerWalking() {
		return true;
	}

	@Override
	protected void entityInit() {}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@Override
	public void onUpdate() {
		if (block == null) {
			setDead();
			return;
		}
		++fallTime;
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= 0.039999999105930328D;
		moveEntity(motionX, motionY, motionZ);
		motionX *= 0.98000001907348633D;
		motionY *= 0.98000001907348633D;
		motionZ *= 0.98000001907348633D;
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY);
		int k = MathHelper.floor_double(posZ);
		if (onGround) {
			if (worldObj.isRemote) return;
			setDead();
			if (!worldObj.setBlock(i, j, k, block, metadata, 2)) {
				if (block != BlockDecay.instance) {
					handleDrops();
				}
			} else {
				worldObj.setBlockMetadataWithNotify(i, j, k, metadata, 2);
				if (tileentity != null) {
					tileentity.setInteger("x", i);
					tileentity.setInteger("y", j);
					tileentity.setInteger("z", k);
					if (worldObj.getTileEntity(i, j, k) != null) {
						worldObj.getTileEntity(i, j, k).readFromNBT(tileentity);
					} else {
						worldObj.setTileEntity(i, j, k, TileEntity.createAndLoadEntity(tileentity));
					}
				}
			}
			return;
		} else if ((posY < -10) && !worldObj.isRemote) {
			setDead();
			return;
		}
		if (fallTime == 1 && !worldObj.isRemote) {
			cascade(worldObj, i - 1, j, k);
			cascade(worldObj, i + 1, j, k);
			cascade(worldObj, i, j, k - 1);
			cascade(worldObj, i, j, k + 1);
			drop(worldObj, i, j + 1, k);
			drop(worldObj, i + 1, j + 1, k);
			drop(worldObj, i - 1, j + 1, k);
			drop(worldObj, i, j + 1, k + 1);
			drop(worldObj, i, j + 1, k - 1);
		}
	}

	private void handleDrops() {
		List<ItemStack> items = getDrops();
		for (ItemStack itemstack : items) {
			entityDropItem(itemstack, 0.0F);
		}
	}

	private List<ItemStack> getDrops() {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		int count = block.quantityDropped(metadata, 0, worldObj.rand);
		for (int i = 0; i < count; i++) {
			Item item = block.getItemDropped(metadata, worldObj.rand, 0);
			if (item != null) {
				ret.add(new ItemStack(item, 1, block.damageDropped(metadata)));
			}
		}
		return ret;
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setInteger("TileID", Block.getIdFromBlock(this.block));
		nbttagcompound.setShort("Metadata", (short) metadata);
		nbttagcompound.setShort("fallTime", (short) fallTime);
		if (tileentity != null) {
			nbttagcompound.setTag("TileEntity", tileentity);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		if (nbttagcompound.hasKey("TileID")) {
			block = Block.getBlockById(nbttagcompound.getInteger("TileID"));
		} else {
			block = Block.getBlockById(nbttagcompound.getInteger("Tile"));
		}
		metadata = nbttagcompound.getShort("Metadata");
		fallTime = nbttagcompound.getShort("fallTime");
		if (nbttagcompound.hasKey("TileEntity")) {
			tileentity = nbttagcompound.getCompoundTag("TileEntity");
		}
	}

	@Override
	public float getShadowSize() {
		return 0.0F;
	}

	public World getWorld() {
		return worldObj;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeInt(Block.getIdFromBlock(this.block));
		data.writeByte(metadata);
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		block = Block.getBlockById(data.readInt());
		metadata = data.readByte();
	}
}
