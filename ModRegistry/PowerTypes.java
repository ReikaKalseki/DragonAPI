/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModRegistry;

import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Interfaces.Registry.Dependency;


public enum PowerTypes implements Dependency {

	//MJ("buildcraft.api.power.IPowerReceptor", "buildcraft.api.power.IPowerEmitter"), killed by RF
	RF("cofh.api.energy.IEnergyHandler", "cofh.api.energy.IEnergyContainerItem"),
	EU("ic2.api.energy.tile.IEnergyTile", "ic2.api.item.IElectricItem"),
	//UE("universalelectricity.api.energy.IEnergyInterface", "universalelectricity.api.item.IEnergyItem"), killed by RF
	ROTARYCRAFT("Reika.RotaryCraft.API.Power.ShaftMachine"),
	PNEUMATIC("pneumaticCraft.api.tileentity.IPneumaticMachine"),
	HYDRAULIC(),
	STEAM(FluidRegistry.getFluid("steam") != null);

	private final boolean exists;

	private PowerTypes(boolean f) {
		exists = f;

		DragonAPICore.log("Power type "+this+" loaded: "+f);
	}

	private PowerTypes(String... cl) {
		this(cl != null && cl.length > 0 && checkAllClasses(cl));
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

	@Override
	public boolean isLoaded() {
		return exists;
	}

	@Override
	public String getDisplayName() {
		return this.name();
	}
}
