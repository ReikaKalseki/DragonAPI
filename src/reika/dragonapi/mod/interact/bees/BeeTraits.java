/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.mod.interact.bees;

import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;
import reika.dragonapi.mod.interact.bees.AlleleRegistry.Fertility;
import reika.dragonapi.mod.interact.bees.AlleleRegistry.Flowering;
import reika.dragonapi.mod.interact.bees.AlleleRegistry.Life;
import reika.dragonapi.mod.interact.bees.AlleleRegistry.Speeds;
import reika.dragonapi.mod.interact.bees.AlleleRegistry.Territory;
import reika.dragonapi.mod.interact.bees.AlleleRegistry.Tolerance;

public class BeeTraits {

	public Speeds speed;
	public Fertility fertility;
	public Flowering flowering;
	public Life lifespan;
	public Territory area;
	public Tolerance tempDir;
	public Tolerance humidDir;
	public int tempTol;
	public int humidTol;
	public EnumTemperature temperature;
	public EnumHumidity humidity;
	public boolean isNocturnal;
	public boolean isCaveDwelling;
	public boolean isTolerant;

	public BeeTraits() {
		speed = Speeds.SLOWEST;
		fertility = Fertility.NORMAL;
		flowering = Flowering.SLOWEST;
		lifespan = Life.SHORT;
		area = Territory.DEFAULT;
		tempDir = Tolerance.NONE;
		humidDir = Tolerance.NONE;
		tempTol = 0;
		humidTol = 0;
		temperature = EnumTemperature.NORMAL;
		humidity = EnumHumidity.NORMAL;
		isNocturnal = false;
		isCaveDwelling = false;
		isTolerant = false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Speed "+speed+"\n");
		sb.append("Fertility "+fertility+"\n");
		sb.append("Life "+lifespan+"\n");
		sb.append("Territory "+area+"\n");
		sb.append("Flowering "+flowering+"\n");
		sb.append("Temperature "+temperature+"\n");
		sb.append("Humidity "+humidity+"\n");
		return sb.toString();
	}

}
