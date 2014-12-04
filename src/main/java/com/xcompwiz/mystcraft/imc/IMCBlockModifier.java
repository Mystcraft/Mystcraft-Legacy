package com.xcompwiz.mystcraft.imc;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

import com.xcompwiz.mystcraft.core.InternalAPI;
import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.nbt.NBTUtils;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;

import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.registry.GameRegistry;

public class IMCBlockModifier implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		if (!message.isNBTMessage()) return;
		NBTTagCompound nbt = message.getNBTValue();
		String blockname = nbt.getString("BlockName");
		Block block = GameRegistry.findBlock(message.getSender(), blockname);
		if (block == null) {
			LoggerUtils.error("Could not find block by name %s belonging to mod [%s] when creating block modifier symbol via IMC message.", blockname, message.getClass());
			return;
		}
		int metadata = 0;
		if (nbt.hasKey("Metadata")) metadata = nbt.getInteger("Metadata");
		String thirdword = nbt.getString("PoemWord");
		if (thirdword == null) {
			LoggerUtils.warn("Poem word not specified for %s belonging to mod [%s] when creating block modifier symbol via IMC message.", blockname, message.getClass());
		}
		int rank = 1;
		if (nbt.hasKey("Rank")) {
			rank = nbt.getInteger("Rank");
		} else {
			LoggerUtils.warn("Item Ranking not specified for %s belonging to mod [%s] when creating block modifier symbol via IMC message.", blockname, message.getClass());
		}

		ArrayList<Object[]> objects = null;
		if (nbt.hasKey("Categories")) {
			ArrayList<NBTTagCompound> list = NBTUtils.readTagCompoundCollection(nbt.getTagList("Categories", Constants.NBT.TAG_COMPOUND), new ArrayList<NBTTagCompound>());
			objects = new ArrayList<Object[]>();
			for (NBTTagCompound cat : list) {
				String catname = cat.getString("Category");
				Integer catrank = cat.getInteger("Rank");
				objects.add(new Object[]{catname, catrank});
			}
		}
		if (objects == null) {
			LoggerUtils.warn("Block categories not specified for %s belonging to mod [%s] when creating block modifier symbol via IMC message.", blockname, message.getClass());
		}

		IAgeSymbol symbol = InternalAPI.symbolFact.createSymbol(block, metadata, thirdword, rank, (objects != null ? objects.toArray() : null));
		if (symbol == null) {
			LoggerUtils.warn("[%s] is attempting to create a block modifier symbol for an already registered block.", message.getSender());
		} else {
			//TODO: MystAPI api = InternalAPI.getAPIInstance(message.getSender());
			//api.getSymbolFactory()
			InternalAPI.symbol.registerSymbol(symbol);
		}
	}

}
