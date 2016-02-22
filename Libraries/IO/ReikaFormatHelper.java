/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ReikaFormatHelper {

	private static final DateFormat dateFormatting = new SimpleDateFormat("MM-dd-yyyy_HH:mm:ss");
	private static final DateFormat dateFormattingFilesafe = new SimpleDateFormat("MM-dd-yyyy_HH;mm;ss");
	private static final Calendar calendar = Calendar.getInstance();

	public static final int HOUR = 72000;
	public static final int MINUTE = 1200;
	public static final int SECOND = 20;
	public static final int MILLI = 1000;

	public static String getTickAsHMS(int ticks) {
		int hour = ticks/HOUR;
		int minute = (ticks-hour*HOUR)/MINUTE;
		double second = (ticks-hour*HOUR-minute*MINUTE)/(double)SECOND;

		return String.format("%dh:%dm:%.2fs", hour, minute, second);
	}

	public static String getSecondsAsClock(int time) {
		int hour = time*20/HOUR;
		int minute = (time*20-hour*HOUR)/MINUTE;
		int second = (time*20-hour*HOUR-minute*MINUTE)/SECOND;

		return String.format("%02d:%02d:%02d", hour, minute, second);
	}

	public static String millisToHMSms(long millis) {
		long hour = TimeUnit.MILLISECONDS.toHours(millis);
		long in1 = millis-TimeUnit.HOURS.toMillis(hour);
		long minute = TimeUnit.MILLISECONDS.toMinutes(in1);
		long in2 = in1-TimeUnit.MINUTES.toMillis(minute);
		long second = TimeUnit.MILLISECONDS.toSeconds(in2);
		long in3 = in2-TimeUnit.SECONDS.toMillis(second);

		return String.format("%dh:%dm:%ds:%dms", hour, minute, second, in3);
	}

	public static String nanosToHMSms(long nanos) {
		long hour = TimeUnit.NANOSECONDS.toHours(nanos);
		long in1 = nanos-TimeUnit.HOURS.toNanos(hour);
		long minute = TimeUnit.NANOSECONDS.toMinutes(in1);
		long in2 = in1-TimeUnit.MINUTES.toNanos(minute);
		long second = TimeUnit.NANOSECONDS.toSeconds(in2);
		long in3 = in2-TimeUnit.SECONDS.toNanos(second);
		long milli = TimeUnit.NANOSECONDS.toMillis(in3);
		long in4 = in3-TimeUnit.MILLISECONDS.toNanos(milli);
		long micro = TimeUnit.NANOSECONDS.toMicros(in4);
		long nano = in4-TimeUnit.MICROSECONDS.toNanos(micro);

		return String.format("%dh:%dm:%ds:%dms:%dus:%sns", hour, minute, second, milli, micro, nano);
	}

	public static String getCurrentTime() {
		return dateFormatting.format(calendar.getTime());
	}

	public static String getFormattedTime(long t) {
		return dateFormatting.format(new Date(t));
	}

	public static String getFormattedTimeFilesafe(long t) {
		return dateFormattingFilesafe.format(new Date(t));
	}

}
