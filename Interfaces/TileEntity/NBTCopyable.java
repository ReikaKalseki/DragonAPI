package Reika.DragonAPI.Interfaces.TileEntity;

import net.minecraft.nbt.NBTTagCompound;


public interface NBTCopyable {

	public void writeCopyableData(NBTTagCompound NBT);

	public void readCopyableData(NBTTagCompound NBT);

}
