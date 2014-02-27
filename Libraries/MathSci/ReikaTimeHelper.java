/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.MathSci;

public enum ReikaTimeHelper {

	YEAR(630720000, "y"),
	MONTH(51840000, "mo"),
	WEEK(12096000, "w"),
	DAY(1728000, "d"),
	HOUR(72000, "h"),
	MINUTE(1200, "m"),
	SECOND(20, "s"),
	TICK(1, "t");

	private int time;
	private String abbrev;

	private ReikaTimeHelper(int t, String a) {
		time = t;
		abbrev = a;
	}

	/** Returns the number of times the given unit happens in this unit */
	public double getNumberOf(ReikaTimeHelper time) {
		return this.getDuration()/time.getDuration();
	}

	public int getDuration() {
		return time;
	}

	public int getMinecraftDuration() {
		switch(this) {
		case DAY:
			return time/144; //10 minutes
		case HOUR:
			return time/144; //25 seconds
		case MONTH: // Moon phases cycle every 8 days
			return DAY.getMinecraftDuration()*8;
		case WEEK: //20 minutes
			return DAY.getMinecraftDuration()*2;
		case YEAR: //8 real hours
			return MONTH.getMinecraftDuration()*6;
		case MINUTE:
		case SECOND:
		case TICK:
		default:
			return time;
		}
	}

	public String getAbbreviation() {
		return abbrev;
	}
}
