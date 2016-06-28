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
import reika.dragonapi.mod.interact.lua.LibraryLuaMethod;
import dan200.computercraft.api.lua.LuaException;


public class LuaGetBlock extends LibraryLuaMethod {

	public LuaGetBlock() {
		super("getBlock");
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		int x = ((Double)args[0]).intValue();
		int y = ((Double)args[1]).intValue();
		int z = ((Double)args[2]).intValue();
		return new Object[]{te.worldObj.getBlock(x, y, z).getUnlocalizedName(), te.worldObj.getBlockMetadata(x, y, z)};
	}

	@Override
	public String getDocumentation() {
		return "Returns block and metadata at position.\nArgs: x, y, z\nReturns: {Block Name, Metadata}";
	}

	@Override
	public String getArgsAsString() {
		return "int x, int y, int z";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.ARRAY;
	}

}
