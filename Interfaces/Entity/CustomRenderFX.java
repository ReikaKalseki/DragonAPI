/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Entity;

import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.RenderMode;
import Reika.DragonAPI.Instantiable.Rendering.ParticleEngine.TextureMode;


public interface CustomRenderFX {

	RenderMode getRenderMode();

	TextureMode getTexture();

	public boolean rendersOverLimit();

	//public double getRenderRange();

}
