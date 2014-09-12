/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Lua;

import net.minecraft.tileentity.TileEntity;
import dan200.computercraft.api.lua.LuaException;

public class LuaGetCoords extends LuaMethod {

	public LuaGetCoords() {
		super("getCoords", TileEntity.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{te.xCoord, te.yCoord, te.zCoord};
	}

	@Override
	public String getDocumentation() {
		return "Returns the TileEntity coordinates.\nArgs: None\nReturns: [x,y,z]";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.ARRAY;
	}

}
