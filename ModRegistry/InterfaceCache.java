/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
//package Reika.DragonAPI.Auxiliary;
package Reika.DragonAPI.ModRegistry;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public enum InterfaceCache {

	IGALACTICWORLD("micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider"),
	ISOLARLEVEL("micdoodle8.mods.galacticraft.api.world.ISolarLevel"),
	IELECTRICITEM("ic2.api.item.IElectricItem"),
	IMISSILE("com.builtbroken.icbm.api"),
	MUSEELECTRICITEM("net.machinemuse.api.electricity.MuseElectricItem"),
	RFENERGYITEM("cofh.api.energy.IEnergyContainerItem"),
	WAILA("mcp.mobius.waila.api.IWailaDataProvider"),
	IWRENCH("buildcraft.api.tools.IToolWrench"),
	IC2WRENCH("ic2.api.tile.IWrenchable"),
	NODE("thaumcraft.api.nodes.INode"),
	GRIDHOST("appeng.api.networking.IGridHost"),
	BCROBOT("buildcraft.api.robots.EntityRobotBase"),
	AREAPROVIDER("buildcraft.api.core.IAreaProvider"),
	TINKERTOOL("tconstruct.library.tools.ToolCore");

	private final String classpath;
	public final String name;
	private final Class object;

	private InterfaceCache(String s) {
		classpath = s;
		String[] sp = s.split("\\.");
		name = sp[sp.length-1];
		object = ReikaJavaLibrary.getClassNoException(s);
	}

	public boolean exists() {
		return object != null;
	}

	public boolean instanceOf(Object o) {
		return object != null && o != null && object.isAssignableFrom(o.getClass());
	}

	public boolean instanceOf(Class c) {
		return object != null && object.isAssignableFrom(c);
	}
}
//}
