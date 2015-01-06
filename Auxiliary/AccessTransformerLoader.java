/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.io.IOException;
import java.util.Map;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class AccessTransformerLoader implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "Reika.DragonAPI.Auxiliary.AccessTransformerLoader$DragonAccessTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return null;
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
		return "Reika.DragonAPI.Auxiliary.AccessTransformerLoader$DragonAccessTransformer";
	}

	public static class DragonAccessTransformer extends AccessTransformer {

		public DragonAccessTransformer() throws IOException {
			super("DragonAPI_at.cfg");
		}

	}

}
