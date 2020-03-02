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

import Reika.DragonAPI.ModInteract.Lua.LibraryLuaMethod;


public class LuaGetWorldTime extends LibraryLuaMethod {

	public LuaGetWorldTime() {
		super("getWorldTime");
	}

	@Override
	protected Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		return new Object[] {te.worldObj.getTotalWorldTime()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the world time.\nReturns: Time";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.LONG;
	}

}
