package Reika.DragonAPI.Interfaces;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public interface Location {

	public void writeToTag(NBTTagCompound data);

	public NBTTagCompound writeToTag();

	public void writeToNBT(String tag, NBTTagCompound NBT);

	public double getDistanceTo(double x, double y, double z);

	public Block getBlock(IBlockAccess world);

	public int getBlockMetadata(IBlockAccess world);

	public TileEntity getTileEntity(IBlockAccess world);
}
