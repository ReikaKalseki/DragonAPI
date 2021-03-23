/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.IO;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.IO.EnumSound;
import Reika.DragonAPI.Instantiable.IO.SingleSound;
import Reika.DragonAPI.Instantiable.IO.SoundVariant;
import Reika.DragonAPI.Interfaces.Registry.SoundEnum;
import Reika.DragonAPI.Interfaces.Registry.VariableSound;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.StreamThread;

public class ReikaSoundHelper {

	private static final MultiMap<SoundEnum, SoundPlay> plays = new MultiMap();
	private static final HashMap<Class, SoundEnumSet> soundSets = new HashMap();
	private static final HashMap<Integer, Class> soundSetIDs = new HashMap();

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

	public static void playSound(SoundEnum s, World world, Entity e, float vol, float pitch) {
		playSound(s, world, e.posX, e.posY, e.posZ, vol, pitch);
	}

	public static void playSound(SoundEnum s, World world, double x, double y, double z, float vol, float pitch) {
		playSound(s, world, x, y, z, vol, pitch, s.attenuate());
	}

	public static void playSound(SoundEnum s, World world, double x, double y, double z, float vol, float pitch, boolean atten) {
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
		sendSound(s, world, x, y, z, vol, pitch, atten);
	}

	private static void sendSound(SoundEnum s, World world, double x, double y, double z, float vol, float pitch, boolean atten) {
		ReikaPacketHelper.sendSoundPacket(s, world, x, y, z, vol, pitch, atten);
	}

	@SideOnly(Side.CLIENT)
	public static ISound playClientSound(SoundEnum s, double x, double y, double z, float vol, float pitch) {
		return playClientSound(s, x, y, z, vol, pitch, true);
	}

	@SideOnly(Side.CLIENT)
	public static ISound playClientSound(SoundEnum s, double x, double y, double z, float vol, float pitch, boolean att) {
		float v = vol*s.getModulatedVolume();
		if (v <= 0)
			return null;
		EnumSound es = new EnumSound(s, x, y, z, v, pitch, att);
		try {
			FMLClientHandler.instance().getClient().getSoundHandler().playSound(es);
		}
		catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
		return es;
	}

	@SideOnly(Side.CLIENT)
	public static void playClientSound(String snd, double x, double y, double z, float vol, float pitch, boolean atten) {
		Minecraft.getMinecraft().theWorld.playSound(x, y, z, snd, vol, pitch, atten);
	}

	@SideOnly(Side.CLIENT)
	public static ISound playClientSound(SoundEnum s, Entity e, float vol, float pitch, boolean att) {
		return playClientSound(s, e.posX, e.posY, e.posZ, vol, pitch, att);
	}

	@SideOnly(Side.CLIENT)
	public static void playClientSound(SoundEnum s, Entity e, float vol, float pitch) {
		playClientSound(s, e.posX, e.posY, e.posZ, vol, pitch, true);
	}

	@SideOnly(Side.CLIENT)
	public static void playNormalClientSound(World world, double x, double y, double z, String name, float vol, float pitch, boolean flag) {
		world.playSound(x, y, z, name, vol, pitch, flag);
	}

	public static void broadcastSound(SoundEnum s, float vol, float pitch) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			throw new MisuseException("You cannot call this from the client!");
		World[] worlds = DimensionManager.getWorlds();
		for (World world : worlds) {
			for (EntityPlayer ep : (List<EntityPlayer>)world.playerEntities) {
				playSound(s, world, ep, vol, pitch);
			}
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
		soundSetIDs.put(0, SingleSound.class);
		SingleSoundSet set = new SingleSoundSet();
		soundSets.put(SingleSound.class, set);
	}

	@SideOnly(Side.CLIENT)
	private static void doClientInit() {
		musicTypes = new HashMap();
		for (MusicType type : MusicType.values()) {
			musicTypes.put(type.getMusicTickerLocation(), type);
		}
	}

	public static SoundEnumSet getSoundLibrary(SoundEnum s) {
		SoundEnumSet set = soundSets.get(s.getClass());
		if (set == null) {
			DragonAPICore.logError("Tried to play an unregistered sound '"+s.getClass()+" "+s+"'!");
		}
		return set;
	}

	public static SoundEnum lookupSound(int lib, int idx) {
		Class type = soundSetIDs.get(lib);
		SoundEnumSet set = soundSets.get(type);
		if (type == null || set == null) {
			DragonAPICore.logError("Tried to play an unregistered sound!");
			return null;
		}
		return set.getSound(idx);
	}

	public static void registerSoundSet(Class<? extends SoundEnum> c) {
		if (c == SingleSound.class) {
			throw new IllegalArgumentException("You cannot register single sounds as a set!");
		}
		else {
			SoundEnumSet set = soundSets.get(c);
			if (set != null) {
				;//throw new IllegalArgumentException("Sound set "+c+" already registered!");
			}
			int idx = set != null ? set.index : soundSets.size();
			soundSetIDs.put(idx, c);
			if (set == null) {
				if (VariableSound.class.isAssignableFrom(c))
					set = new SoundEnumSetWithVariants((Class<? extends VariableSound>)c, idx);
				else
					set = new SoundEnumSet(c, idx);
			}
			soundSets.put(c, set);
			if (set instanceof SoundEnumSetWithVariants) {
				for (Class c2 : ((SoundEnumSetWithVariants)set).variantClasses) {
					soundSets.put(c2, set);
				}
			}
			ReikaJavaLibrary.pConsole("Registered sound set of type "+c+" with values "+Arrays.toString(set.sounds));
		}
	}

	public static void registerSingleSound(SingleSound s) {
		SingleSoundSet set = (SingleSoundSet)soundSets.get(SingleSound.class);
		set.addSound(s);
	}

	private static class SoundPlay {

		private final long time;
		private final DecimalPosition loc;

		private SoundPlay(long t, double x, double y, double z) {
			time = t;
			loc = new DecimalPosition(x, y, z);
		}

	}

	private static class SingleSoundSet extends SoundEnumSet {

		private final ArrayList<SingleSound> soundList = new ArrayList();

		private SingleSoundSet() {
			super(SingleSound.class, 0);
		}

		private void addSound(SingleSound s) {
			soundList.add(s);
		}

		@Override
		protected SoundEnum getSound(int idx) {
			return soundList.get(idx);
		}

		@Override
		public int getSoundIndex(SoundEnum s) {
			return soundList.indexOf(s);
		}

	}

	private static class SoundEnumSetWithVariants extends SoundEnumSet {

		private static final Comparator<SoundVariant> sorter = new Comparator<SoundVariant>() {

			@Override
			public int compare(SoundVariant o1, SoundVariant o2) {
				return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
			}

		};

		private final HashMap<SoundEnum, SoundVariant[]> variants = new HashMap();
		private final HashSet<Class> variantClasses = new HashSet();

		private SoundEnumSetWithVariants(Class<? extends VariableSound> c, int idx) {
			super(c, idx);

			for (SoundEnum e : sounds) {
				VariableSound v = (VariableSound)e;
				Collection<SoundVariant> cv = v.getVariants();
				if (cv != null && !cv.isEmpty()) {
					SoundVariant[] arr = cv.toArray(new SoundVariant[cv.size()]);
					Arrays.sort(arr, sorter);
					variants.put(e, arr);
					for (SoundVariant sv : arr) {
						variantClasses.add(sv.getClass());
					}
				}
			}
		}

		@Override
		protected SoundEnum getSound(int idx) {
			int base = idx & 32767;
			int variant = (idx >> 16) & 32767;
			SoundEnum e = super.getSound(base);
			if (variant > 0) {
				SoundVariant[] arr = variants.get(e);
				e = arr[variant-1];
			}
			return e;
		}

		@Override
		public int getSoundIndex(SoundEnum s) {
			SoundEnum parent = s;
			boolean var = s instanceof SoundVariant;
			if (var) {
				parent = ((SoundVariant)s).root;
			}
			int val = super.getSoundIndex(parent);
			if (var) {
				SoundVariant[] arr = variants.get(parent);
				int offset = arr == null ? 0 : 1+ReikaArrayHelper.indexOf(arr, s);
				if (offset == 0) {
					DragonAPICore.logError("Could not find variant index for "+s+" in "+Arrays.toString(arr)+" from "+parent);
				}
				val |= (offset << 16);
			}
			return val;
		}

	}

	public static class SoundEnumSet {

		public final int index;
		public final Class<? extends SoundEnum> enumClass;
		protected final SoundEnum[] sounds;

		private SoundEnumSet(Class<? extends SoundEnum> c, int idx) {
			enumClass = c;
			sounds = c.getEnumConstants();
			index = idx;
		}

		protected SoundEnum getSound(int idx) {
			return sounds[idx];
		}

		public int getSoundIndex(SoundEnum s) {
			return s.ordinal();
		}

	}
}
