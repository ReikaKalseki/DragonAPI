package Reika.DragonAPI.Interfaces;

import net.minecraft.nbt.NBTTagCompound;

public interface ObjectToNBTSerializer<V> {

	public NBTTagCompound save(V obj);
	public V construct(NBTTagCompound tag);

}
