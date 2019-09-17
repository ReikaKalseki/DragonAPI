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

public class LuaIsTankFull extends LuaMethod {

	public LuaIsTankFull() {
		super("isTankFull", IFluidHandler.class);
	}

	@Override
	protected Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		IFluidHandler ifl = (IFluidHandler)te;
		int ordinal = ((Double)args[0]).intValue();
		FluidTankInfo info = ifl.getTankInfo(ForgeDirection.UP)[ordinal];
		if (info.fluid == null)
			return new Object[]{false};
		return new Object[]{info.fluid.amount >= info.capacity};
	}

	@Override
	public String getDocumentation() {
		return "Checks if a tank is full.\nArgs: Tank Index\nReturns: true/false";
	}

	@Override
	public String getArgsAsString() {
		return "int tankIndex";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.BOOLEAN;
	}

}
