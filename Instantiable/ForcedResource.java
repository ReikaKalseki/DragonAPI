package Reika.DragonAPI.Instantiable;

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
