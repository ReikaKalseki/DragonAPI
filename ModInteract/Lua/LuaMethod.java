/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Lua;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public abstract class LuaMethod {

	public final String displayName;
	private final Class requiredClass;

	private static final ArrayList<LuaMethod> methods = new ArrayList();

	private static final LuaMethod tanks = new LuaGetTanks();
	private static final LuaMethod readTank = new LuaReadTank();
	private static final LuaMethod getSlot = new LuaGetSlot();
	private static final LuaMethod getSizeInv = new LuaInvSize();

	public LuaMethod(String name, Class requiredParent) {
		displayName = name;

		requiredClass = requiredParent;

		if (!methods.contains(this))
			methods.add(this);
	}

	public static final List<LuaMethod> getMethods() {
		return ReikaJavaLibrary.copyList(methods);
	}

	public abstract Object[] invoke(TileEntity te, Object[] args) throws Exception;

	public final boolean isValidFor(TileEntity te) {
		return requiredClass != null ? requiredClass.isAssignableFrom(te.getClass()) : true;
	}

	@Override
	public final boolean equals(Object o) {
		if (o instanceof LuaMethod) {
			return ((LuaMethod)o).displayName.equals(displayName) && requiredClass == ((LuaMethod)o).requiredClass;
		}
		else
			return false;
	}

}
