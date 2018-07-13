package com.xcompwiz.mystcraft.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import com.xcompwiz.mystcraft.nbt.NBTUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFallingBlock extends Entity implements IEntityAdditionalSpawnData {

	protected static final DataParameter<BlockPos> ORIGIN = EntityDataManager.<BlockPos> createKey(net.minecraft.entity.item.EntityFallingBlock.class, DataSerializers.BLOCK_POS);

	private static final String NBT_Drops = "Drops";
	private static final String NBT_TE = "TE";

	public IBlockState falltile;
	public int fallTime;
	private NBTTagCompound data;
	private ArrayList collidingBoundingBoxes = new ArrayList();

	public EntityFallingBlock(World world) {
		super(world);
		fallTime = 0;
	}

	private EntityFallingBlock(World world, double d, double d1, double d2, IBlockState state, NBTTagCompound data) {
		super(world);
		fallTime = 0;
		this.falltile = state;
		preventEntitySpawning = true;
		setSize(0.98F, 0.98F);
		setPosition(d, d1, d2);
		motionX = 0.0D;
		motionY = 0.0D;
		motionZ = 0.0D;
		prevPosX = d;
		prevPosY = d1;
		prevPosZ = d2;
		this.data = data;
		this.setOrigin(new BlockPos(this));
	}

	public void setOrigin(BlockPos pos) {
		this.dataManager.set(ORIGIN, pos);
	}

	@SideOnly(Side.CLIENT)
	public BlockPos getOrigin() {
		return this.dataManager.get(ORIGIN);
	}

	@Override
	protected void entityInit() {
		this.dataManager.register(ORIGIN, BlockPos.ORIGIN);
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	public static void cascade(World world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock().isLeaves(world.getBlockState(pos), world, pos)) {
			drop(world, pos);
		}
	}

	@Override
	public double getYOffset() {
		return height / 2D;
	}

	public static void drop(World world, BlockPos pos) {
		if (world.isRemote) {
			return;
		}
		boolean flag = false;
		IBlockState posState = world.getBlockState(pos);
		Material material = posState.getMaterial();
		while (material == Material.LAVA || material == Material.WATER) {
			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			return;
		}
		if (flag)
			return;
		if (world.isAirBlock(pos))
			return;
		if (!world.isAirBlock(pos.down()))
			return;

		NBTTagCompound data = new NBTTagCompound();

		List drops = posState.getBlock().getDrops(world, pos, posState, 0);
		data.setTag(NBT_Drops, NBTUtils.writeItemStackCollection(new NBTTagList(), drops));
		if (world.getTileEntity(pos) != null) {
			NBTTagCompound tedata = new NBTTagCompound();
			world.getTileEntity(pos).writeToNBT(tedata);
			world.removeTileEntity(pos);
			data.setTag(NBT_TE, tedata);
		}
		EntityFallingBlock entityfalling = new EntityFallingBlock(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, posState, data);
		world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
		world.spawnEntity(entityfalling);
	}

	@Override
	public boolean isWet() {
		return isInWater();
	}

	@Override
	public boolean canBeCollidedWith() {
		return !isDead;
	}

	@Override
	public void onUpdate() {
		if (falltile == null) {
			setDead();
			return;
		}
		++fallTime;
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= 0.039999999105930328D;
		move(MoverType.SELF, motionX, motionY, motionZ);
		motionX *= 0.98000001907348633D;
		motionY *= 0.98000001907348633D;
		motionZ *= 0.98000001907348633D;
		int x = MathHelper.floor(posX);
		int y = MathHelper.floor(posY);
		int z = MathHelper.floor(posZ);
		if (onGround) {
			if (world.isRemote)
				return;
			setDead();
			place(new BlockPos(x, y, z));
			return;
		} else if ((posY < -10) && !world.isRemote) {
			setDead();
			return;
		}
		if (fallTime == 1 && !world.isRemote) {
			for (EnumFacing face : EnumFacing.HORIZONTALS) {
				cascade(world, getPosition().offset(face));
			}
			for (EnumFacing face : EnumFacing.VALUES) {
				if (face != EnumFacing.DOWN) {
					//drop(world, getPosition().offset(face));
				}
			}
		}
	}

	private void place(BlockPos pos) {
		if (!world.setBlockState(pos, falltile, 2)) {
			handleDrops();
		} else {
			if (data != null && data.hasKey(NBT_TE)) {
				NBTTagCompound tileentity = data.getCompoundTag(NBT_TE);
				tileentity.setInteger("x", pos.getX());
				tileentity.setInteger("y", pos.getY());
				tileentity.setInteger("z", pos.getZ());
				if (world.getTileEntity(pos) != null) {
					world.getTileEntity(pos).readFromNBT(tileentity);
				} else {
					world.setTileEntity(pos, TileEntity.create(world, tileentity));
				}
			}
		}
	}

	@Override
	public void move(MoverType type, double x, double y, double z) {
		if (this.noClip) {
			this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, y, z));
			this.resetPositionToBB();
		} else {
			this.world.profiler.startSection("move");
			double d10 = this.posX;
			double d11 = this.posY;
			double d1 = this.posZ;

			if (this.isInWeb) {
				this.isInWeb = false;
				x *= 0.25D;
				y *= 0.05000000074505806D;
				z *= 0.25D;
				this.motionX = 0.0D;
				this.motionY = 0.0D;
				this.motionZ = 0.0D;
			}

			double d2 = x;
			double d3 = y;
			double d4 = z;

			AxisAlignedBB box = this.getEntityBoundingBox();
			box = addCoord(x, y, z, box);
			List<AxisAlignedBB> list1 = this.world.getCollisionBoxes(this, box);

			if (y != 0.0D) {
				int k = 0;

				for (int l = list1.size(); k < l; ++k) {
					y = list1.get(k).calculateYOffset(this.getEntityBoundingBox(), y);
				}

				this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, y, 0.0D));
			}

			if (x != 0.0D) {
				int j5 = 0;

				for (int l5 = list1.size(); j5 < l5; ++j5) {
					x = list1.get(j5).calculateXOffset(this.getEntityBoundingBox(), x);
				}

				if (x != 0.0D) {
					this.setEntityBoundingBox(this.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
				}
			}

			if (z != 0.0D) {
				int k5 = 0;

				for (int i6 = list1.size(); k5 < i6; ++k5) {
					z = list1.get(k5).calculateZOffset(this.getEntityBoundingBox(), z);
				}

				if (z != 0.0D) {
					this.setEntityBoundingBox(this.getEntityBoundingBox().offset(0.0D, 0.0D, z));
				}
			}

			boolean flag = this.onGround || d3 != y && d3 < 0.0D;
			this.world.profiler.endSection();
			this.world.profiler.startSection("rest");
			this.resetPositionToBB();
			this.collidedHorizontally = d2 != x || d4 != z;
			this.collidedVertically = d3 != y;
			this.onGround = this.collidedVertically && d3 < 0.0D;
			this.collided = this.collidedHorizontally || this.collidedVertically;
			int j6 = MathHelper.floor(this.posX);
			int i1 = MathHelper.floor(this.posY - 0.20000000298023224D);
			int k6 = MathHelper.floor(this.posZ);
			BlockPos blockpos = new BlockPos(j6, i1, k6);
			IBlockState iblockstate = this.world.getBlockState(blockpos);

			if (iblockstate.getMaterial() == Material.AIR) {
				BlockPos blockpos1 = blockpos.down();
				IBlockState iblockstate1 = this.world.getBlockState(blockpos1);
				Block block1 = iblockstate1.getBlock();

				if (block1 instanceof BlockFence || block1 instanceof BlockWall || block1 instanceof BlockFenceGate) {
					iblockstate = iblockstate1;
					blockpos = blockpos1;
				}
			}

			this.updateFallState(y, this.onGround, iblockstate, blockpos);

			if (d2 != x) {
				this.motionX = 0.0D;
			}

			if (d4 != z) {
				this.motionZ = 0.0D;
			}

			Block block = iblockstate.getBlock();

			if (d3 != y) {
				block.onLanded(this.world, this);
			}

			try {
				this.doBlockCollisions();
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
				this.addEntityCrashInfo(crashreportcategory);
				throw new ReportedException(crashreport);
			}

			this.world.profiler.endSection();
		}
	}

	private void handleDrops() {
		NBTTagList drops = data.getTagList(NBT_Drops, Constants.NBT.TAG_COMPOUND);
		Collection<ItemStack> items = NBTUtils.readItemStackCollection(drops, new ArrayList<>());
		for (ItemStack itemstack : items) {
			entityDropItem(itemstack, 0.0F);
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		Block block = this.falltile != null ? this.falltile.getBlock() : Blocks.AIR;
		ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(block);
		compound.setString("Block", resourcelocation == null ? "" : resourcelocation.toString());
		compound.setByte("Data", (byte) block.getMetaFromState(this.falltile));
		compound.setInteger("Time", this.fallTime);
		if (this.data != null) {
			compound.setTag("TileEntityData", this.data);
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		int i = compound.getByte("Data") & 255;

		this.falltile = null; //Reset
		if (compound.hasKey("Block", 8)) {
			Block b = Block.getBlockFromName(compound.getString("Block"));
			if (b != null) {
				this.falltile = b.getStateFromMeta(i);
			}
		}

		this.fallTime = compound.getInteger("Time");

		if (compound.hasKey("TileEntityData", 10)) {
			this.data = compound.getCompoundTag("TileEntityData");
		}

		if (falltile == null || falltile.getMaterial() == Material.AIR) {
			this.falltile = Blocks.SAND.getDefaultState();
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean canRenderOnFire() {
		return false;
	}

	@Nullable
	public IBlockState getBlock() {
		return this.falltile;
	}

	public boolean ignoreItemEntityData() {
		return true;
	}

	public World getWorld() {
		return world;
	}

	@Override
	public void writeSpawnData(ByteBuf data) {
		data.writeInt(Block.getStateId(this.falltile));
	}

	@Override
	public void readSpawnData(ByteBuf data) {
		falltile = Block.getStateById(data.readInt());
	}

	//Doesn't exist anymore in AABB
	private AxisAlignedBB addCoord(double x, double y, double z, AxisAlignedBB toAddTo) {
		double d0 = toAddTo.minX;
		double d1 = toAddTo.minY;
		double d2 = toAddTo.minZ;
		double d3 = toAddTo.maxX;
		double d4 = toAddTo.maxY;
		double d5 = toAddTo.maxZ;
		if (x < 0.0D) {
			d0 += x;
		} else if (x > 0.0D) {
			d3 += x;
		}
		if (y < 0.0D) {
			d1 += y;
		} else if (y > 0.0D) {
			d4 += y;
		}
		if (z < 0.0D) {
			d2 += z;
		} else if (z > 0.0D) {
			d5 += z;
		}
		return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
	}

}
