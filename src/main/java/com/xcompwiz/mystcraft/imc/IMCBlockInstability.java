package com.xcompwiz.mystcraft.imc;

import com.xcompwiz.mystcraft.imc.IMCHandler.IMCProcessor;
import com.xcompwiz.mystcraft.instability.InstabilityBlockManager;
import com.xcompwiz.mystcraft.logging.LoggerUtils;
import com.xcompwiz.mystcraft.nbt.NBTUtils;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class IMCBlockInstability implements IMCProcessor {

	@Override
	public void process(IMCMessage message) {
		if (!message.isNBTMessage()) throw new RuntimeException("Message type must be NBT");
		NBTTagCompound nbt = message.getNBTValue();
		Block block = null;
		int metadata = 0;

		if (nbt.hasKey("ItemStack")) {
			ItemStack itemstack = new ItemStack(nbt.getCompoundTag("ItemStack"));
			if (!(itemstack.getItem() instanceof ItemBlock)) throw new RuntimeException("Itemstacks references used for setting instability factors must extend ItemBlock");
			block = ((ItemBlock)itemstack.getItem()).block;
			metadata = itemstack.getMetadata();
		}

		if (nbt.hasKey("BlockName")) {
			String blockname = nbt.getString("BlockName");
			block = Block.REGISTRY.getObject(new ResourceLocation(message.getSender(), blockname)); //XXX: Verify
			if (block == null || block == Blocks.AIR) {
				LoggerUtils.error("Could not find block by name %s belonging to mod [%s] when setting instability factors via IMC message.", blockname, message.getSender());
				return;
			}
		}

		if (block == null) {
			LoggerUtils.error("No block specified when setting instability factors via IMC message from mod [%s].", message.getSender());
			return;
		}

		if (nbt.hasKey("Metadata")) metadata = NBTUtils.readNumber(nbt.getTag("Metadata")).intValue();

		InstabilityBlockManager.setInstabilityFactors(block.getStateFromMeta(metadata), nbt.getFloat("Accessibility"), nbt.getFloat("Flat"));
	}
}
