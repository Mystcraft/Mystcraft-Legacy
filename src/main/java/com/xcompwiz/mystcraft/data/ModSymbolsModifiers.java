package com.xcompwiz.mystcraft.data;

import com.xcompwiz.mystcraft.api.MystObjects;
import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.word.WordData;
import com.xcompwiz.mystcraft.grammar.GrammarGenerator.Rule;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.symbol.SymbolBase;
import com.xcompwiz.mystcraft.symbol.SymbolManager;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolBlock;
import com.xcompwiz.mystcraft.symbol.modifiers.SymbolColor;
import com.xcompwiz.util.CollectionUtils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class ModSymbolsModifiers {

	public static class BlockModifierContainerObject {
		private BlockDescriptor descriptor;
		private SymbolBlock symbol;

		private BlockModifierContainerObject(BlockDescriptor descriptor, SymbolBlock symbol) {
			this.descriptor = descriptor;
			this.symbol = symbol;
		}

		private BlockModifierContainerObject() {}

		public BlockModifierContainerObject add(BlockCategory cat, Integer rank) {
			if (descriptor == null || symbol == null)
				return this;
			if (!descriptor.isUsable(cat)) {
				descriptor.setUsable(cat, true);
				this.symbol.addRule(new Rule(cat.getGrammarBinding(), CollectionUtils.buildList(symbol.getRegistryName()), rank));
			}
			return this;
		}

		public static BlockModifierContainerObject create(String word, int cardrank, IBlockState blockstate) {
			BlockDescriptor descriptor = new BlockDescriptor(blockstate);
			SymbolBlock symbol = new SymbolBlock(descriptor, word);
			if (SymbolManager.hasBinding(symbol.getRegistryName())) {
				LoggerUtils.info("Some Mod is attempting to register a block symbol over an existing registration.");
				return new BlockModifierContainerObject();
			}
			symbol.setCardRank(cardrank);
			return new BlockModifierContainerObject(descriptor, symbol);
		}

		private static BlockModifierContainerObject createMyst(String word, int cardrank, Block block, int metadata) {
			IBlockState state = block.getStateFromMeta(metadata);
			if (state == null)
				return null;
			return BlockModifierContainerObject.create(word, cardrank, state);
		}

		public IAgeSymbol getSymbol() {
			return symbol;
		}

		private BlockModifierContainerObject register() {
			if (symbol != null) {
				SymbolManager.tryAddSymbol(symbol);
			}
			return this;
		}
	}

	public static void initialize() {
		BlockModifierContainerObject.createMyst(WordData.Terrain, 2, Blocks.DIRT, 0).register().add(BlockCategory.TERRAIN, 4).add(BlockCategory.STRUCTURE, 2).add(BlockCategory.SOLID, 1);
		BlockModifierContainerObject.createMyst(WordData.Terrain, 2, Blocks.STONE, 0).register().add(BlockCategory.TERRAIN, 1).add(BlockCategory.STRUCTURE, 2).add(BlockCategory.SOLID, 1);
		for (byte i = 1; i < 7; i += 2)
			BlockModifierContainerObject.createMyst(WordData.Terrain, 2, Blocks.STONE, i).register().add(BlockCategory.TERRAIN, 2).add(BlockCategory.STRUCTURE, 2).add(BlockCategory.SOLID, 1);
		for (byte i = 2; i < 7; i += 2)
			BlockModifierContainerObject.createMyst(WordData.Structure, 2, Blocks.STONE, i).register().add(BlockCategory.TERRAIN, 5).add(BlockCategory.STRUCTURE, 1).add(BlockCategory.SOLID, 1);
		BlockModifierContainerObject.createMyst(WordData.Terrain, 2, Blocks.SANDSTONE, 0).register().add(BlockCategory.TERRAIN, 2).add(BlockCategory.STRUCTURE, 1).add(BlockCategory.SOLID, 1);
		BlockModifierContainerObject.createMyst(WordData.Terrain, 2, Blocks.NETHERRACK, 0).register().add(BlockCategory.TERRAIN, 3).add(BlockCategory.STRUCTURE, 2).add(BlockCategory.SOLID, 2);
		BlockModifierContainerObject.createMyst(WordData.Terrain, 3, Blocks.END_STONE, 0).register().add(BlockCategory.TERRAIN, 4).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.SOLID, 3);

		BlockModifierContainerObject.createMyst(WordData.Structure, 2, Blocks.NETHER_BRICK, 0).register().add(BlockCategory.SOLID, 2).add(BlockCategory.STRUCTURE, 2);
		for (byte i = 0; i < 4; ++i) {
			BlockModifierContainerObject.createMyst(WordData.Structure, 2, Blocks.LOG, i).register().add(BlockCategory.SOLID, 1).add(BlockCategory.ORGANIC, 1).add(BlockCategory.STRUCTURE, 1);
		}
		for (byte i = 0; i < 2; ++i) {
			BlockModifierContainerObject.createMyst(WordData.Structure, 2, Blocks.LOG2, i).register().add(BlockCategory.SOLID, 1).add(BlockCategory.ORGANIC, 1).add(BlockCategory.STRUCTURE, 1);
		}

		BlockModifierContainerObject.createMyst(WordData.Ore, 5, Blocks.DIAMOND_ORE, 0).register().add(BlockCategory.SOLID, 6).add(BlockCategory.STRUCTURE, 6);
		BlockModifierContainerObject.createMyst(WordData.Ore, 4, Blocks.GOLD_ORE, 0).register().add(BlockCategory.SOLID, 5).add(BlockCategory.STRUCTURE, 5);
		BlockModifierContainerObject.createMyst(WordData.Ore, 3, Blocks.IRON_ORE, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4);
		BlockModifierContainerObject.createMyst(WordData.Ore, 3, Blocks.COAL_ORE, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4);
		BlockModifierContainerObject.createMyst(WordData.Ore, 4, Blocks.REDSTONE_ORE, 0).register().add(BlockCategory.SOLID, 5).add(BlockCategory.STRUCTURE, 5);
		BlockModifierContainerObject.createMyst(WordData.Ore, 3, Blocks.LAPIS_ORE, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4);
		BlockModifierContainerObject.createMyst(WordData.Ore, 4, Blocks.EMERALD_ORE, 0).register().add(BlockCategory.SOLID, 5).add(BlockCategory.STRUCTURE, 5);

		BlockModifierContainerObject.createMyst(WordData.Chain, 2, Blocks.ICE, 0).register().add(BlockCategory.SOLID, 3).add(BlockCategory.FLUID, 3).add(BlockCategory.SEA, 2).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.createMyst(WordData.Chain, 2, Blocks.PACKED_ICE, 0).register().add(BlockCategory.SOLID, 3).add(BlockCategory.FLUID, 3).add(BlockCategory.TERRAIN, 3).add(BlockCategory.SEA, 3).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.createMyst(WordData.Chain, 2, Blocks.GLASS, 0).register().add(BlockCategory.SOLID, 3).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.createMyst(WordData.Chain, 2, Blocks.SNOW, 0).register().add(BlockCategory.SOLID, 3).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.createMyst(WordData.Chain, 3, Blocks.OBSIDIAN, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.TERRAIN, 4).add(BlockCategory.STRUCTURE, 3).add(BlockCategory.CRYSTAL, 3);
		BlockModifierContainerObject.createMyst(WordData.Chain, 3, Blocks.GLOWSTONE, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4).add(BlockCategory.CRYSTAL, 4);
		BlockModifierContainerObject.createMyst(WordData.Chain, 3, Blocks.QUARTZ_ORE, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4).add(BlockCategory.CRYSTAL, 4);

		BlockModifierContainerObject.createMyst(WordData.Chain, 3, ModBlocks.crystal, 0).register().add(BlockCategory.SOLID, 4).add(BlockCategory.STRUCTURE, 4).add(BlockCategory.CRYSTAL, 4);

		BlockModifierContainerObject.createMyst(WordData.Sea, 2, Blocks.FLOWING_WATER, 0).register().add(BlockCategory.FLUID, 1).add(BlockCategory.SEA, 1);
		BlockModifierContainerObject.createMyst(WordData.Sea, 3, Blocks.FLOWING_LAVA, 0).register().add(BlockCategory.FLUID, 2).add(BlockCategory.SEA, 2);

		// color
		registerSymbol((new SymbolColor(0.50F, 0.00F, 0.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorMaroon"))));
		registerSymbol((new SymbolColor(1.00F, 0.00F, 0.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorRed"))));
		registerSymbol((new SymbolColor(0.50F, 0.50F, 0.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorOlive"))));
		registerSymbol((new SymbolColor(1.00F, 1.00F, 0.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorYellow"))));
		registerSymbol((new SymbolColor(0.00F, 0.50F, 0.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorDarkGreen"))));
		registerSymbol((new SymbolColor(0.00F, 1.00F, 0.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorGreen"))));
		registerSymbol((new SymbolColor(0.00F, 0.50F, 0.50F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorTeal"))));
		registerSymbol((new SymbolColor(0.00F, 1.00F, 1.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorCyan"))));
		registerSymbol((new SymbolColor(0.00F, 0.00F, 0.50F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorNavy"))));
		registerSymbol((new SymbolColor(0.00F, 0.00F, 1.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorBlue"))));
		registerSymbol((new SymbolColor(0.50F, 0.00F, 0.50F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorPurple"))));
		registerSymbol((new SymbolColor(1.00F, 0.00F, 1.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorMagenta"))));
		registerSymbol((new SymbolColor(0.00F, 0.00F, 0.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorBlack"))));
		registerSymbol((new SymbolColor(0.50F, 0.50F, 0.50F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorGrey"))));
		registerSymbol((new SymbolColor(0.75F, 0.75F, 0.75F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorSilver"))));
		registerSymbol((new SymbolColor(1.00F, 1.00F, 1.00F, new ResourceLocation(MystObjects.MystcraftModId, "ModColorWhite"))));
	}

	private static void registerSymbol(SymbolBase symbol) {
		SymbolManager.tryAddSymbol(symbol);
	}
}
