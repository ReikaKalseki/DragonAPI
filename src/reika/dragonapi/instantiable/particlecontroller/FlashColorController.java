/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.particlecontroller;

import net.minecraft.entity.Entity;
import reika.dragonapi.instantiable.formula.MathExpression;
import reika.dragonapi.libraries.io.ReikaColorAPI;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ColorController;


public class FlashColorController implements ColorController {

	private final MathExpression mixFactor;
	public final int baseColor;
	public final int flashColor;

	public FlashColorController(MathExpression e, int c, int c2) {
		mixFactor = e;
		baseColor = c;
		flashColor = c2;
	}

	@Override
	public void update(Entity e) {

	}

	@Override
	public int getColor(Entity e) {
		float f = Math.abs((float)mixFactor.evaluate(System.currentTimeMillis()/200D));
		//ReikaJavaLibrary.pConsole(e.ticksExisted+">"+f);
		return ReikaColorAPI.mixColors(baseColor, flashColor, f);
	}

}
