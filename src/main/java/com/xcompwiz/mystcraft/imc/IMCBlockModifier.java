package com.xcompwiz.mystcraft.imc;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.api.hook.SymbolFactory.CategoryPair;
import com.xcompwiz.mystcraft.api.impl.InternalAPI;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.nbt.NBTUtils;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.registry.GameRegistry;

public class IMCBlockModifier implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		if (!message.isNBTMessage()) return;
		NBTTagCompound nbt = message.getNBTValue();

		Block block = null;
		int metadata = 0;

		if (nbt.hasKey("ItemStack")) {
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("ItemStack"));
			if (!(itemstack.getItem() instanceof ItemBlock)) throw new RuntimeException("Itemstacks references used for setting instability factors must extend ItemBlock");
			block = ((ItemBlock)itemstack.getItem()).field_150939_a;
			metadata = itemstack.getItemDamage();
		}

		if (nbt.hasKey("BlockName")) {
			String blockname = nbt.getString("BlockName");
			block = GameRegistry.findBlock(message.getSender(), blockname);
			if (block == null) {
				LoggerUtils.error("Could not find block by name %s belonging to mod [%s] when creating block modifier symbol via IMC message.", blockname, message.getSender());
				return;
			}
		}

		if (block == null) {
			LoggerUtils.error("No block specified when creating block modifier symbol via IMC message from mod [%s].", message.getSender());
			return;
		}

		if (nbt.hasKey("Metadata")) metadata = NBTUtils.readNumber(nbt.getTag("Metadata")).intValue();

		String thirdword = nbt.getString("PoemWord");
		if (thirdword == null) {
			LoggerUtils.warn("Poem word not specified for %s:%d belonging to mod [%s] when creating block modifier symbol via IMC message.", block.getUnlocalizedName(), metadata, message.getSender());
		}

		int rank = 1;
		if (nbt.hasKey("Rank")) {
			rank = NBTUtils.readNumber(nbt.getTag("Rank")).intValue();
		} else {
			LoggerUtils.warn("Item Ranking not specified for %s:%d belonging to mod [%s] when creating block modifier symbol via IMC message.", block.getUnlocalizedName(), metadata, message.getSender());
		}

		ArrayList<CategoryPair> objects = null;
		if (nbt.hasKey("Categories")) {
			ArrayList<NBTTagCompound> list = NBTUtils.readTagCompoundCollection(nbt.getTagList("Categories", Constants.NBT.TAG_COMPOUND), new ArrayList<NBTTagCompound>());
			objects = new ArrayList<CategoryPair>();
			for (NBTTagCompound cat : list) {
				String catname = cat.getString("Category");
				int catrank = 1;
				if (cat.hasKey("Rank")) catrank = NBTUtils.readNumber(cat.getTag("Rank")).intValue();
				objects.add(new CategoryPair(catname, catrank));
			}
		}
		if (objects == null) {
			LoggerUtils.warn("Block categories not specified for %s:%d belonging to mod [%s] when creating block modifier symbol via IMC message.", block.getUnlocalizedName(), metadata, message.getSender());
		}

		CategoryPair[] args = (objects != null ? objects.toArray(new CategoryPair[] {}) : null);
		IAgeSymbol symbol = InternalAPI.symbolFact.createSymbol(block, metadata, thirdword, rank, args);
		if (symbol == null) {
			LoggerUtils.warn("[%s] is attempting to create a block modifier symbol for an already registered block.", message.getSender());
		} else {
			InternalAPI.symbol.registerSymbol(symbol, message.getSender()); //TODO: Replace with using the mod's own API instances
		}
	}

}
