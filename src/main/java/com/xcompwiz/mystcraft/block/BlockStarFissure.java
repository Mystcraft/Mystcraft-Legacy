package com.xcompwiz.mystcraft.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import com.xcompwiz.mystcraft.api.event.StarFissureLinkEvent;
import com.xcompwiz.mystcraft.api.linking.ILinkInfo;
import com.xcompwiz.mystcraft.data.Sounds;
import com.xcompwiz.mystcraft.linking.LinkController;
import com.xcompwiz.mystcraft.linking.LinkOptions;
import com.xcompwiz.mystcraft.oldapi.internal.ILinkPropertyAPI;
import com.xcompwiz.mystcraft.tileentity.TileEntityStarFissure;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStarFissure extends BlockContainer {

	public static Block			instance;

	private static LinkOptions	defaultstarfissure	= new LinkOptions(null);
	static {
		defaultstarfissure.setFlag(ILinkPropertyAPI.FLAG_NATURAL, true);
		defaultstarfissure.setFlag(ILinkPropertyAPI.FLAG_EXTERNAL, true);
		defaultstarfissure.setProperty(ILinkPropertyAPI.PROP_SOUND, Sounds.FISSURELINK);
	}

	public BlockStarFissure(Material material) {
		super(material);
		setLightLevel(0.4F);
		setTickRandomly(false);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityStarFissure();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister par1IconRegister) {}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
		setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
	}

	@Override
	public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List list, Entity entity) {}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int quantityDropped(Random random) {
		return 0;
	}

	@Override
	public void onEntityCollidedWithBlock(World worldObj, int i, int j, int k, Entity entity) {
		ILinkInfo info = defaultstarfissure.clone();
		info.setSpawnYaw(entity.rotationYaw);
		MinecraftForge.EVENT_BUS.post(new StarFissureLinkEvent(worldObj, entity, info));
		LinkController.travelEntity(worldObj, entity, info);
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {}
}
