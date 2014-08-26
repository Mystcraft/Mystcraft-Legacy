package com.xcompwiz.mystcraft.data;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.block.BlockCrystal;
import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierAngle;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierBlock;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierClear;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierColor;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierGradient;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierLength;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierNoSea;
import com.xcompwiz.mystcraft.symbol.modifiers.ModifierPhase;
import com.xcompwiz.mystcraft.world.ChunkProfiler;
import com.xcompwiz.util.CollectionUtils;

public class SymbolDataModifiers {

	public static class BlockModifierContainerObject {
		private BlockDescriptor	descriptor;
		private ModifierBlock	symbol;

		private BlockModifierContainerObject(BlockDescriptor descriptor, ModifierBlock symbol) {
			this.descriptor = descriptor;
			this.symbol = symbol;
		}

		private BlockModifierContainerObject() {
		}

		public BlockModifierContainerObject add(BlockCategory cat, float grammarweight) {
			if (descriptor == null || symbol == null) return this;
			if (!descriptor.isUsable(cat)) {
				descriptor.setUsable(cat, true);
				this.symbol.addRule(new Rule(cat.getGrammarBinding(), CollectionUtils.buildList(symbol.identifier()), grammarweight));
			}
			return this;
		}

		public static BlockModifierContainerObject create(String word, float itemrarity, Block block, byte metadata) {
			BlockDescriptor descriptor = new BlockDescriptor(block, metadata);
			ModifierBlock symbol = new ModifierBlock(descriptor, word);
			if (SymbolManager.hasBinding(symbol.identifier())) return new BlockModifierContainerObject();
			InternalAPI.symbol.registerSymbol(symbol);
			return new BlockModifierContainerObject(descriptor, symbol);
		}

		public static BlockModifierContainerObject create(String word, float itemrarity, Block block, int metadata) {
			return BlockModifierContainerObject.create(word, itemrarity, block, (byte) metadata);
		}
	}

	public static void initialize() {
		BlockModifierContainerObject.create(WordData.Terrain, 0.50F, Blocks.dirt, 0).add(BlockCategory.TERRAIN, 0.10F).add(BlockCategory.STRUCTURE, 1.00F).add(BlockCategory.SOLID, 1.00F);
		BlockModifierContainerObject.create(WordData.Terrain, 0.62F, Blocks.stone, 0).add(BlockCategory.TERRAIN, 1.00F).add(BlockCategory.STRUCTURE, 1.00F).add(BlockCategory.SOLID, 1.00F);
		BlockModifierContainerObject.create(WordData.Terrain, 0.18F, Blocks.end_stone, 0).add(BlockCategory.TERRAIN, 0.40F).add(BlockCategory.STRUCTURE, 0.40F).add(BlockCategory.SOLID, 0.40F);
		BlockModifierContainerObject.create(WordData.Terrain, 0.32F, Blocks.netherrack, 0).add(BlockCategory.TERRAIN, 0.80F).add(BlockCategory.STRUCTURE, 0.80F).add(BlockCategory.SOLID, 0.80F);

		BlockModifierContainerObject.create(WordData.Structure, 0.32F, Blocks.nether_brick, 0).add(BlockCategory.SOLID, 0.50F).add(BlockCategory.STRUCTURE, 0.50F);
		for (byte i = 0; i < 4; ++i) {
			BlockModifierContainerObject.create(WordData.Structure, 0.62F, Blocks.log, i).add(BlockCategory.SOLID, 1.00F).add(BlockCategory.ORGANIC, 1.00F).add(BlockCategory.STRUCTURE, 1.00F);
		}
		for (byte i = 0; i < 2; ++i) {
			BlockModifierContainerObject.create(WordData.Structure, 0.62F, Blocks.log2, i).add(BlockCategory.SOLID, 1.00F).add(BlockCategory.ORGANIC, 1.00F).add(BlockCategory.STRUCTURE, 1.00F);
		}

		BlockModifierContainerObject.create(WordData.Ore, 0.01F, Blocks.diamond_ore, 0).add(BlockCategory.SOLID, 0.10F).add(BlockCategory.STRUCTURE, 0.10F);
		BlockModifierContainerObject.create(WordData.Ore, 0.10F, Blocks.gold_ore, 0).add(BlockCategory.SOLID, 0.30F).add(BlockCategory.STRUCTURE, 0.30F);
		BlockModifierContainerObject.create(WordData.Ore, 0.15F, Blocks.iron_ore, 0).add(BlockCategory.SOLID, 0.30F).add(BlockCategory.STRUCTURE, 0.30F);
		BlockModifierContainerObject.create(WordData.Ore, 0.25F, Blocks.coal_ore, 0).add(BlockCategory.SOLID, 0.30F).add(BlockCategory.STRUCTURE, 0.30F);
		BlockModifierContainerObject.create(WordData.Ore, 0.25F, Blocks.redstone_ore, 0).add(BlockCategory.SOLID, 0.30F).add(BlockCategory.STRUCTURE, 0.30F);

		BlockModifierContainerObject.create(WordData.Chain, 0.42F, Blocks.ice, 0).add(BlockCategory.SOLID, 0.60F).add(BlockCategory.FLUID, 0.40F).add(BlockCategory.SEA, 0.40F).add(BlockCategory.STRUCTURE, 0.40F).add(BlockCategory.CRYSTAL, 0.40F);
		BlockModifierContainerObject.create(WordData.Chain, 0.42F, Blocks.packed_ice, 0).add(BlockCategory.SOLID, 0.60F).add(BlockCategory.FLUID, 0.40F).add(BlockCategory.TERRAIN, 0.40F).add(BlockCategory.SEA, 0.40F).add(BlockCategory.STRUCTURE, 0.40F).add(BlockCategory.CRYSTAL, 0.40F);
		BlockModifierContainerObject.create(WordData.Chain, 0.42F, Blocks.glass, 0).add(BlockCategory.SOLID, 0.60F).add(BlockCategory.STRUCTURE, 0.60F).add(BlockCategory.CRYSTAL, 0.60F);
		BlockModifierContainerObject.create(WordData.Chain, 0.62F, Blocks.snow, 0).add(BlockCategory.SOLID, 1.00F).add(BlockCategory.STRUCTURE, 1.00F).add(BlockCategory.CRYSTAL, 1.00F);
		BlockModifierContainerObject.create(WordData.Chain, 0.50F, Blocks.obsidian, 0).add(BlockCategory.SOLID, 0.80F).add(BlockCategory.TERRAIN, 0.20F).add(BlockCategory.STRUCTURE, 0.80F).add(BlockCategory.CRYSTAL, 0.80F);
		BlockModifierContainerObject.create(WordData.Chain, 0.62F, BlockCrystal.instance, 0).add(BlockCategory.SOLID, 1.00F).add(BlockCategory.STRUCTURE, 1.00F).add(BlockCategory.CRYSTAL, 1.00F);
		BlockModifierContainerObject.create(WordData.Chain, 0.42F, Blocks.glowstone, 0).add(BlockCategory.SOLID, 0.60F).add(BlockCategory.STRUCTURE, 0.60F).add(BlockCategory.CRYSTAL, 0.60F);
		BlockModifierContainerObject.create(WordData.Chain, 0.62F, Blocks.quartz_ore, 0).add(BlockCategory.SOLID, 1.00F).add(BlockCategory.STRUCTURE, 1.00F).add(BlockCategory.CRYSTAL, 1.00F);

		//XXX: Move these to own data class
		ChunkProfiler.setInstabilityFactors(Blocks.coal_ore, 10, 2, 200);
		ChunkProfiler.setInstabilityFactors(Blocks.iron_ore, 30, 6, 300);
		ChunkProfiler.setInstabilityFactors(Blocks.redstone_ore, 150, 20, 400);
		ChunkProfiler.setInstabilityFactors(Blocks.gold_ore, 400, 80, 500);
		ChunkProfiler.setInstabilityFactors(Blocks.diamond_ore, 4000, 200, 1000);

		ChunkProfiler.setInstabilityFactors(BlockCrystal.instance, 20, 4, 0);
		ChunkProfiler.setInstabilityFactors(Blocks.glowstone, 50, 4, 0);
		ChunkProfiler.setInstabilityFactors(Blocks.quartz_ore, 20, 4, 0);

		BlockModifierContainerObject.create(WordData.Sea, 0.62F, Blocks.flowing_water, 0).add(BlockCategory.FLUID, 1.00F).add(BlockCategory.SEA, 1.00F);
		BlockModifierContainerObject.create(WordData.Sea, 0.28F, Blocks.flowing_lava, 0).add(BlockCategory.FLUID, 0.50F).add(BlockCategory.SEA, 0.50F);

		InternalAPI.symbol.registerSymbol((new ModifierNoSea()));

		InternalAPI.symbol.registerSymbol((new ModifierClear()));
		// wavelength
		InternalAPI.symbol.registerSymbol((new ModifierLength(0.0F, "ModZero", "Zero Length")));
		InternalAPI.symbol.registerSymbol((new ModifierLength(0.5F, "ModHalf", "Half Length")));
		InternalAPI.symbol.registerSymbol((new ModifierLength(1.0F, "ModFull", "Full Length")));
		InternalAPI.symbol.registerSymbol((new ModifierLength(2.0F, "ModDouble", "Double Length")));
		// phase
		InternalAPI.symbol.registerSymbol((new ModifierPhase(000F, "ModEnd", "Nadir")));
		InternalAPI.symbol.registerSymbol((new ModifierPhase(090F, "ModRising", "Rising")));
		InternalAPI.symbol.registerSymbol((new ModifierPhase(180F, "ModNoon", "Zenith")));
		InternalAPI.symbol.registerSymbol((new ModifierPhase(270F, "ModSetting", "Setting")));
		// angle
		InternalAPI.symbol.registerSymbol((new ModifierAngle(000.0F, "ModNorth", "North")));
		InternalAPI.symbol.registerSymbol((new ModifierAngle(090.0F, "ModEast", "East")));
		InternalAPI.symbol.registerSymbol((new ModifierAngle(180.0F, "ModSouth", "South")));
		InternalAPI.symbol.registerSymbol((new ModifierAngle(270.0F, "ModWest", "West")));
		// color
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.50F, 0.00F, 0.00F, "Maroon")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(1.00F, 0.00F, 0.00F, "Red")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.50F, 0.50F, 0.00F, "Olive")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(1.00F, 1.00F, 0.00F, "Yellow")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.50F, 0.00F, "Dark Green")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 1.00F, 0.00F, "Green")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.50F, 0.50F, "Teal")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 1.00F, 1.00F, "Cyan")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.00F, 0.50F, "Navy")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.00F, 1.00F, "Blue")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.50F, 0.00F, 0.50F, "Purple")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(1.00F, 0.00F, 1.00F, "Magenta")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.00F, 0.00F, 0.00F, "Black")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.50F, 0.50F, 0.50F, "Grey")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(0.75F, 0.75F, 0.75F, "Silver")));
		InternalAPI.symbol.registerSymbol((new ModifierColor(1.00F, 1.00F, 1.00F, "White")));

		InternalAPI.symbol.registerSymbol((new ModifierGradient()));
	}
}
