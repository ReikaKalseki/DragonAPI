package Reika.DragonAPI.IO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import Reika.DragonAPI.DragonOptions;


public class ThrottleableEffectRenderer extends EffectRenderer {

	public final int limit;

	private final EffectRenderer original;

	public ThrottleableEffectRenderer(EffectRenderer eff) {
		super(Minecraft.getMinecraft().theWorld, Minecraft.getMinecraft().renderEngine);
		limit = Math.max(250, DragonOptions.PARTICLELIMIT.getValue());
		original = eff;
	}

	@Override
	public void addEffect(EntityFX fx)
	{
		int i = fx.getFXLayer();

		if (fxLayers[i].size() >= limit) {
			fxLayers[i].remove(0);
		}

		fxLayers[i].add(fx);
	}

}
