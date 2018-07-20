package com.xcompwiz.mystcraft.client;

import com.xcompwiz.mystcraft.api.util.Color;
import com.xcompwiz.mystcraft.world.WorldProviderMyst;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BlockColorMyst implements IBlockColor {

	private IBlockColor fallback;
	private String colorTypeKey;

	public BlockColorMyst(IBlockColor fallback, String colorTypeKey) {
		this.fallback = fallback;
		this.colorTypeKey = colorTypeKey;
	}

	@Override
	public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
		World world = Minecraft.getMinecraft().world;
		WorldProviderMyst provider = null;
		Color color = null;
		if (world != null && world.provider != null && world.provider instanceof WorldProviderMyst)
			provider = (WorldProviderMyst) world.provider;
		if (provider != null) {
			Biome biome = null;
			if (worldIn != null)
				worldIn.getBiome(pos);
			color = provider.getStaticColor(colorTypeKey, biome, pos);
		}
		if (color != null)
			return color.asInt(); //TODO: support mod biome color event?
		return fallback.colorMultiplier(state, worldIn, pos, tintIndex);
	}
}
