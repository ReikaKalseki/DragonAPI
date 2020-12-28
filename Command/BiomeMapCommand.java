/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMesa;
import net.minecraft.world.biome.BiomeGenMushroomIsland;
import net.minecraft.world.biome.BiomeGenMutated;
import net.minecraft.world.biome.BiomeGenRiver;
import net.minecraft.world.biome.WorldChunkManager;

import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.IO.MapOutput;
import Reika.DragonAPI.Interfaces.CustomBiomeDistributionWorld;
import Reika.DragonAPI.Interfaces.CustomMapColorBiome;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BiomeMapCommand extends DragonCommandBase {

	public static final int PACKET_COMPILE = 2048; //packet size in bytes = 4*(1+n*3)

	private static BiomeMapCommand instance;

	private static final Random rand = new Random();
	private final static HashMap<Integer, BiomeMap> activeMaps = new HashMap();

	public BiomeMapCommand() {
		instance = this;
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		Object[] ret = this.getPlayer(ics, args);
		Collection<BiomeProvider> set = new ArrayList();
		if (args[0].toLowerCase(Locale.ENGLISH).startsWith("seed=")) {
			args[0] = args[0].substring(5);
			if (args[0].contains(",")) {
				String[] parts = args[0].split(",");
				for (String s : parts) {
					set.add(new SeedBiomes(Long.parseLong(s)));
				}
			}
			else if (args[0].charAt(0) != '-' && args[0].contains("-")) {
				String[] parts = args[0].split("\\-");
				long s1 = Long.parseLong(parts[0]);
				long s2 = Long.parseLong(parts[1]);
				for (long seed = s1; seed <= s2; seed++) {
					set.add(new SeedBiomes(seed));
				}
			}
			else {
				set.add(new SeedBiomes(Long.parseLong(args[0])));
			}
			String[] nargs = new String[args.length-1];
			System.arraycopy(args, 1, nargs, 0, nargs.length);
			args = nargs;
		}
		EntityPlayerMP ep = (EntityPlayerMP)ret[0];
		if ((boolean)ret[1]) {
			String[] nargs = new String[args.length-1];
			System.arraycopy(args, 1, nargs, 0, nargs.length);
			args = nargs;
		}
		if (args.length < 2) {
			this.sendChatToSender(ics, EnumChatFormatting.RED.toString()+"Illegal arguments. Use [seed=<seed>] [range] [resolution] <grid> <fullGrid>.");
			return;
		}
		int range = Integer.parseInt(args[0]);
		int res = Integer.parseInt(args[1]);
		int grid = args.length >= 3 ? Integer.parseInt(args[2]) : -1;
		boolean fullGrid = grid > 0 && args.length >= 4 && Boolean.parseBoolean(args[3]);
		int x = MathHelper.floor_double(ep.posX);
		int z = MathHelper.floor_double(ep.posZ);
		long start = System.currentTimeMillis();

		if (set.isEmpty())
			set.add(new WorldBiomes(ep.worldObj));

		for (BiomeProvider bp : set)
			this.generateMap(bp, ep, start, x, z, range, res, grid, fullGrid, null);
	}

	public static void triggerBiomeMap(EntityPlayerMP ep, int x, int z, int range, int res, int grid, MapCompleteCallback call) {
		instance.generateMap(new WorldBiomes(ep.worldObj), ep, System.currentTimeMillis(), x, z, range, res, grid, false, call);
	}

	public static void triggerBiomeMap(EntityPlayerMP ep, int range, int res, int grid) {
		instance.processCommand(ep, new String[] {String.valueOf(range), String.valueOf(res), String.valueOf(grid)});
	}

	private void generateMap(BiomeProvider bp, EntityPlayerMP ep, long start, int x, int z, int range, int res, int grid, boolean fullGrid, MapCompleteCallback callback) {
		int hash = rand.nextInt();

		int dim = bp instanceof WorldBiomes ? 0 : ep.worldObj.provider.dimensionId;
		if (DragonAPICore.isSinglePlayer()) {
			this.startCollecting(hash, bp.getName(), dim, x, z, range, res, grid, fullGrid);
		}
		else {
			ReikaPacketHelper.sendStringIntPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGSTART.ordinal(), ep, bp.getName(), hash, dim, x, z, range, res, grid, fullGrid ? 1 : 0);
		}

		ArrayList<Integer> dat = new ArrayList();
		dat.add(hash);
		int n = 0;
		for (int dx = x-range; dx <= x+range; dx += res) {
			for (int dz = z-range; dz <= z+range; dz += res) {
				int biome = bp.getBiome(dx, dz);
				//ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGDAT.ordinal(), ep, hash, dx, dz, b.biomeID);
				n++;
				if (DragonAPICore.isSinglePlayer()) {
					this.addBiomePoint(hash, dx, dz, biome);
				}
				else {
					dat.add(dx);
					dat.add(dz);
					dat.add(biome);
				}
				if (n >= PACKET_COMPILE) {
					if (DragonAPICore.isSinglePlayer()) {

					}
					else {
						ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGDAT.ordinal(), ep, dat);
						n = 0;
						dat.clear();
						dat.add(hash);
					}
				}
			}
		}
		//in case leftover
		if (dat.size() > 1) {
			//pad to fit normal packet size expectation
			int m = (dat.size()-1)/3;
			int biome = bp.getBiome(x, z);
			if (DragonAPICore.isSinglePlayer()) {
				this.addBiomePoint(hash, x, z, biome);
			}
			else {
				for (int i = m; i < PACKET_COMPILE; i++) {
					dat.add(x);
					dat.add(z);
					dat.add(biome);
				}
				ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGDAT.ordinal(), ep, dat);
				n = 0;
				dat.clear();
				dat.add(hash);
			}
		}
		if (callback != null) {
			callback.onComplete();
			callback = null;
		}
		if (DragonAPICore.isSinglePlayer()) {
			this.finishCollectingAndMakeImage(hash);
		}
		else {
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGEND.ordinal(), ep, hash);
		}
	}

	private Object[] getPlayer(ICommandSender ics, String[] args) {
		try {
			return new Object[]{this.getCommandSenderAsPlayer(ics), false};
		}
		catch (Exception e) {
			EntityPlayerMP ep = ReikaPlayerAPI.getPlayerByNameAnyWorld(args[0]);
			if (ep == null) {
				this.sendChatToSender(ics, "If you specify a player, they must exist.");
				throw new IllegalArgumentException(e);
			}
			return new Object[]{ep, true};
		}
	}

	@Override
	public String getCommandString() {
		return "biomepng";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public static void startCollecting(int hash, String world, int dim, int x, int z, int range, int res, int grid, boolean fullGrid) {
		BiomeMap map = new BiomeMap(world, dim, x, z, range, res, grid, fullGrid);
		activeMaps.put(hash, map);
	}

	@SideOnly(Side.CLIENT)
	public static void addBiomePoint(int hash, int x, int z, int biomeID) {
		BiomeMap map = activeMaps.get(hash);
		if (map != null) {
			map.addPoint(x, z, biomeID);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void finishCollectingAndMakeImage(int hash) {
		BiomeMap map = activeMaps.remove(hash);
		if (map != null) {
			try {
				map.addGrid();
				String path = map.createImage();
				long dur = System.currentTimeMillis()-map.startTime;
				ReikaChatHelper.sendChatToPlayer(Minecraft.getMinecraft().thePlayer, EnumChatFormatting.GREEN+"File created in "+dur+" ms: "+path);
			}
			catch (IOException e) {
				ReikaChatHelper.sendChatToPlayer(Minecraft.getMinecraft().thePlayer, EnumChatFormatting.RED+"Failed to create file: "+e.toString());
				e.printStackTrace();
			}
		}
	}

	private static interface BiomeProvider {

		//public String getFileName(long seed, String name, int x, int z, int range, int res, int grid, boolean fullGrid);
		public int getBiome(int x, int z);
		public String getName();

	}

	private static class WorldBiomes implements BiomeProvider {

		private final World world;

		private WorldBiomes(World world) {
			this.world = world;
		}
		/*
		@Override
		public String getFileName(long seed, String name, int x, int z, int range, int res, int grid, boolean fullGrid) {

		}*/

		@Override
		public int getBiome(int x, int z) {
			if (world.provider instanceof CustomBiomeDistributionWorld) {
				return ((CustomBiomeDistributionWorld)world.provider).getBiomeID(world, x, z);
			}
			return world.getWorldChunkManager().getBiomeGenAt(x, z).biomeID;
		}

		@Override
		public String getName() {
			return world.getWorldInfo().getWorldName()+"/["+world.getSaveHandler().getWorldDirectoryName()+"]";
		}

	}

	private static class SeedBiomes implements BiomeProvider {

		private final long seed;
		private final WorldChunkManager world;

		private SeedBiomes(long seed) {
			this.seed = seed;
			world = new WorldChunkManager(seed, WorldType.DEFAULT);
		}
		/*
		@Override
		public String getFileName(long seed, String name, int x, int z, int range, int res, int grid, boolean fullGrid) {
			return "BiomeMap/Forced/"+worldName+"; "+x+", "+z+" ("+sr+"x"+sr+"; [R="+res+" b-px, G="+grid+"-"+fullGrid+"]).png";
		}*/

		@Override
		public int getBiome(int x, int z) {
			return world.getBiomeGenAt(x, z).biomeID;
		}

		@Override
		public String getName() {
			return "SEED="+seed;
		}

	}

	private static class BiomeMap extends MapOutput<Integer> {

		private BiomeMap(String name, int dim, int x, int z, int r, int res, int grid, boolean fgrid) {
			super(name, dim, x, z, r, res, grid, fgrid);
		}

		@Override
		protected void onImageCreate(File f) throws IOException {
			this.createLegend(f);
		}

		@Override
		protected int getColor(int x, int z, Integer data) {
			return this.getBiomeColor(x, z, BiomeGenBase.biomeList[data.intValue()]);
		}

		private void createLegend(File f) throws IOException {
			File f2 = new File(f.getParentFile(), "!legend.png");
			if (f2.exists() && !ReikaObfuscationHelper.isDeObfEnvironment())
				return;
			f2.createNewFile();

			MultiMap<Integer, Integer> li = ReikaBiomeHelper.getBiomeHierearchy();
			int heightPerBiome = 18;
			int height = (4+heightPerBiome)*(1+li.totalSize()+li.keySet().size());
			//height = ReikaMathLibrary.ceil2PseudoExp(height);

			BufferedImage img = new BufferedImage(256, height, BufferedImage.TYPE_INT_ARGB);

			Graphics graphics = img.getGraphics();
			Font ft = graphics.getFont();
			graphics.setFont(new Font(ft.getName(), ft.getStyle(), ft.getSize()*1));
			graphics.setColor(new Color(0xff000000));
			int y = 2;
			for (Integer b : li.keySet()) {
				this.createLegendEntry(b, 2, y, graphics, img, heightPerBiome);
				y += heightPerBiome+4;
				for (Integer b2 : li.get(b)) {
					this.createLegendEntry(b2, 24, y, graphics, img, heightPerBiome);
					y += heightPerBiome+4;
				}
			}
			graphics.dispose();

			ImageIO.write(img, "png", f2);
		}

		private void createLegendEntry(int b, int x, int y, Graphics g, BufferedImage img, int hpb) {
			BiomeGenBase biome = BiomeGenBase.biomeList[b];
			g.drawString(biome.biomeName+" ["+biome.biomeID+"]", x+hpb+4, y+hpb/2+4);
			for (int i = -1; i <= hpb; i++) {
				for (int k = -1; k <= hpb; k++) {
					int clr = i == -1 || k == -1 || i == hpb || k == hpb ? 0xff000000 : 0xff000000 | this.getBiomeColor(i*12, k*12, biome);
					img.setRGB(x+i, y+k, clr);
				}
			}
		}

		@SideOnly(Side.CLIENT)
		private int getBiomeColor(int x, int z, BiomeGenBase b) {
			if (b == null)
				return 0x000000; //should never happen

			if (b instanceof CustomMapColorBiome)
				return ((CustomMapColorBiome)b).getMapColor(Minecraft.getMinecraft().theWorld, x, z);

			boolean mutate = b instanceof BiomeGenMutated;
			if (mutate) {
				b = ((BiomeGenMutated)b).baseBiome;
			}

			if (b == BiomeGenBase.hell) {
				return 0xC12603;
			}
			if (b == BiomeGenBase.sky) {
				return 0xFFE9A3;
			}

			if (b == BiomeGenBase.frozenOcean) {
				return 0x00ffff;
			}
			if (b.biomeID == BiomeGenBase.icePlains.biomeID+128) { //Ice Spikes
				return 0x7FFFFF;
			}
			if (b == BiomeGenBase.iceMountains) {
				return 0xd0d0d0;
			}

			//Because some BoP forests secretly identify as ocean-kin
			if (b.biomeName.equalsIgnoreCase("Shield")) {
				return 0x387F4D;
			}
			else if (b.biomeName.equalsIgnoreCase("Tropics")) {
				return 0x00ff00;
			}
			else if (b.biomeName.equalsIgnoreCase("Lush Swamp")) {
				return 0x009000;
			}
			else if (b.biomeName.equalsIgnoreCase("Bayou")) {
				return 0x7B7F4F; //Eew
			}
			else if (ReikaBiomeHelper.isOcean(b)) {
				if (b == BiomeGenBase.deepOcean)
					return 0x0000b0;
				return 0x0000ff;
			}

			if (b instanceof BiomeGenRiver)
				return 0x22aaff;

			if (b instanceof BiomeGenMesa) {
				return mutate ? 0xCE7352 : 0xC4542B;
			}

			if (b instanceof BiomeGenMushroomIsland) {
				return 0x965471;
			}

			if (b == BiomeGenBase.megaTaiga || b == BiomeGenBase.megaTaigaHills) {
				return 0x9B6839;
			}

			if (b.topBlock == Blocks.sand) {
				return 0xE2C995;
			}
			if (b.topBlock == Blocks.stone) {
				return 0x808080;
			}

			if (b.biomeName.equalsIgnoreCase("Coniferous Forest")) {
				return 0x007F42;
			}
			if (b.biomeName.equalsIgnoreCase("Maple Forest")) {
				return 0x3A7F52;
			}
			if (b.biomeName.equalsIgnoreCase("Rainbow Forest")) {
				return 0x8888FF;
			}
			if (b.biomeName.equalsIgnoreCase("Ender Forest")) {
				return 0xC872DB;
			}
			if (b.biomeName.equalsIgnoreCase("Luminous Cliffs")) {
				return 0x22aaff;
			}
			if (b.biomeName.equalsIgnoreCase("Luminous Cliffs Shores")) {
				return 0x428AFF;
			}

			int c = b.getBiomeGrassColor(x, 64, z);

			if (ReikaBiomeHelper.isSnowBiome(b) && !b.biomeName.equalsIgnoreCase("Pink Birch Forest")) {
				c = 0xffffff;
			}

			if (b == BiomeGenBase.coldTaiga) {
				c = 0xADFFCB;
			}

			if (mutate) {
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.875F);
			}
			else if (ReikaBiomeHelper.isChildBiome(b)) {
				c = c == 0xffffff ? 0xd0d0d0 : ReikaColorAPI.getColorWithBrightnessMultiplier(c, 1.125F);
			}

			return c;
		}

	}

	public static interface MapCompleteCallback {

		public void onComplete();

	}

}
