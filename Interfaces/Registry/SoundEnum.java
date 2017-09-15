/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


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

	public void playSound(World world, double x, double y, double z, float volume, float pitch, boolean attenuate);

	public void playSoundNoAttenuation(World world, double x, double y, double z, float volume, float pitch, int range);

	public boolean attenuate();

	@SideOnly(Side.CLIENT)
	/** Use this for clientside volume controls. */
	public float getModulatedVolume();

	/** Should this audio file be preloaded for real-time playback? */
	public boolean preload();

}
