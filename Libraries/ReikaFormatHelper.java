/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

public class ReikaFormatHelper {

	public static final int HOUR = 72000;
	public static final int MINUTE = 1200;
	public static final int SECOND = 20;

	public static String getTickAsHMS(int ticks) {
		int hour = 0;
		int minute = 0;
		double second = 0D;

		hour = ticks/HOUR;
		minute = (ticks-hour*HOUR)/MINUTE;
		second = (ticks-hour*HOUR-minute*MINUTE)/(double)SECOND;

		return String.format("%dh:%dm:%.2fs", hour, minute, second);
	}

}
