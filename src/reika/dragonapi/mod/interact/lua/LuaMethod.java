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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.tileentity.TileEntity;
import reika.dragonapi.mod.interact.lua.library.LuaFluidColor;
import reika.dragonapi.mod.interact.lua.library.LuaGetBlock;
import reika.dragonapi.mod.registry.PowerTypes;
import dan200.computercraft.api.lua.LuaException;

public abstract class LuaMethod {

	public final String displayName;
	private final Class requiredClass;

	private static final Collection<LuaMethod> methods = new HashSet();

	private static final LuaMethod tanks = new LuaGetTanks();
	private static final LuaMethod readTank = new LuaReadTank();
	private static final LuaMethod getSlot = new LuaGetSlot();
	private static final LuaMethod getSizeInv = new LuaInvSize();
	private static final LuaMethod printInv = new LuaPrintInv();
	private static final LuaMethod getCoords = new LuaGetCoords();
	private static final LuaMethod isFull = new LuaIsFull();
	private static final LuaMethod isTankFull = new LuaIsTankFull();
	private static final LuaMethod hasItem = new LuaHasItem();
	private static final LuaMethod trigger = new LuaTriggerAction();
	private static final LuaMethod placer = new LuaGetPlacer();
	private static final LuaMethod nbt = new LuaGetNBTTag();

	private static final LuaMethod getRFStorage;
	private static final LuaMethod getRFCapacity;
	//private static final LuaMethod getEUStorage = new LuaGetStoredEU();
	//private static final LuaMethod getEUCapacity = new LuaGetEUCapacity();

	private static final LuaMethod fluidColor = new LuaFluidColor();
	private static final LuaMethod getBlock = new LuaGetBlock();

	static {
		if (PowerTypes.RF.isLoaded()) {
			getRFStorage = new LuaGetStoredRF();
			getRFCapacity = new LuaGetRFCapacity();
		}
		else {
			getRFStorage = null;
			getRFCapacity = null;
		}
	}

	public LuaMethod(String name, Class requiredParent) {
		displayName = name;

		requiredClass = requiredParent;

		if (methods.contains(this))
			throw new IllegalArgumentException("This method is a duplicate of one that already exists!");
		else
			methods.add(this);
	}

	public static final Collection<LuaMethod> getMethods() {
		return Collections.unmodifiableCollection(methods);
	}

	public static final int getNumberMethods() {
		return methods.size();
	}

	public abstract Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException;

	public abstract String getDocumentation();

	public boolean isDocumented() {
		return true;
	}

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
		LONG("long"),
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
