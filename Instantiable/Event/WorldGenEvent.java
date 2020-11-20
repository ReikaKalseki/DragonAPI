/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import java.util.Random;

import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

public abstract class WorldGenEvent extends WorldPositionEvent {

	private final Random rand;

	public WorldGenEvent(World world, int x, int y, int z, Random r) {
		super(world, x, y, z);
		rand = r;
	}

	public final int getRandomInt(int n) {
		return rand.nextInt(n);
	}

	public final float getRandomFloat() {
		return rand.nextFloat();
	}

	public final double getRandomDouble() {
		return rand.nextDouble();
	}

}
