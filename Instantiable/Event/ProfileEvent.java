/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Event;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.Event;


/** Generally not used for actual profiling handling, but for the massive number of hooks it provides into vanilla code. Check the profiler's calls
  to see potential uses. */
public class ProfileEvent extends Event {

	public final String sectionName;

	public ProfileEvent(String s) {
		sectionName = s;
	}

	public static void fire(String tag) {
		MinecraftForge.EVENT_BUS.post(new ProfileEvent(tag));
	}
}
