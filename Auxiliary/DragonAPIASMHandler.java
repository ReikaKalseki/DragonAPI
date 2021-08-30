/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.Map;

import net.minecraft.launchwrapper.Launch;

import Reika.DragonAPI.ASM.APIStripper;
import Reika.DragonAPI.ASM.ClassReparenter;
import Reika.DragonAPI.ASM.DependentMethodStripper;
import Reika.DragonAPI.ASM.DragonAPIClassTransformer;
import Reika.DragonAPI.ASM.FMLItemBlockPatch;
import Reika.DragonAPI.ASM.FluidNamePatch;
import Reika.DragonAPI.ASM.InterfaceInjector;
import Reika.DragonAPI.ASM.SpecialBiomePlacement;
import Reika.DragonAPI.ASM.StructureLootHooks;
import Reika.DragonAPI.ASM.Profiling.ChunkInitProfilingHooks;
import Reika.DragonAPI.ASM.Profiling.MapGeneratorProfilingHooks;
import Reika.DragonAPI.ASM.Profiling.WorldGeneratorProfilingHooks;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@SortingIndex(1001)
@MCVersion("1.7.10")
public class DragonAPIASMHandler implements IFMLLoadingPlugin {

	static {
		Launch.classLoader.addTransformerExclusion("Reika.DragonAPI.ASM");

		Launch.classLoader.addTransformerExclusion("Reika.LegacyCraft.ASM");
		Launch.classLoader.addTransformerExclusion("Reika.ChromatiCraft.Auxiliary.ChromaASMHandler");
		Launch.classLoader.addTransformerExclusion("Reika.CaveControl.CaveASMHandler");
		Launch.classLoader.addTransformerExclusion("Reika.RotaryCraft.Auxiliary.RotaryASMHandler");
		Launch.classLoader.addTransformerExclusion("Reika.RotaryCraft.Auxiliary.RotaryIntegrationManager");
		Launch.classLoader.addTransformerExclusion("Reika.TreeClimbing.TreeClimbingASMHandler");

		Launch.classLoader.addTransformerExclusion("Reika.DragonAPI.Libraries.Java.ReikaASMHelper");
		Launch.classLoader.addTransformerExclusion("Reika.DragonAPI.Libraries.Java.ReikaJVMParser");
		Launch.classLoader.addTransformerExclusion("Reika.DragonAPI.Exception.ASMException");

		Launch.classLoader.addTransformerExclusion("Reika.DragonAPI.ModInteract.BannedItemReader");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{
				InterfaceInjector.class.getName(), //Must run before dependent method stripper
				APIStripper.class.getName(),

				DragonAPIClassTransformer.class.getName(),

				FMLItemBlockPatch.class.getName(),
				FluidNamePatch.class.getName(),

				DependentMethodStripper.class.getName(),
				ClassReparenter.class.getName(),

				WorldGeneratorProfilingHooks.class.getName(),
				MapGeneratorProfilingHooks.class.getName(),
				ChunkInitProfilingHooks.class.getName(),
				//BiomeTerrainProfilingHooks.class.getName(),

				StructureLootHooks.class.getName(),
				SpecialBiomePlacement.class.getName(),
		};
	}

	@Override
	public String getModContainerClass() {
		return "Reika.DragonAPI.ASM.APIStripper$AnnotationDummyContainer";
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
