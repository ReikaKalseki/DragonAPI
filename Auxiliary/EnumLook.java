/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;

@Deprecated
public enum EnumLook {

	PLUSX(),
	MINX(),
	PLUSZ(),
	MINZ(),
	UP(),
	DOWN();

	private EnumLook() {

	}

	public static EnumLook getLookDir(EntityLivingBase e, boolean hasV) {
		float y = e.rotationPitch;
		int i = MathHelper.floor_double((e.rotationYaw * 4F) / 360F + 0.5D);
		while (i > 3)
			i -= 4;
		while (i < 0)
			i += 4;
		if (!hasV || Math.abs(y) < 60) {
			switch (i) {
			case 3:
				return PLUSX;
			case 1:
				return MINX;
			case 0:
				return PLUSZ;
			case 2:
				return MINZ;
			}
		}
		else {
			if (y < 0)
				return UP; // LOOKING up!
			else
				return DOWN;
		}
		return null;
	}

	public int getMetadataOfLook() {
		switch(this) {
		case UP:
			return 6;
		case DOWN:
			return 5;
		case MINX:
			return 1;
		case MINZ:
			return 2;
		case PLUSX:
			return 3;
		case PLUSZ:
			return 0;
		default:
			return 0;
		}
	}

	public boolean isTopOrBottom() {
		return this == UP || this == DOWN;
	}

	public boolean isXDir() {
		return this == PLUSX || this == MINX;
	}

	public boolean isZDir() {
		return this == PLUSZ || this == MINZ;
	}

	public ForgeDirection getCorrespondingForge() {
		switch(this) {
		case DOWN:
			return ForgeDirection.DOWN;
		case MINX:
			return ForgeDirection.WEST;
		case MINZ:
			return ForgeDirection.NORTH;
		case PLUSX:
			return ForgeDirection.EAST;
		case PLUSZ:
			return ForgeDirection.SOUTH;
		case UP:
			return ForgeDirection.UP;
		default:
			return ForgeDirection.UNKNOWN;
		}
	}
}
