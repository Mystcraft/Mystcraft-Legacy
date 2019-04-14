package com.xcompwiz.mystcraft.api.world.logic;

import net.minecraft.block.state.IBlockState;

import javax.annotation.Nullable;

/**
 * An interface to filter blockstates from pushed into the chunk primer
 * @author HellFirePvP
 */
public interface IPrimerFilter {

    /**
     * Check to prevent a given blockstate from being set into the chunk.
     *
     * @param x [0-15] The x coordinate in the chunk
     * @param y [0-255] The y coordinate in the chunk
     * @param z [0-15] The z coordinate in the chunk
     * @param state the blockstate to add
     *
     * @return The state to set or NULL to filter and prevent setting the suggested blockstate.
     */
    @Nullable
    public IBlockState filter(int x, int y, int z, IBlockState state);

}
