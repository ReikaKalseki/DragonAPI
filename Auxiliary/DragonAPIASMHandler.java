/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.Map;

import Reika.DragonAPI.ASM.APIStripper;
import Reika.DragonAPI.ASM.DragonAPIClassTransfomer;
import Reika.DragonAPI.ASM.FMLItemBlockPatch;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.7.10")
public class DragonAPIASMHandler implements IFMLLoadingPlugin {

	static {
		//Launch.classLoader.addTransformerExclusion("Reika");  Breaks @SideOnly and EventHandlers
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{
				APIStripper.class.getName(),
				DragonAPIClassTransfomer.class.getName(),
				FMLItemBlockPatch.class.getName(),
				//DependentMethodStripper.class.getName()
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
		return "";
	}

}
