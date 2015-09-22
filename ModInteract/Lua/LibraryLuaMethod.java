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


public abstract class LibraryLuaMethod extends LuaMethod {

	public LibraryLuaMethod(String name) {
		super(name, TileEntity.class);
	}

	public final boolean isDocumented() {
		return false;
	}

}
