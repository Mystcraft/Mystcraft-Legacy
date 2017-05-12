package com.xcompwiz.mystcraft.instability.decay;

import java.util.HashMap;
import java.util.Random;

import com.xcompwiz.mystcraft.block.BlockDecay;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.world.WorldInfoUtils;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public abstract class DecayHandler {
	public static final int							BLACK		= 0;
	public static final int							RED			= 1;
	public static final int							GREEN		= 2;
	public static final int							BLUE		= 3;
	public static final int							PURPLE		= 4;
	public static final int							YELLOW		= 5;
	public static final int							WHITE		= 6;
	private static HashMap<Integer, DecayHandler>	handlers	= new HashMap<Integer, DecayHandler>();

	public enum DecayType implements IStringSerializable {

		BLACK(0, "black"),
		RED(1, "red"),
		GREEN(2, "green"),
		BLUE(3, "blue"),
		PURPLE(4, "purple"),
		YELLOW(5, "yellow"),
		WHITE(6, "white");

		private final int index;
		private final String name;

		private DecayType(int indexIn, String nameIn) {
			this.index = indexIn;
			this.name = nameIn;
		}

		public int getIndex() {
			return this.index;
		}

		@Override
		public String getName() {
			return name;
		}
	}

	private static HashMap<DecayType, DecayHandler> handlers = new HashMap<DecayType, DecayHandler>();

	private DecayType decayType;

	static {
		registerHandler(DecayType.BLACK, new DecayHandlerBlack());
		registerHandler(DecayType.RED, new DecayHandlerRed());
		// TODO: (Instability) registerHandler(GREEN, new DecayHandlerGreen());
		registerHandler(DecayType.BLUE, new DecayHandlerBlue());
		registerHandler(DecayType.PURPLE, new DecayHandlerPurple());
		// TODO: (Instability) registerHandler(YELLOW, new DecayHandlerYellow());
		registerHandler(DecayType.WHITE, new DecayHandlerWhite());
	}

	public static void registerHandler(DecayType type, DecayHandler handler) {
		handler.setDecayType(type);
		handlers.put(type, handler);
	}

	protected void setDecayType(DecayType type) {
		this.decayType = type;
	}

	protected DecayType getDecayType() {
		return this.decayType;
	}

	protected int getMetadata() {
		return this.metadata;
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

	public void onBlockAdded(World world, BlockPos pos) {
		if (!world.isRemote) {
			if (world.getBlockState(pos).getBlock().equals(ModBlocks.decay)) {
				if (!WorldInfoUtils.isMystcraftAge(world)) {
					world.setBlockToAir(pos);
				}
			}
		}
	}

	public void updateTick(World world, BlockPos pos, Random random) {
		this.pulse(world, pos, random);
	}

	protected abstract void pulse(World world, BlockPos pos, Random rand);

	public void onBlockDestroyedByPlayer(World world, BlockPos pos) {}

	public float getExplosionResistance(Entity entity, World worldObj, BlockPos pos, Explosion explosion) {
		return 2.5F;
	}

	public float getBlockHardness(World worldObj, BlockPos pos) {
		return 0.5F;
	}

	public void onEntityContact(World worldObj, BlockPos pos, Entity entity) {}
}
