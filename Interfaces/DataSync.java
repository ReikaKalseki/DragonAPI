/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface DataSync {

	public void setData(TileEntity te, boolean force, NBTTagCompound NBT);

	@SideOnly(Side.CLIENT)
	public void readForSync(TileEntity te, NBTTagCompound NBT);

	public boolean hasNoData();

}
