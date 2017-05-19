/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.ParticleController;

import net.minecraft.entity.Entity;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.ColorController;


public class BlendListColorController implements ColorController {

	private final ColorBlendList colors;

	public BlendListColorController(ColorBlendList cbl) {
		colors = cbl;
	}

	@Override
	public void update(Entity e) {

	}

	@Override
	public int getColor(Entity e) {
		return colors.getColor(e.ticksExisted);
	}

}
