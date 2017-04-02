/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.StreamThread;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Auxiliary.Trackers.CustomSoundHandler;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.IO.EnumSound;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ReikaSoundHelper {

	private static final MultiMap<SoundEnum, SoundPlay> plays = new MultiMap();

	private static Field soundLibraryField;
	private static Field streamThreadField;

	@SideOnly(Side.CLIENT)
	private static HashMap<ResourceLocation, MusicType> musicTypes;

	public static void playBreakSound(World world, int x, int y, int z, Block b) {
		SoundType s = b.stepSound;
		String f = s.getBreakSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume(), s.getPitch());
	}

	public static void playStepSound(World world, int x, int y, int z, Block b) {
		SoundType s = b.stepSound;
		String f = s.getStepResourcePath();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume(), s.getPitch());
	}

	public static void playPlaceSound(World world, int x, int y, int z, Block b) {
		SoundType s = b.stepSound;
		String f = s.getStepResourcePath();//s.getPlaceSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume(), s.getPitch());
	}

	public static void playBreakSound(World world, int x, int y, int z, Block b, float vol, float pitch) {
		SoundType s = b.stepSound;
		String f = s.getBreakSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume()*vol, s.getPitch()*pitch);
	}

	public static void playStepSound(World world, int x, int y, int z, Block b, float vol, float pitch) {
		SoundType s = b.stepSound;
		String f = s.getStepResourcePath();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume()*vol, s.getPitch()*pitch);
	}

	public static void playPlaceSound(World world, int x, int y, int z, Block b, float vol, float pitch) {
		SoundType s = b.stepSound;
		String f = s.getStepResourcePath();//s.getPlaceSound();
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, f, s.getVolume()*vol, s.getPitch()*pitch);
	}

	public static void playSoundAtBlock(World world, int x, int y, int z, String snd, float vol, float pit) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, snd, vol, pit);
	}

	public static void playSoundAtBlock(World world, int x, int y, int z, String snd) {
		world.playSoundEffect(x+0.5, y+0.5, z+0.5, snd, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSoundAtBlock(String file, TileEntity te) {
		playCustomSoundAtBlock(file, te.xCoord, te.yCoord, te.zCoord);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSoundAtBlock(String file, int x, int y, int z) {
		playCustomSoundAtBlock(file, x, y, z, 1, 1);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSoundAtBlock(String file, int x, int y, int z, float vol, float pitch) {
		playCustomSound(file, x+0.5, y+0.5, z+0.5, vol, pitch);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSound(String file, Entity e, float vol, float pitch) {
		playCustomSound(file, e.posX, e.posY, e.posZ, vol, pitch);
	}

	@SideOnly(Side.CLIENT)
	public static void playCustomSound(String file, double x, double y, double z, float vol, float pitch) {
		ResourceLocation rl = CustomSoundHandler.instance.getSoundResource(file);
		try {
			FMLClientHandler.instance().getClient().getSoundHandler().playSound(new PositionedSoundRecord(rl, (float)x, (float)y, (float)z, vol, pitch));
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}

	public static void playSound(SoundEnum s, String ch, World world, Entity e, float vol, float pitch) {
		playSound(s, ch, world, e.posX, e.posY, e.posZ, vol, pitch);
	}

	public static void playSound(SoundEnum s, String ch, World world, double x, double y, double z, float vol, float pitch) {
		playSound(s, ch, world, x, y, z, vol, pitch, s.attenuate());
	}

	public static void playSound(SoundEnum s, String ch, World world, double x, double y, double z, float vol, float pitch, boolean atten) {
		long time = world.getTotalWorldTime();
		if (!s.canOverlap()) {
			Collection<SoundPlay> c = plays.get(s);
			Iterator<SoundPlay> it = c.iterator();
			while (it.hasNext()) {
				SoundPlay p = it.next();
				if (time-p.time < 20) { //1s for now
					if (p.loc.getDistanceTo(x, y, z) < 12 && !p.loc.sharesBlock(x, y, z))
						return;
				}
				else {
					it.remove();
				}
			}
			plays.addValue(s, new SoundPlay(time, x, y, z));
		}
		sendSound(ch, s, world, x, y, z, vol, pitch, atten);
	}

	private static void sendSound(String ch, SoundEnum s, World world, double x, double y, double z, float vol, float pitch, boolean atten) {
		ReikaPacketHelper.sendSoundPacket(ch, s, world, x, y, z, vol, pitch, atten);
	}

	@SideOnly(Side.CLIENT)
	public static void playClientSound(SoundEnum s, double x, double y, double z, float vol, float pitch) {
		playClientSound(s, x, y, z, vol, pitch, true);
	}

	@SideOnly(Side.CLIENT)
	public static void playClientSound(SoundEnum s, double x, double y, double z, float vol, float pitch, boolean att) {
		float v = vol*s.getModulatedVolume();
		if (v <= 0)
			return;
		try {
			FMLClientHandler.instance().getClient().getSoundHandler().playSound(new EnumSound(s, x, y, z, v, pitch, att));
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	public static void playClientSound(String snd, double x, double y, double z, float vol, float pitch, boolean atten) {
		Minecraft.getMinecraft().theWorld.playSound(x, y, z, snd, vol, pitch, atten);
	}

	@SideOnly(Side.CLIENT)
	public static void playClientSound(SoundEnum s, Entity e, float vol, float pitch, boolean att) {
		playClientSound(s, e.posX, e.posY, e.posZ, vol, pitch, att);
	}

	@SideOnly(Side.CLIENT)
	public static void playClientSound(SoundEnum s, Entity e, float vol, float pitch) {
		playClientSound(s, e.posX, e.posY, e.posZ, vol, pitch, true);
	}

	@SideOnly(Side.CLIENT)
	public static void playNormalClientSound(World world, double x, double y, double z, String name, float vol, float pitch, boolean flag) {
		world.playSound(x, y, z, name, vol, pitch, flag);
	}

	public static void broadcastSound(SoundEnum s, String ch, float vol, float pitch) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			throw new MisuseException("You cannot call this from the client!");
		World[] worlds = DimensionManager.getWorlds();
		for (World world : worlds) {
			for (EntityPlayer ep : (List<EntityPlayer>)world.playerEntities) {
				playSound(s, ch, world, ep, vol, pitch);
			}
		}

	}

	private static class SoundPlay {

		private final long time;
		private final DecimalPosition loc;

		private SoundPlay(long t, double x, double y, double z) {
			time = t;
			loc = new DecimalPosition(x, y, z);
		}

	}

	public static void playSoundAtEntity(World world, Entity e, String snd) {
		playSoundAtEntity(world, e, snd, 1, 1);
	}

	public static void playSoundAtEntity(World world, Entity e, String snd, float vol, float p) {
		world.playSoundEffect(e.posX, e.posY, e.posZ, snd, vol, p);
	}

	public static void playSoundFromServer(World world, double x, double y, double z, String name, float vol, float pitch, boolean scale) {
		ReikaPacketHelper.writeDirectSound(DragonAPIInit.packetChannel, PacketIDs.SERVERSOUND.ordinal(), world, x, y, z, name, vol, pitch, scale);
	}

	public static void playSoundFromServerAtBlock(World world, int x, int y, int z, String name, float vol, float pitch, boolean scale) {
		playSoundFromServer(world, x+0.5, y+0.5, z+0.5, name, vol, pitch, scale);
	}

	@SideOnly(Side.CLIENT)
	public static StreamThread getStreamingThread(SoundHandler sh) {
		try {
			SoundSystem sys = sh.sndManager.sndSystem;
			Library lib = (Library)soundLibraryField.get(sys);
			StreamThread s = (StreamThread)streamThreadField.get(lib);
			return s;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** Recreates and relaunches the StreamThread. Only call this if the thread has crashed. */
	@SideOnly(Side.CLIENT)
	public static void restartStreamingSystem(SoundHandler sh) {
		DragonAPICore.log("Restarting sound streaming thread.");
		StreamThread thread = new StreamThread();
		try {
			SoundSystem sys = sh.sndManager.sndSystem;
			Library lib = (Library)soundLibraryField.get(sys);
			streamThreadField.set(lib, thread);

			thread.start();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void injectPaulscodeAccesses() {
		try {
			DragonAPICore.log("Injecting accesses into paulscode...");

			soundLibraryField = SoundSystem.class.getDeclaredField("soundLibrary");
			streamThreadField = Library.class.getDeclaredField("streamThread");

			soundLibraryField.setAccessible(true);
			streamThreadField.setAccessible(true);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SideOnly(Side.CLIENT)
	public static MusicType getMusicTypeByResourceLocation(ResourceLocation loc) {
		return musicTypes.get(loc);
	}

	static {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			doClientInit();
		}
	}

	@SideOnly(Side.CLIENT)
	private static void doClientInit() {
		musicTypes = new HashMap();
		for (MusicType type : MusicType.values()) {
			musicTypes.put(type.getMusicTickerLocation(), type);
		}
	}
}
