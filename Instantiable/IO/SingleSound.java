/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import Reika.DragonAPI.Interfaces.Registry.SoundEnum;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public final class SingleSound implements SoundEnum {

	public final String name;
	public final String path;
	public final SoundCategory category;

	public SingleSound(String n, String p, SoundCategory cat) {
		name = n;
		path = p;
		category = cat;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public SoundCategory getCategory() {
		return category;
	}

	@Override
	public int ordinal() {
		return 0;
	}

	@Override
	public boolean canOverlap() {
		return true;
	}

	@Override
	public void playSound(World world, double x, double y, double z, float volume, float pitch) {

	}

	public void playSound(World world, double x, double y, double z, float vol, float pitch, boolean attenuate) {

	}

	public void playSoundNoAttenuation(World world, double x, double y, double z, float vol, float pitch, int broadcast) {

	}

	@Override
	public void playSound(Entity e, float volume, float pitch) {

	}

	@Override
	public boolean attenuate() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getModulatedVolume() {
		return 1;
	}

	@Override
	public boolean preload() {
		return false;
	}

}
