/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.io;

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
