/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Lua;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.Base.TileEntityBase;


public class LuaGetPlacer extends LuaMethod {

	public LuaGetPlacer() {
		super("getPlacer", TileEntityBase.class);
	}

	@Override
	protected Object[] invoke(TileEntity te, Object[] args) throws LuaMethodException, InterruptedException {
		TileEntityBase tile = (TileEntityBase)te;
		EntityPlayer ep = tile.getPlacer();
		return new Object[]{ep.getCommandSenderName(), ep.getUniqueID()};
	}

	@Override
	public String getDocumentation() {
		return "Returns the player who placed the machine.\nArgs: None\nReturns: [Name, UUID]";
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
