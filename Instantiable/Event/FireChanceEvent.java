/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;



public class FireChanceEvent extends WorldPositionEvent {

	/** An arbitrarily large number (ie not a 0-1 decimal); higher means more chance to spread. Values of 400 or so guarantee a spread event. */
	public int spreadChance;
	public final int baseSpreadChance;

	/** In some versions of Minecraft/Forge, this is <i>always</i> "UP", so do not rely on it to be accurate. */
	public final ForgeDirection side;

	private static float lastRatio;

	public FireChanceEvent(World world, int x, int y, int z, ForgeDirection side, int base) {
		super(world, x, y, z);

		this.side = side;

		baseSpreadChance = spreadChance = base;
	}

	public static int fire(World world, int x, int y, int z, int base) {
		return fire(world, x, y, z, ForgeDirection.UP, base);
	}

	public static int fire(World world, int x, int y, int z, ForgeDirection side, int base) {
		FireChanceEvent evt = new FireChanceEvent(world, x, y, z, side, base);
		MinecraftForge.EVENT_BUS.post(evt);
		lastRatio = evt.baseSpreadChance > 0 ? (float)evt.spreadChance/evt.baseSpreadChance : 0;
		return evt.spreadChance;
	}

	public static int pass2(int base) {
		return lastRatio <= 0 ? base : (int)(base*lastRatio);
	}

}
