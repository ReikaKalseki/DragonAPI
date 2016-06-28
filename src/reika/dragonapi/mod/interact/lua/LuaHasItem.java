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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import reika.dragonapi.libraries.ReikaInventoryHelper;
import dan200.computercraft.api.lua.LuaException;

public class LuaHasItem extends LuaMethod {

	public LuaHasItem() {
		super("hasItem", IInventory.class);
	}

	@Override
	public Object[] invoke(TileEntity te, Object[] args) throws LuaException, InterruptedException {
		IInventory ii = (IInventory) te;
		boolean flag = false;
		switch(args.length) {
		case 1: {
			Item itemID = Item.getItemById(((Double)args[0]).intValue());
			flag = ReikaInventoryHelper.checkForItem(itemID, ii);
		}
		break;
		case 2: {
			Item itemID = Item.getItemById(((Double)args[0]).intValue());
			int dmg = ((Double)args[1]).intValue();
			flag = ReikaInventoryHelper.checkForItemStack(itemID, dmg, ii);
		}
		break;
		case 3: {
			Item itemID = Item.getItemById(((Double)args[0]).intValue());
			int dmg = ((Double)args[1]).intValue();
			int size = ((Double)args[2]).intValue();
			ItemStack is = new ItemStack(itemID, size, dmg);
			flag = ReikaInventoryHelper.checkForItemStack(is, ii, true);
		}
		break;
		default:
			throw new IllegalArgumentException("Invalid ItemStack!");
		}
		return new Object[]{flag};
	}

	@Override
	public String getDocumentation() {
		return "Checks for the item in an inventory.\nArgs: ID, metadata (optional), stackSize (optional)\nReturns: true/false";
	}

	@Override
	public String getArgsAsString() {
		return "int id, int meta*, int size*";
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.BOOLEAN;
	}

}
