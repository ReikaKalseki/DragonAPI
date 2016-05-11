/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.entity.Entity;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ColorController;
import Reika.DragonAPI.Instantiable.Formula.MathExpression;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;


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
