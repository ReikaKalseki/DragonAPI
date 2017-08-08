/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.TileEntity;

import net.minecraft.nbt.NBTTagCompound;


public interface NBTCopyable {

	public void writeCopyableData(NBTTagCompound NBT);

	public void readCopyableData(NBTTagCompound NBT);

}
