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

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class DragonAPIASMHandler implements IFMLLoadingPlugin {

	static {
		//Launch.classLoader.addTransformerExclusion("Reika");  Breaks @SideOnly
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{"Reika.DragonAPI.Extras.APIStripper"};
	}

	@Override
	public String getModContainerClass() {
		return "Reika.DragonAPI.Extras.APIStripper$AnnotationDummyContainer";
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
