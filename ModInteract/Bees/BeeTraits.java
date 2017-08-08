/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Bees;

import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Tolerance;
import forestry.api.core.EnumHumidity;
import forestry.api.core.EnumTemperature;

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
