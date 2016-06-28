/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.lua.library;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import reika.dragonapi.mod.interact.lua.LibraryLuaMethod;
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
		return new Object[]{f.getColor()};
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
