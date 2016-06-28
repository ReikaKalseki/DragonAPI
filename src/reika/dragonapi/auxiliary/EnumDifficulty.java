/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary;

public enum EnumDifficulty {

	NOOB(),
	EASY(),
	MEDIUM(),
	HARD(),
	UNPLAYABLE();

	private static final EnumDifficulty[] difficultyList = EnumDifficulty.values();

	private EnumDifficulty() {

	}

	public static EnumDifficulty getDifficulty(int id) {
		if (id < 0)
			return NOOB;
		if (id >= difficultyList.length)
			return UNPLAYABLE;
		return difficultyList[id];
	}

	public static EnumDifficulty getBoundedDifficulty(int id, EnumDifficulty low, EnumDifficulty hi) {
		if (id < low.ordinal())
			return low;
		if (id > hi.ordinal())
			return hi;
		return difficultyList[id];
	}

}
