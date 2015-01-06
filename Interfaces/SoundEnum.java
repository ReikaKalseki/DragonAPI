/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import net.minecraft.client.audio.SoundCategory;


/** This is an interface for ENUMS! */
public interface SoundEnum {

	public String getName();

	public String getPath();

	//public URL getURL();

	public SoundCategory getCategory();

	//public int getTickDuration();

	public int ordinal();

	public boolean canOverlap();

}
