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

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import dan200.computercraft.api.lua.LuaException;

public class LuaInvSize extends LuaMethod {

	public LuaInvSize() {
		super("getSizeInv", IInventory.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((IInventory)te).getSizeInventory()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the inventory size.\nArgs: None\nReturns: Inventory Size";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.INTEGER;
	}

}
