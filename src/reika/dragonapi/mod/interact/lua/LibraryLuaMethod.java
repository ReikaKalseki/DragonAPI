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


public abstract class LibraryLuaMethod extends LuaMethod {

	public LibraryLuaMethod(String name) {
		super(name, TileEntity.class);
	}

	public final boolean isDocumented() {
		return false;
	}

}
