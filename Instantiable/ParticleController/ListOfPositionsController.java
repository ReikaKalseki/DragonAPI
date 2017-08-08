/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.ParticleController;

import java.util.List;

import net.minecraft.entity.Entity;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Interfaces.PositionController;

public class ListOfPositionsController implements PositionController {

	public final int entityLife;

	private int tick = 0;
	private List<DecimalPosition> points;

	public ListOfPositionsController(int l, List<DecimalPosition> li) {
		entityLife = l;
		points = li;
	}

	public ListOfPositionsController setTick(int tick) {
		this.tick = tick;
		return this;
	}

	@Override
	public void update(Entity e) {
		tick++;
	}

	private int getIndex(Entity e) {
		int t = tick*points.size()/entityLife;
		return Math.min(t, points.size()-1);
	}

	@Override
	public double getPositionX(Entity e) {
		return points.get(this.getIndex(e)).xCoord;
	}

	@Override
	public double getPositionY(Entity e) {
		return points.get(this.getIndex(e)).yCoord;
	}

	@Override
	public double getPositionZ(Entity e) {
		return points.get(this.getIndex(e)).zCoord;
	}

}
