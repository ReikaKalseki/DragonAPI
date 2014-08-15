/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;


public interface OreType {

	public OreRarity getRarity();

	public boolean isNether();
	public boolean isEnd();


	public static enum OreRarity {
		EVERYWHERE(), //Copper, Fluorite
		COMMON(), //Tin, Redstone
		AVERAGE(), //Iron
		SCATTERED(), //Gold, Calcite
		SCARCE(), //Lapis, Diamond
		RARE(); //Emerald, Platinum
	}

}