package com.xcompwiz.mystcraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.ChunkPrimer;

public class StateBasedChunkPrimer extends ChunkPrimer {

    private static final IBlockState DEFAULT_STATE = Blocks.AIR.getDefaultState();
    private final IBlockState[] data;

    public StateBasedChunkPrimer(IBlockState[] data) {
        this.data = data;
    }

    public static StateBasedChunkPrimer intoData(IBlockState[] states) {
        if(states.length != 65536) {
            throw new IllegalArgumentException("Tried instantiating StateBasedPrimer with an invalid state length. actual: " + states.length + " / expected: 65536");
        }
        return new StateBasedChunkPrimer(states);
    }

    @Override
    public IBlockState getBlockState(int x, int y, int z) {
        IBlockState iblockstate = this.data[getBlockIndexVanilla(x, y, z)];
        return iblockstate == null ? DEFAULT_STATE : iblockstate;
    }

    @Override
    public void setBlockState(int x, int y, int z, IBlockState state) {
        this.data[getBlockIndexVanilla(x, y, z)] = state;
    }

    private static int getBlockIndexVanilla(int x, int y, int z) {
        return x << 12 | z << 8 | y;
    }

    public IBlockState[] getData() {
        return data;
    }

    @Override
    public int findGroundBlockIdx(int x, int z) {
        int i = (x << 12 | z << 8) + 256 - 1;
        for (int j = 255; j >= 0; --j) {
            IBlockState iblockstate = this.data[i + j];
            if (iblockstate != null && iblockstate != DEFAULT_STATE) {
                return j;
            }
        }
        return 0;
    }

}
