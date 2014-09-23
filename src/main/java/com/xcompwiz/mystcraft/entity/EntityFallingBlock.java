package com.xcompwiz.mystcraft.entity;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

import com.xcompwiz.mystcraft.data.ModBlocks;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityFallingBlock extends Entity implements IEntityAdditionalSpawnData {

	public Block			block;
	public int				metadata;
	public int				fallTime;
	private NBTTagCompound	tileentity;
	private ArrayList		collidingBoundingBoxes	= new ArrayList();

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
		if (world.isRemote) return;
		boolean flag = false;
		Material material = world.getBlock(i, j, k).getMaterial();
		while (material == Material.lava || material == Material.water) {
			flag = true;
			world.setBlock(i, j, k, Blocks.air, 0, 2);
			++j;
			material = world.getBlock(i, j, k).getMaterial();
			return;
		}
		if (flag) return;
		if (world.isAirBlock(i, j, k)) return;
		if (!world.isAirBlock(i, j - 1, k)) return;

		NBTTagCompound data = null;
		if (world.getTileEntity(i, j, k) != null) {
			data = new NBTTagCompound();
			world.getTileEntity(i, j, k).writeToNBT(data);
			world.removeTileEntity(i, j, k);
		}
		EntityFallingBlock entityfalling = new EntityFallingBlock(world, i + 0.5F, j + 0.5F, k + 0.5F, world.getBlock(i, j, k), world.getBlockMetadata(i, j, k), data);
		world.setBlock(i, j, k, Blocks.air, 0, 2);
		world.spawnEntityInWorld(entityfalling);
	}

	@Override
	protected boolean canTriggerWalking() {
		return false;
	}

	@Override
	public boolean isWet() {
		return isInWater();
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
				if (block != ModBlocks.decay) {
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

	/**
	 * Tries to moves the entity by the passed in displacement. Args: x, y, z
	 */
	@Override
	public void moveEntity(double dx, double dy, double dz) {
		if (this.noClip) {
			this.boundingBox.offset(dx, dy, dz);
			this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
			this.posY = this.boundingBox.minY + this.yOffset - this.ySize;
			this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
		} else {
			this.worldObj.theProfiler.startSection("move");
			this.ySize *= 0.4F;

			if (this.isInWeb) {
				this.isInWeb = false;
				dx *= 0.25D;
				dy *= 0.05000000074505806D;
				dz *= 0.25D;
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}

			double dx0 = dx;
			double dy0 = dy;
			double dz0 = dz;

			this.collidingBoundingBoxes = this.getCollidingBoundingBoxes(this.boundingBox.addCoord(dx, dy, dz), this.collidingBoundingBoxes);

			for (int i = 0; i < this.collidingBoundingBoxes.size(); ++i) {
				dy = ((AxisAlignedBB) this.collidingBoundingBoxes.get(i)).calculateYOffset(this.boundingBox, dy);
			}

			this.boundingBox.offset(0.0D, dy, 0.0D);

			if (!this.field_70135_K && dy0 != dy) {
				dz = 0.0D;
				dy = 0.0D;
				dx = 0.0D;
			}

			int j;

			for (j = 0; j < this.collidingBoundingBoxes.size(); ++j) {
				dx = ((AxisAlignedBB) this.collidingBoundingBoxes.get(j)).calculateXOffset(this.boundingBox, dx);
			}

			this.boundingBox.offset(dx, 0.0D, 0.0D);

			if (!this.field_70135_K && dx0 != dx) {
				dz = 0.0D;
				dy = 0.0D;
				dx = 0.0D;
			}

			for (j = 0; j < this.collidingBoundingBoxes.size(); ++j) {
				dz = ((AxisAlignedBB) this.collidingBoundingBoxes.get(j)).calculateZOffset(this.boundingBox, dz);
			}

			this.boundingBox.offset(0.0D, 0.0D, dz);

			if (!this.field_70135_K && dz0 != dz) {
				dz = 0.0D;
				dy = 0.0D;
				dx = 0.0D;
			}

			this.worldObj.theProfiler.endSection();
			this.worldObj.theProfiler.startSection("rest");
			this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
			this.posY = this.boundingBox.minY + this.yOffset - this.ySize;
			this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
			this.isCollidedHorizontally = dx0 != dx || dz0 != dz;
			this.isCollidedVertically = dy0 != dy;
			this.onGround = dy0 != dy && dy0 < 0.0D;
			this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
			this.updateFallState(dy, this.onGround);

			if (dx0 != dx) {
				this.motionX = 0.0D;
			}

			if (dy0 != dy) {
				this.motionY = 0.0D;
			}

			if (dz0 != dz) {
				this.motionZ = 0.0D;
			}

			try {
				this.func_145775_I();
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
				this.addEntityCrashInfo(crashreportcategory);
				throw new ReportedException(crashreport);
			}

			this.worldObj.theProfiler.endSection();
		}
	}

	public ArrayList getCollidingBoundingBoxes(AxisAlignedBB bounding, ArrayList collidingBoundingBoxes) {
		collidingBoundingBoxes.clear();
		int i = MathHelper.floor_double(bounding.minX);
		int j = MathHelper.floor_double(bounding.maxX + 1.0D);
		int k = MathHelper.floor_double(bounding.minY);
		int l = MathHelper.floor_double(bounding.maxY + 1.0D);
		int i1 = MathHelper.floor_double(bounding.minZ);
		int j1 = MathHelper.floor_double(bounding.maxZ + 1.0D);

		for (int k1 = i; k1 < j; ++k1) {
			for (int l1 = i1; l1 < j1; ++l1) {
				if (this.worldObj.blockExists(k1, 64, l1)) {
					for (int i2 = k - 1; i2 < l; ++i2) {
						Block block;

						if (k1 >= -30000000 && k1 < 30000000 && l1 >= -30000000 && l1 < 30000000) {
							block = this.worldObj.getBlock(k1, i2, l1);
						} else {
							block = Blocks.stone;
						}

						block.addCollisionBoxesToList(this.worldObj, k1, i2, l1, bounding, collidingBoundingBoxes, this);
					}
				}
			}
		}
		return collidingBoundingBoxes;
	}

	private void handleDrops() {
		List<ItemStack> items = getDrops();
		for (ItemStack itemstack : items) {
			entityDropItem(itemstack, 0.0F);
		}
	}

	private List<ItemStack> getDrops() {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

		//FIXME: This isn't what the block should be dropping.  Need to use getDrops... somehow...
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
