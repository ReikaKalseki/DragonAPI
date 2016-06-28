/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.lua;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import dan200.computercraft.api.lua.LuaException;

public class LuaIsTankFull extends LuaMethod {

	public LuaIsTankFull() {
		super("isTankFull", IFluidHandler.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
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
