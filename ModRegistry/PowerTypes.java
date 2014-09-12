/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModRegistry;


public enum PowerTypes {

	MJ("buildcraft.api.power.IPowerReceptor", "buildcraft.api.power.IPowerEmitter"),
	RF("cofh.api.energy.IEnergyHandler", "cofh.api.energy.IEnergyContainerItem"),
	EU("ic2.api.energy.tile.IEnergyTile", "ic2.api.item.IElectricItem"),
	UE("universalelectricity.api.energy.IEnergyInterface", "universalelectricity.api.item.IEnergyItem"),
	ROTARYCRAFT("Reika.RotaryCraft.API.ShaftMachine"),
	PNEUMATIC(),
	HYDRAULIC();

	private final boolean exists;

	private PowerTypes(String... cl) {
		exists = cl != null && cl.length > 0 && this.checkAllClasses(cl);
	}

	private static boolean checkAllClasses(String... cl) {
		for (int i = 0; i < cl.length; i++) {
			String s = cl[i];
			try {
				Class c = Class.forName(s);
			}
			catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public boolean exists() {
		return exists;
	}
}
