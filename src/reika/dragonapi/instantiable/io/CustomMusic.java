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

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import reika.dragonapi.DragonAPIInit;
import reika.dragonapi.io.DirectResourceManager;

public class CustomMusic implements ISound {

	public final float volume;
	public final float pitch;

	public final String path;
	private final ResourceLocation res;

	private boolean repeat = false;

	private float posX;
	private float posY;
	private float posZ;

	public CustomMusic(String path) {
		this(path, 1, 1);
	}

	public CustomMusic(String path, float vol, float p) {
		this.path = path;
		res = DirectResourceManager.getResource(path);
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
		return posX;
	}

	@Override
	public float getYPosF() {
		return posY;
	}

	@Override
	public float getZPosF() {
		return posZ;
	}

	@Override
	public AttenuationType getAttenuationType() {
		return AttenuationType.NONE;
	}

	public void play(SoundHandler sh) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		posX = (float)ep.posX;
		posY = (float)ep.posY;
		posZ = (float)ep.posZ;
		sh.playSound(this);
	}

	public boolean resourceExists() {
		return DragonAPIInit.class.getClassLoader().getResourceAsStream(path) != null;
	}

}
