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

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.IO.DirectResourceManager;

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

	public void stream(MusicFinishedCallback call) {
		Thread t = new MusicStreamer(call);
		t.start();
	}

	public boolean resourceExists() {
		return DragonAPIInit.class.getClassLoader().getResourceAsStream(path) != null;
	}

	public static interface MusicFinishedCallback {

		public void onFinishedMusicTrack();

	}

	private class MusicStreamer extends Thread {

		private final MusicFinishedCallback call;

		public MusicStreamer(MusicFinishedCallback c) {
			call = c;
		}

		@Override
		public void run() {
			AudioInputStream audioInputStream = this.verifyInputStream();
			if (audioInputStream == null) {
				return;
			}

			AudioFormat format = audioInputStream.getFormat();
			SourceDataLine audioLine = this.openInputStream(format);

			if (audioLine != null) {
				audioLine.start();
				this.playInputStream(audioInputStream, audioLine);
			}
		}

		private AudioInputStream verifyInputStream() {
			AudioInputStream audioInputStream = null;
			try {
				audioInputStream = AudioSystem.getAudioInputStream(DirectResourceManager.getInstance().getResource(res).getInputStream());
			}
			catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
				return null;
			}
			catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return audioInputStream;
		}

		private SourceDataLine openInputStream(AudioFormat format) {
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
			SourceDataLine audioLine = null;
			try {
				audioLine = (SourceDataLine) AudioSystem.getLine(info);
				audioLine.open(format);
			}
			catch (LineUnavailableException e) {
				e.printStackTrace();
				return null;
			}
			catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return audioLine;
		}

		private void playInputStream(AudioInputStream audioInputStream, SourceDataLine audioLine) {
			int externalBufferSize = (int) audioInputStream.getFrameLength() * 4;
			int nBytesRead = 0;
			byte[] abData = new byte[externalBufferSize];

			try {
				while (nBytesRead != -1) {
					nBytesRead = audioInputStream.read(abData, 0, abData.length);
					if (nBytesRead >= 0) {
						audioLine.write(abData, 0, nBytesRead);
					}
				}
				call.onFinishedMusicTrack();
			}
			catch (IOException e) {
				e.printStackTrace();
				return;
			}
			finally {
				audioLine.drain();
				audioLine.close();
			}
		}

	}

}
