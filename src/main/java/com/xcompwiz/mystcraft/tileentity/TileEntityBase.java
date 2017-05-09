package com.xcompwiz.mystcraft.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;


public abstract class TileEntityBase extends TileEntity {

    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readCustomNBT(compound);
    }

    //Overwrite to read NBT data from save/on client
    public void readCustomNBT(NBTTagCompound compound) {}

    //Overwrite to read data that was specifically only intended for network sync via writeNetNBT
    public void readNetNBT(NBTTagCompound compound) {}

    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        writeCustomNBT(compound);
        return compound;
    }

    //Overwrite to write NBT to the NBT tag for save or sending on client
    public void writeCustomNBT(NBTTagCompound compound) {}

    //Overwrite to write data that is only sent to client, not write to chunk data. read that data with readNetNBT
    public void writeNetNBT(NBTTagCompound compound) {}

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound compound = new NBTTagCompound();
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        writeNetNBT(compound);
        return new SPacketUpdateTileEntity(getPos(), 255, compound);
    }

    @Override
    @Nonnull
    public final NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = new NBTTagCompound();
        super.writeToNBT(compound);
        writeCustomNBT(compound);
        return compound;
    }

    public final void onDataPacket(NetworkManager manager, SPacketUpdateTileEntity packet) {
        super.onDataPacket(manager, packet);
        readCustomNBT(packet.getNbtCompound());
        readNetNBT(packet.getNbtCompound());
    }

    //Use this instead of markDirty. markDirty alone doesn't cause a sync of data to client.
    public final void markForUpdate() {
        IBlockState thisState = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, thisState, thisState, 3);
        markDirty();
    }

}
