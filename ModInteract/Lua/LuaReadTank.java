/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Lua;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class LuaReadTank extends LuaMethod {

	public LuaReadTank() {
		super("readTank", IFluidHandler.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		int ordinal = ((Double)args[0]).intValue();
		IFluidHandler ifl = (IFluidHandler)te;
		FluidTankInfo info = ifl.getTankInfo(ForgeDirection.UP)[ordinal];
		if (info.fluid == null)
			return new Object[]{null, 0, info.capacity};
		Object[] o = new Object[4];
		o[0] = info.fluid.getFluid().getLocalizedName(info.fluid);
		o[1] = info.fluid.amount;
		o[2] = info.capacity;
		o[3] = info.fluid.getFluid().getName();
		return o;
	}

	@Override
	public String getDocumentation() {
		return "Returns the contents of an fluid tank.\nArgs: Tank Index\nReturns: [Fluid, Amount, Capacity, Internal Name]";
	}

	@Override
	public String getArgsAsString() {
		return "int tankIndex";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.ARRAY;
	}

}
