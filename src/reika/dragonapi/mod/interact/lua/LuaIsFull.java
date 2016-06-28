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
import reika.dragonapi.libraries.ReikaInventoryHelper;
import dan200.computercraft.api.lua.LuaException;

public class LuaIsFull extends LuaMethod {

	public LuaIsFull() {
		super("isFull", IInventory.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		IInventory ii = (IInventory)te;
		return new Object[]{ReikaInventoryHelper.isFull(ii)};
	}

	@Override
	public String getDocumentation() {
		return "Checks if an inventory is completely full.\nArgs: None\nReturns: true/false";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.BOOLEAN;
	}

}
