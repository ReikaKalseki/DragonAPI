/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import net.minecraft.util.ResourceLocation;

/** Damn that assets folder... */
public final class ForcedResource extends ResourceLocation {

	private String texture;

	public ForcedResource(String tex) {
		super(tex);
	}

	@Override
	public String getResourcePath()
	{
		return texture;
	}

	@Override
	public String getResourceDomain()
	{
		return texture;
	}

}
