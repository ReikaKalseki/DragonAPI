/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.io;

import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import reika.dragonapi.interfaces.registry.SoundEnum;
import reika.dragonapi.io.DirectResourceManager;

public class EnumSound implements ISound {

	public final double posX;
	public final double posY;
	public final double posZ;
	public final float volume;
	public final float pitch;

	public final SoundEnum sound;
	private final ResourceLocation res;

	public final boolean attenuate;

	private boolean repeat = false;

	public EnumSound(SoundEnum obj, double x, double y, double z, float vol, float p, boolean att) {
		sound = obj;
		res = DirectResourceManager.getResource(obj.getPath());
		posX = x;
		posY = y;
		posZ = z;
		volume = vol;
		pitch = p;
		attenuate = att;
	}

	public EnumSound setRepeating() {
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
		return (float)posX;
	}

	@Override
	public float getYPosF() {
		return (float)posY;
	}

	@Override
	public float getZPosF() {
		return (float)posZ;
	}

	@Override
	public AttenuationType getAttenuationType() {
		return attenuate ? AttenuationType.LINEAR : AttenuationType.NONE;
	}

}
