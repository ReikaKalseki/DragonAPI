/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMesa;
import net.minecraft.world.biome.BiomeGenMushroomIsland;
import net.minecraft.world.biome.BiomeGenMutated;
import net.minecraft.world.biome.BiomeGenRiver;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.World.ReikaBiomeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BiomeMapCommand extends DragonCommandBase {

	public static final int PACKET_COMPILE = 2048; //packet size in bytes = 4*(1+n*3)

	private static final Random rand = new Random();
	private final static HashMap<Integer, BiomeMap> activeMaps = new HashMap();

	@Override
	public void processCommand(ICommandSender ics, String[] args) {
		if (args.length < 2) {
			this.sendChatToSender(ics, EnumChatFormatting.RED.toString()+"Illegal arguments. Use [range] [resolution] <grid>.");
			return;
		}
		int range = Integer.parseInt(args[0]);
		int res = Integer.parseInt(args[1]);
		int grid = args.length >= 3 ? Integer.parseInt(args[2]) : -1;
		EntityPlayerMP ep = this.getCommandSenderAsPlayer(ics);
		int x = MathHelper.floor_double(ep.posX);
		int z = MathHelper.floor_double(ep.posZ);
		long start = System.currentTimeMillis();

		int hash = rand.nextInt();

		String name = ep.worldObj.getWorldInfo().getWorldName();
		int dim = ep.worldObj.provider.dimensionId;
		ReikaPacketHelper.sendStringIntPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGSTART.ordinal(), ep, name, hash, dim, x, z, range, res, grid);

		ArrayList<Integer> dat = new ArrayList();
		dat.add(hash);
		int n = 0;
		for (int dx = x-range; dx <= x+range; dx += res) {
			for (int dz = z-range; dz <= z+range; dz += res) {
				BiomeGenBase b = ep.worldObj.getBiomeGenForCoords(dx, dz);
				//ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGDAT.ordinal(), ep, hash, dx, dz, b.biomeID);
				n++;
				dat.add(dx);
				dat.add(dz);
				dat.add(b.biomeID);
				if (n >= PACKET_COMPILE) {
					ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGDAT.ordinal(), ep, dat);
					n = 0;
					dat.clear();
					dat.add(hash);
				}
			}
		}
		//in case leftover
		if (dat.size() > 1) {
			//pad to fit normal packet size expectation
			int m = (dat.size()-1)/3;
			BiomeGenBase b = ep.worldObj.getBiomeGenForCoords(x, z);
			for (int i = m; i < PACKET_COMPILE; i++) {
				dat.add(x);
				dat.add(z);
				dat.add(b.biomeID);
			}

			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGDAT.ordinal(), ep, dat);
			n = 0;
			dat.clear();
			dat.add(hash);
		}

		ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, PacketIDs.BIOMEPNGEND.ordinal(), ep, hash);
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
	public static void startCollecting(int hash, String world, int dim, int x, int z, int range, int res, int grid) {
		BiomeMap map = new BiomeMap(world, dim, x, z, range, res, grid);
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

	private static class BiomeMap {

		private final String worldName;
		private final int dimensionID;

		private final int originX;
		private final int originZ;
		private final int range;

		private final int resolution;
		private final int gridSize;

		private final long startTime;

		private final int[][] data;

		private BiomeMap(String name, int dim, int x, int z, int r, int res, int grid) {
			startTime = System.currentTimeMillis();

			worldName = name;
			dimensionID = dim;

			originX = x;
			originZ = z;
			range = r;
			resolution = res;
			gridSize = grid;

			data = new int[range*2/resolution+1][range*2/resolution+1];
		}

		private void addGrid() {
			if (gridSize > 0) {
				for (int dx = originX-range; dx <= originX+range; dx += resolution) {
					for (int dz = originZ-range; dz <= originZ+range; dz += resolution) {
						int i = (range+(dx-originX))/resolution;
						int k = (range+(dz-originZ))/resolution;
						int i2 = dx-originX;
						int k2 = dz-originZ;
						if (i2%gridSize == 0 && k2%gridSize == 0) {
							data[i][k] = ReikaColorAPI.mixColors(data[i][k], i2 == 0 && k2 == 0 ? 0xffff0000 : 0xffffffff, 0.25F);
							if (i-1 >= 0)
								data[i-1][k] = ReikaColorAPI.mixColors(data[i-1][k], 0xff000000, 0.5F);
							if (i+1 < data.length)
								data[i+1][k] = ReikaColorAPI.mixColors(data[i+1][k], 0xff000000, 0.5F);
							if (k-1 >= 0)
								data[i][k-1] = ReikaColorAPI.mixColors(data[i][k-1], 0xff000000, 0.5F);
							if (k+1 < data[i].length)
								data[i][k+1] = ReikaColorAPI.mixColors(data[i][k+1], 0xff000000, 0.5F);
						}
					}
				}
			}
		}

		@SideOnly(Side.CLIENT)
		private void addPoint(int x, int z, int biomeID) {
			int c = 0xff000000 | this.getBiomeColor(Minecraft.getMinecraft().theWorld, x, z, BiomeGenBase.biomeList[biomeID]);
			int i = (range+(x-originX))/resolution;
			int k = (range+(z-originZ))/resolution;
			data[i][k] = c;
		}

		@SideOnly(Side.CLIENT)
		private String createImage() throws IOException {
			String name = this.getFilename();
			File f = new File(DragonAPICore.getMinecraftDirectory(), name);
			if (f.exists())
				f.delete();
			f.getParentFile().mkdirs();
			f.createNewFile();
			BufferedImage img = new BufferedImage(data.length, data.length, BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < data.length; i++) {
				for (int k = 0; k < data[i].length; k++) {
					img.setRGB(i, k, data[i][k]);
				}
			}
			ImageIO.write(img, "png", f);
			return f.getAbsolutePath();
		}

		private String getFilename() {
			String sr = String.valueOf(range*2+1);
			return "BiomeMap/"+worldName+"/DIM"+dimensionID+"/"+originX+", "+originZ+" ("+sr+"x"+sr+"; [R="+resolution+" b-px, G="+gridSize+"]).png";
		}

		@SideOnly(Side.CLIENT)
		private int getBiomeColor(World world, int x, int z, BiomeGenBase b) {
			if (b == null)
				return 0x000000; //should never happen

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

			if (ReikaBiomeHelper.isSnowBiome(b)) {
				return 0xffffff;
			}

			int c = b.getBiomeGrassColor(x, 64, z);

			if (mutate) {
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 0.875F);
			}
			else if (ReikaBiomeHelper.isChildBiome(b)) {
				c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, 1.125F);
			}

			return c;
		}

	}

}
