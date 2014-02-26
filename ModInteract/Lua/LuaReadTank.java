/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Lua;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class LuaReadTank extends LuaMethod {

	public LuaReadTank() {
		super("readTank", IFluidHandler.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws Exception {
		int ordinal = ((Double)args[0]).intValue();
		IFluidHandler ifl = (IFluidHandler)te;
		Object[] o = new Object[3];
		FluidTankInfo info = ifl.getTankInfo(ForgeDirection.UP)[0];
		o[0] = info.fluid.getFluid().getLocalizedName();
		o[1] = info.fluid.amount;
		o[2] = info.capacity;
		return o;
	}

}
