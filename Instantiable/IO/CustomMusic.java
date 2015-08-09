/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;

public class CustomMusic implements ISound {

	public final float volume;
	public final float pitch;

	public final String path;
	private final ResourceLocation res;

	private boolean repeat = false;

	public CustomMusic(String path) {
		this(path, 1, 1);
	}

	public CustomMusic(String path, float vol, float p) {
		this.path = path;
		res = new ResourceLocation("custom_path", path);
		volume = vol;
		pitch = p;
	}

	public CustomMusic setRepeating() {
		repeat = true;
		return this;
	}

	@Override
	public ResourceLocation getPositionedSoundLocation() {
		return res;
	}

	@Override
	public boolean canRepeat() {
		return repeat;
	}

	@Override
	public int getRepeatDelay() {
		return 0;
	}

	@Override
	public float getVolume() {
		return volume;
	}

	@Override
	public float getPitch() {
		return pitch;
	}

	@Override
	public float getXPosF() {
		return 0;
	}

	@Override
	public float getYPosF() {
		return 0;
	}

	@Override
	public float getZPosF() {
		return 0;
	}

	@Override
	public AttenuationType getAttenuationType() {
		return AttenuationType.NONE;
	}

}
