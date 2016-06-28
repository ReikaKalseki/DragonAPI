/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary;

import java.util.Map;

import net.minecraft.launchwrapper.Launch;
import reika.dragonapi.asm.APIStripper;
import reika.dragonapi.asm.DependentMethodStripper;
import reika.dragonapi.asm.DragonAPIClassTransformer;
import reika.dragonapi.asm.FMLItemBlockPatch;
import reika.dragonapi.asm.FluidNamePatch;
import reika.dragonapi.asm.InterfaceInjector;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@SortingIndex(1001)
@MCVersion("1.7.10")
public class DragonAPIASMHandler implements IFMLLoadingPlugin {

	static {
		Launch.classLoader.addTransformerExclusion("Reika.DragonAPI.ASM");
		Launch.classLoader.addTransformerExclusion("Reika.LegacyCraft.LegacyASMHandler");
		Launch.classLoader.addTransformerExclusion("Reika.ChromatiCraft.Auxiliary.ChromaASMHandler");
		Launch.classLoader.addTransformerExclusion("Reika.RotaryCraft.Auxiliary.RotaryASMHandler");
		Launch.classLoader.addTransformerExclusion("Reika.RotaryCraft.Auxiliary.RotaryIntegrationManager");

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
