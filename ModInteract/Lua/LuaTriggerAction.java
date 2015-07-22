/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Lua;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Interfaces.TileEntity.TriggerableAction;
import dan200.computercraft.api.lua.LuaException;

public class LuaTriggerAction extends LuaMethod {

	public LuaTriggerAction() {
		super("trigger", TriggerableAction.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		return new Object[]{((TriggerableAction)te).trigger()};
	}

	@Override
	public String getDocumentation() {
		return "Triggers the block to attempt to perform its action.\nArgs: None\nReturns: Success true/false";
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
