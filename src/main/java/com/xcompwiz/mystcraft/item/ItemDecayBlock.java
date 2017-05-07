package com.xcompwiz.mystcraft.item;

import com.xcompwiz.mystcraft.instability.decay.DecayHandler;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDecayBlock extends ItemBlock {
	/** Instance of the Blocks. */
	private Block	theBlock;

	public ItemDecayBlock(Block block) {
		super(block);
		this.theBlock = block;
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Gets an icon index based on an item's damage value
	 */
	public IIcon getIconFromDamage(int par1) {
		return this.theBlock.getIcon(2, par1);
	}

	/**
	 * Returns the metadata of the block which this Item (ItemBlock) can place
	 */
	@Override
	public int getMetadata(int par1) {
		return par1;
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		int meta = par1ItemStack.getItemDamage();
		DecayHandler handler = DecayHandler.getHandler(meta);
		if (handler == null) return super.getUnlocalizedName() + "." + "unknown";
		return super.getUnlocalizedName() + "." + handler.getIdentifier();
	}
}
