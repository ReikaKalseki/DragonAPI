/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.world.World;


/** This is an interface for ENUMS! */
public interface SoundEnum {

	public String getName();

	public String getPath();

	//public URL getURL();

	public SoundCategory getCategory();

	//public int getTickDuration();

	public int ordinal();

	public boolean canOverlap();

	public void playSound(World world, double x, double y, double z, float volume, float pitch);

	public boolean attenuate();

}
