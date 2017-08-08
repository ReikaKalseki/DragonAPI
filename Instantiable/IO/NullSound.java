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

import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;

/** Used to cancel sounds. */
public class NullSound implements ISound {

	@Override
	public ResourceLocation getPositionedSoundLocation() {
		return new ResourceLocation("");
	}

	@Override
	public boolean canRepeat() {
		return false;
	}

	@Override
	public int getRepeatDelay() {
		return 0;
	}

	@Override
	public float getVolume() {
		return 0;
	}

	@Override
	public float getPitch() {
		return 0;
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
		return AttenuationType.LINEAR;
	}

}
