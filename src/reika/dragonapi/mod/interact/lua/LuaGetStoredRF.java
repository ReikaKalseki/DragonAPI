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

public class LuaGetStoredRF extends LuaMethod {

	public LuaGetStoredRF() {
		super("getStoredRF", IEnergyReceiver.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((IEnergyReceiver)te).getEnergyStored(ForgeDirection.valueOf(((String)args[0]).toUpperCase()))};
	}

	@Override
	public String getDocumentation() {
		return "Returns the stored RF value.\nArgs: Side (compass)\nReturns: Energy";
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
