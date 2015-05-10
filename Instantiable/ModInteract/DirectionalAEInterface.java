/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.ModInteract;

import java.util.EnumSet;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class DirectionalAEInterface extends BasicAEInterface {

	private EnumSet<ForgeDirection> sideSet = EnumSet.noneOf(ForgeDirection.class);

	public DirectionalAEInterface(TileEntity te, ItemStack is) {
		super(te, is);
	}

	public DirectionalAEInterface connect(ForgeDirection dir) {
		sideSet.add(dir);
		return this;
	}

	public DirectionalAEInterface disconnect(ForgeDirection dir) {
		sideSet.remove(dir);
		return this;
	}

	public DirectionalAEInterface disconnectAll() {
		sideSet.clear();
		return this;
	}

	@Override
	public EnumSet<ForgeDirection> getConnectableSides() {
		return sideSet;
	}

	public static DirectionalAEInterface omni(TileEntity te, ItemStack is) {
		DirectionalAEInterface d = new DirectionalAEInterface(te, is);
		d.sideSet = EnumSet.allOf(ForgeDirection.class);
		return d;
	}

}
