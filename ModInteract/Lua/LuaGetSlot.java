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

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class LuaGetSlot extends LuaMethod {

	public LuaGetSlot() {
		super("getSlot", IInventory.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws Exception {
		int slot = ((Double)args[0]).intValue();
		IInventory ii = (IInventory)te;
		ItemStack is = ii.getStackInSlot(slot);
		if (is == null)
			return null;
		Object[] o = new Object[4];
		o[0] = is.itemID;
		o[1] = is.getItemDamage();
		o[2] = is.stackSize;
		o[3] = is.getDisplayName();
		return o;
	}

	@Override
	public String getDocumentation() {
		return "Returns the inventory slot contents.\nArgs: None\nReturns: \"Empty\" if empty, otherwise [itemID, metadata, stackSize, displayName]";
	}

	@Override
	public String getArgsAsString() {
		return "";
	}

}
