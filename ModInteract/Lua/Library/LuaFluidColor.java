/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Lua.Library;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.ModInteract.Lua.LibraryLuaMethod;

import dan200.computercraft.api.lua.LuaException;


public class LuaFluidColor extends LibraryLuaMethod {

	public LuaFluidColor() {
		super("fluidColor");
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		String name = (String)args[0];
		Fluid f = FluidRegistry.getFluid(name);
		if (f == null)
			throw new IllegalArgumentException("No such fluid with name '"+name+"'.");
		return new Object[]{ReikaFluidHelper.getFluidColor(f)};
	}

	@Override
	public String getDocumentation() {
		return "Returns fluid color.\nArgs: fluidName\nReturns: Fluid color";
	}

	@Override
	public String getArgsAsString() {
		return "String fluidName";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.INTEGER;
	}

}
