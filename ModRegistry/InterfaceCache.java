/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
//package Reika.DragonAPI.Auxiliary;
package Reika.DragonAPI.ModRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public enum InterfaceCache {

	IGALACTICWORLD("micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider"),
	ISOLARLEVEL("micdoodle8.mods.galacticraft.api.world.ISolarLevel"),
	IELECTRICITEM("ic2.api.item.IElectricItem"),
	IMISSILE("com.builtbroken.icbm.api.missile.IMissileEntity"),
	MUSEELECTRICITEM("net.machinemuse.api.electricity.MuseElectricItem"),
	RFENERGYITEM("cofh.api.energy.IEnergyContainerItem"),
	WAILA("mcp.mobius.waila.api.IWailaDataProvider"),
	IWRENCH("buildcraft.api.tools.IToolWrench"),
	IC2WRENCH("ic2.api.tile.IWrenchable"),
	NODE("thaumcraft.api.nodes.INode"),
	GRIDHOST("appeng.api.networking.IGridHost"),
	BCROBOT("buildcraft.api.robots.EntityRobotBase"),
	AREAPROVIDER("buildcraft.api.core.IAreaProvider"),
	TINKERTOOL("tconstruct.library.tools.ToolCore"),
	DSU("powercrystals.minefactoryreloaded.api.IDeepStorageUnit"),
	MEINTERFACE("appeng.tile.misc.TileInterface"),
	STREAM("streams.block.FixedFlowBlock"),
	SPELLSHOT("WayofTime.alchemicalWizardry.api.spell.EntitySpellProjectile"),
	GASITEM("mekanism.api.gas.IGasItem"),
	BEEHOUSE("forestry.api.apiculture.IBeeHousing"),
	ENERGYITEM("com.builtbroken.mc.api.items.energy.IEnergyItem"),
	IMMERSIVEWIRE("blusunrize.immersiveengineering.api.energy.IImmersiveConnectable"),
	IC2POWERTILE("ic2.api.energy.tile.IEnergyTile"),
	BOPBIOME("biomesoplenty.api.biome.BOPBiome"),
	PAINTABLE("crazypants.enderio.machine.painter.IPaintableTileEntity"),
	EIOCONDUIT("crazypants.enderio.conduit.IConduitBundle"),
	EIOCONDUITBLOCK("crazypants.enderio.conduit.BlockConduitBundle"),
	RECONFIGURABLEFACE("cofh.api.tileentity.IReconfigurableFacing"),
	IPLANETWORLD("zmaster587.advancedRocketry.api.IPlanetaryProvider"),
	BCPIPE("buildcraft.api.transport.IPipeTile"),
	BCPIPEBLOCK("buildcraft.transport.BlockGenericPipe"),
	AEPATTERN("appeng.api.implementations.ICraftingPatternItem"),
	TDDUCT("cofh.thermaldynamics.block.TileTDBase"),
	TDDUCTBLOCK("cofh.thermaldynamics.block.BlockTDBase"),
	AECABLE("appeng.tile.networking.TileCableBus"),
	AECABLEBLOCK("appeng.block.networking.BlockCableBus"),
	ESSENTIADISTILL("tuhljin.automagy.api.essentia.IEssentiaDistillery"),
	WARPGEAR("thaumcraft.api.IWarpingGear"),
	BREWITEM("vazkii.botania.api.brew.IBrewItem"),
	IC2NUKE("ic2.api.reactor.IReactor"),
	IC2NUKECHAMBER("ic2.api.reactor.IReactorChamber"),
	;

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

	public Class getClassType() {
		return object;
	}

	public Field getField(String s) throws Exception {
		Field f = this.getClassType().getDeclaredField(s);
		f.setAccessible(true);
		return f;
	}

	public Method getMethod(String s, Class... args) throws Exception {
		Method m = this.getClassType().getDeclaredMethod(s, args);
		m.setAccessible(true);
		return m;
	}
}
//}
