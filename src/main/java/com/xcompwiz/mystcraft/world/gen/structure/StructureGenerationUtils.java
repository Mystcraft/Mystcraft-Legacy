package com.xcompwiz.mystcraft.world.gen.structure;

import java.util.Random;

import com.xcompwiz.mystcraft.block.BlockLectern;
import com.xcompwiz.mystcraft.data.ModBlocks;
import com.xcompwiz.mystcraft.tileentity.TileEntityLectern;
import com.xcompwiz.mystcraft.treasure.LootTableHandler;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public class StructureGenerationUtils {

	public static boolean generateLectern(World worldObj, StructureBoundingBox boundingbox, Random rand, BlockPos pos, BlockPos lookPos, LootTable lootTable, LootContext lootContext) {
		if (boundingbox.isVecInside(pos)) {
			EnumFacing horizontal = EnumFacing.fromAngle(360 - getRotation(pos.getX(), pos.getZ(), lookPos.getX(), lookPos.getZ()) + 90); //todo Hellfire> check rotation of lecterns in villages
			worldObj.setBlockState(pos, ModBlocks.lectern.getDefaultState().withProperty(BlockLectern.ROTATION, horizontal));
			TileEntityLectern lectern = (TileEntityLectern) worldObj.getTileEntity(pos);

			if (lectern != null) {
				lectern.setYaw(360 - getRotation(pos.getX(), pos.getZ(), lookPos.getX(), lookPos.getZ()) + 90);
				ItemStack item = LootTableHandler.generateLecternItem(lectern, rand, lootTable, lootContext);
				if (item != null)
					lectern.setBook(item);
			}

			return true;
		}
		return false;
	}
	
	public static int getRotation(int x1, int y1, int x2, int y2) {
		int deltaX = x2 - x1;
		int deltaZ = -(y2 - y1);
		if (deltaZ == 0)
			return (deltaX < 0 ? 180 : 0);
		if (deltaX == 0)
			return (deltaZ < 0 ? 270 : 90);
		float f = (float) deltaZ / (float) deltaX;
		return (int) (Math.atan(f) * 180 / Math.PI);
	}
}
