package com.xcompwiz.mystcraft.instability.decay;

import java.util.HashMap;
import java.util.Random;

import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.world.WorldInfoUtils;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class DecayHandler {
	public static final int							BLACK		= 0;
	public static final int							RED			= 1;
	public static final int							GREEN		= 2;
	public static final int							BLUE		= 3;
	public static final int							PURPLE		= 4;
	public static final int							YELLOW		= 5;
	public static final int							WHITE		= 6;
	private static HashMap<Integer, DecayHandler>	handlers	= new HashMap<Integer, DecayHandler>();

	@SideOnly(Side.CLIENT)
	protected IIcon									icon;
	private int										metadata;

	static {
		registerHandler(BLACK, new DecayHandlerBlack());
		registerHandler(RED, new DecayHandlerRed());
		// TODO: (Instability) registerHandler(GREEN, new DecayHandlerGreen());
		registerHandler(BLUE, new DecayHandlerBlue());
		registerHandler(PURPLE, new DecayHandlerPurple());
		// TODO: (Instability) registerHandler(YELLOW, new DecayHandlerYellow());
		registerHandler(WHITE, new DecayHandlerWhite());
	}

	public static int size() {
		return handlers.size();
	}

	public static void registerHandler(int meta, DecayHandler handler) {
		handler.setMetadata(meta);
		handlers.put(meta, handler);
	}

	protected void setMetadata(int meta) {
		this.metadata = meta;
	}

	protected int getMetadata() {
		return this.metadata;
	}

	public static void registerIcons(IIconRegister par1IconRegister) {
		for (DecayHandler handler : handlers.values()) {
			handler.registerIcon(par1IconRegister);
		}
	}

	public static DecayHandler getHandler(int meta) {
		return handlers.get(meta);
	}

	protected void addInstability(World world, int amount) {
		// if (world.provider instanceof WorldProviderMyst) {
		// WorldProviderMyst provider = (WorldProviderMyst) world.provider;
		// provider.agedata.instability += amount;
		// }
	}

	public abstract String getIdentifier();

	@SideOnly(Side.CLIENT)
	public IIcon getBlockTextureFromSide(int side) {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	protected void registerIcon(IIconRegister register) {
		icon = register.registerIcon("mystcraft:decay_" + getIdentifier());
	}

	public void onBlockAdded(World world, int i, int j, int k) {
		if (!world.isRemote) {
			if (world.getBlock(i, j, k) == ModBlocks.decay) {
				if (!WorldInfoUtils.isMystcraftAge(world)) {
					world.setBlock(i, j, k, Blocks.AIR);
					return;
				}
			}
		}
	}

	public void updateTick(World world, int x, int y, int z, Random random) {
		this.pulse(world, x, y, z, random);
	}

	protected abstract void pulse(World world, int i, int j, int k, Random rand);

	public void onBlockDestroyedByPlayer(World world, int i, int j, int k) {}

	public float getExplosionResistance(Entity entity, World worldObj, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		return 2.5F;
	}

	public float getBlockHardness(World worldObj, int x, int y, int z) {
		return 0.5F;
	}

	public void onEntityContact(World worldObj, int x, int y, int z, Entity entity) {}
}
