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

import net.minecraftforge.common.EnumPlantType;

import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Heights;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Maturation;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Saplings;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Sappiness;
import Reika.DragonAPI.ModInteract.Bees.TreeAlleleRegistry.Yield;

public class TreeTraits {

	public Yield yield;
	public Saplings fertility;
	public Sappiness sappiness;
	public Heights height;
	public Maturation maturation;
	public Territory area;
	public int girth;
	public EnumPlantType plant;
	public boolean isFireproof;

	public TreeTraits() {
		yield = Yield.LOWEST;
		fertility = Saplings.LOWER;
		sappiness = Sappiness.LOWEST;
		height = Heights.AVERAGE;
		maturation = Maturation.SLOWER;
		area = Territory.DEFAULT;
		girth = 1;
		plant = EnumPlantType.Plains;
		isFireproof = false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("Yield "+yield+"\n");
		sb.append("Fertility "+fertility+"\n");
		sb.append("Maturation "+maturation+"\n");
		sb.append("Territory "+area+"\n");
		sb.append("Height "+height+"\n");
		sb.append("Girth "+girth+"\n");
		sb.append("Plant Type "+plant+"\n");
		return sb.toString();
	}

}
