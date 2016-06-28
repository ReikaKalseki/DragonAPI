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
import cofh.api.energy.IEnergyReceiver;
import dan200.computercraft.api.lua.LuaException;

public class LuaGetRFCapacity extends LuaMethod {

	public LuaGetRFCapacity() {
		super("getMaxStoredRF", IEnergyReceiver.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((IEnergyReceiver)te).getMaxEnergyStored(ForgeDirection.valueOf(((String)args[0]).toUpperCase()))};
	}

	@Override
	public String getDocumentation() {
		return "Returns the RF capacity.\nArgs: Side (compass)\nReturns: Capacity";
	}

	@Override
	public String getArgsAsString() {
		return "String dir";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.INTEGER;
	}

}
