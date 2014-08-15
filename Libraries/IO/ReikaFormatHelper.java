/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

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

	public static String getSecondsAsClock(int time) {
		int hour = 0;
		int minute = 0;
		int second = 0;

		hour = time*20/HOUR;
		minute = (time*20-hour*HOUR)/MINUTE;
		second = (time*20-hour*HOUR-minute*MINUTE)/SECOND;

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

}