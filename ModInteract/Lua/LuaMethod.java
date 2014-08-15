/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Lua;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;

public abstract class LuaMethod {

	public final String displayName;
	private final Class requiredClass;

	private static final ArrayList<LuaMethod> methods = new ArrayList();

	private static final LuaMethod tanks = new LuaGetTanks();
	private static final LuaMethod readTank = new LuaReadTank();
	private static final LuaMethod getSlot = new LuaGetSlot();
	private static final LuaMethod getSizeInv = new LuaInvSize();
	private static final LuaMethod printInv = new LuaPrintInv();
	private static final LuaMethod getCoords = new LuaGetCoords();
	private static final LuaMethod isFull = new LuaIsFull();
	private static final LuaMethod isTankFull = new LuaIsTankFull();
	private static final LuaMethod hasItem = new LuaHasItem();

	public LuaMethod(String name, Class requiredParent) {
		displayName = name;

		requiredClass = requiredParent;

		if (methods.contains(this))
			throw new IllegalArgumentException("This method is a duplicate of one that already exists!");
		else
			methods.add(this);
	}

	public static final List<LuaMethod> getMethods() {
		return ReikaJavaLibrary.copyList(methods);
	}

	public static final int getNumberMethods() {
		return methods.size();
	}

	public abstract Object[] invoke(TileEntity te, Object[] args) throws Exception;

	public abstract String getDocumentation();

	public final boolean isClassInstanceOf(Class<? extends TileEntity> te) {
		return requiredClass != null ? requiredClass.isAssignableFrom(te) : true;
	}

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

	@Override
	public final String toString() {
		String name = requiredClass != null ? requiredClass.getSimpleName() : "Any TileEntity";
		return displayName+"() for "+name;
	}

	/** Without "( )" */
	public abstract String getArgsAsString();

	public abstract ReturnType getReturnType();

	public static enum ReturnType {
		VOID("void"),
		INTEGER("int"),
		ARRAY("Object[]"),
		STRING("String"),
		BOOLEAN("boolean"),
		FLOAT("float");

		public final String displayName;

		private ReturnType(String name) {
			displayName = name;
		}

	}

}