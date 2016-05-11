/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.message.Message;

import Reika.DragonAPI.Auxiliary.LoggingFilters.CoreFilter;
import Reika.DragonAPI.Auxiliary.LoggingFilters.LoggerType;


public class LagWarningFilter extends CoreFilter {

	private final String PREFIX = "Did the system time change, or is the server overloaded?";
	public final int minDelay;

	public LagWarningFilter(int ms) {
		super(LoggerType.SERVER);

		minDelay = ms;
	}


	@Override
	protected String parse(Message msg, Level lvl) {
		String sg = msg.getFormattedMessage();
		if (sg.contains(PREFIX)) {
			int ms = this.parse(sg);
			if (ms < minDelay)
				return null;
		}
		return "";
	}


	private int parse(String s) {
		String pre = "Running ";
		String post = "ms behind";
		s = s.substring(s.indexOf(pre)+pre.length(), s.indexOf(post));
		return Integer.parseInt(s);
	}

}
