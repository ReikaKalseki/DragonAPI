/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Auxiliary.Trackers.CommandableUpdateChecker;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Command.EntityListCommand;
import Reika.DragonAPI.Command.IDDumpCommand;
import Reika.DragonAPI.Instantiable.Effects.NumberParticleFX;
import Reika.DragonAPI.Instantiable.Event.RawKeyPressEvent;
import Reika.DragonAPI.Instantiable.Event.Client.ClientLoginEvent;
import Reika.DragonAPI.Interfaces.PacketHandler;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.DataPacket;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class APIPacketHandler implements PacketHandler {

	private final Random rand = new Random();

	protected PacketIDs pack;

	public void handleData(PacketObj packet, World world, EntityPlayer ep) {
		DataInputStream inputStream = packet.getDataIn();
		int control = Integer.MIN_VALUE;
		int len;
		int[] data = new int[0];
		long longdata = 0;
		float floatdata = 0;
		int x = 0;
		int y = 0;
		int z = 0;
		boolean readinglong = false;
		NBTTagCompound NBT = null;
		String stringdata = null;
		//System.out.print(packet.length);
		try {
			//ReikaJavaLibrary.pConsole(inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt());
			PacketTypes packetType = packet.getType();
			switch(packetType) {
				case SOUND:
					return;
				case FULLSOUND:
					control = inputStream.readInt();
					pack = PacketIDs.getEnum(control);
					break;
				case STRING:
					stringdata = packet.readString();
					control = inputStream.readInt();
					pack = PacketIDs.getEnum(control);
					break;
				case DATA:
					control = inputStream.readInt();
					pack = PacketIDs.getEnum(control);
					len = pack.getNumberDataInts();
					data = new int[len];
					readinglong = pack.isLongPacket();
					if (!readinglong) {
						for (int i = 0; i < len; i++)
							data[i] = inputStream.readInt();
					}
					else
						longdata = inputStream.readLong();
					break;
				case UPDATE:
					control = inputStream.readInt();
					pack = PacketIDs.getEnum(control);
					break;
				case FLOAT:
					control = inputStream.readInt();
					pack = PacketIDs.getEnum(control);
					floatdata = inputStream.readFloat();
					break;
				case SYNC:
					String name = packet.readString();
					x = inputStream.readInt();
					y = inputStream.readInt();
					z = inputStream.readInt();
					int value = inputStream.readInt();
					ReikaPacketHelper.updateTileEntityData(world, x, y, z, name, value);
					return;
				case TANK:
					String tank = packet.readString();
					x = inputStream.readInt();
					y = inputStream.readInt();
					z = inputStream.readInt();
					int level = inputStream.readInt();
					ReikaPacketHelper.updateTileEntityTankData(world, x, y, z, tank, level);
					return;
				case RAW:
					control = inputStream.readInt();
					pack = PacketIDs.getEnum(control);
					len = pack.getNumberDataInts();
					data = new int[len];
					readinglong = pack.isLongPacket();
					if (!readinglong) {
						for (int i = 0; i < len; i++)
							data[i] = inputStream.readInt();
					}
					else
						longdata = inputStream.readLong();
					break;
				case NBT:
					control = inputStream.readInt();
					pack = PacketIDs.getEnum(control);
					NBT = ((DataPacket)packet).asNBT();
					break;
				case STRINGINT:
					stringdata = packet.readString();
					control = inputStream.readInt();
					pack = PacketIDs.getEnum(control);
					data = new int[pack.getNumberDataInts()];
					for (int i = 0; i < data.length; i++)
						data[i] = inputStream.readInt();
					break;
				default:
					break;
			}
			if (packetType.hasCoordinates()) {
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try {
			switch (pack) {
				case BLOCKUPDATE:
					//ReikaJavaLibrary.pConsole(x+", "+y+", "+z, Side.CLIENT);
					world.markBlockForUpdate(x, y, z);
					world.func_147479_m(x, y, z);
					break;
				case PARTICLE:
					for (int i = 0; i < data[1]; i++) {
						if (data[0] >= 0 && data[0] < ReikaParticleHelper.particleList.length) {
							ReikaParticleHelper p = ReikaParticleHelper.particleList[data[0]];
							world.spawnParticle(p.name, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), 0, 0, 0);
						}
					}
					break;
				case BIOMECHANGE:
					ReikaWorldHelper.setBiomeForXZ(world, x, z, BiomeGenBase.biomeList[data[0]]);
					world.markBlockRangeForRenderUpdate(x, 0, z, x, world.provider.getActualHeight(), z);
					break;
				case KEYUPDATE:
					int ordinal = data[0];
					boolean used = data[1] > 0;
					Key key = Key.keyList[ordinal];
					KeyWatcher.instance.setKey(ep, key, used);
					MinecraftForge.EVENT_BUS.post(new RawKeyPressEvent(key, ep));
					break;
				case TILESYNC:
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof TileEntityBase && !world.isRemote) {
						TileEntityBase tile = (TileEntityBase)te;
						tile.syncAllData(data[0] > 0);
					}
					break;
				case VTILESYNC:
					int tx = NBT.getInteger("x");
					int ty = NBT.getInteger("y");
					int tz = NBT.getInteger("z");
					TileEntity tile = world.getTileEntity(tx, ty, tz);
					//ReikaJavaLibrary.pConsole(((IInventory)tile).getStackInSlot(0));
					tile.readFromNBT(NBT);
					break;
				case TILEDELETE:
					world.setBlockToAir(x, y, z);
					break;
				case PLAYERDATSYNC:
				case PLAYERDATSYNC_CLIENT:
					for (Object o : NBT.func_150296_c()) {
						String name = (String)o;
						NBTBase tag = NBT.getTag(name);
						ep.getEntityData().setTag(name, tag);
					}
					break;/*
			case PLAYERATTRSYNC:
				for (Object o : NBT.func_150296_c()) { //Double tags
					String name = (String)o;
					NBTBase tag = NBT.getTag(name);
					BaseAttributeMap map = ep.getAttributeMap();
				}
				break;*/
				case RERENDER:
					ReikaRenderHelper.rerenderAllChunks();
					break;
				case COLOREDPARTICLE:
					ReikaParticleHelper.spawnColoredParticlesWithOutset(world, x, y, z, data[0], data[1], data[2], data[3], data[4]/16D);
					break;
				case NUMBERPARTICLE:
					break;
				case IDDUMP:
				case ENTITYDUMP:
					break;
				case EXPLODE:
					break;
				case OLDMODS:
					break;
				case LOGIN:
					break;
				case SERVERSOUND:
					if (world.isRemote) {
						double dx = inputStream.readDouble();
						double dy = inputStream.readDouble();
						double dz = inputStream.readDouble();
						String name = packet.readString();
						float vol = inputStream.readFloat();
						float pitch = inputStream.readFloat();
						boolean flag = inputStream.readBoolean();
						ReikaSoundHelper.playNormalClientSound(world, dx, dy, dz, name, vol, pitch, flag);
					}
					break;
				case BREAKPARTICLES:
					break;
				case PLAYERKICK:
					((EntityPlayerMP)ep).playerNetServerHandler.kickPlayerFromServer(stringdata);
					break;
			}
			if (world.isRemote)
				this.clientHandle(world, x, y, z, pack, data, stringdata, ep);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	private void clientHandle(World world, int x, int y, int z, PacketIDs pack, int[] data, String sg, EntityPlayer player) {
		switch(pack) {
			case NUMBERPARTICLE:
				Minecraft.getMinecraft().effectRenderer.addEffect(new NumberParticleFX(world, x+0.5, y+0.5, z+0.5, data[0]));
				break;
			case IDDUMP:
				IDDumpCommand.dumpClientside(data[0]);
				break;
			case ENTITYDUMP:
				EntityListCommand.dumpClientside();
				break;
			case EXPLODE:
				ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.explode");
				ReikaParticleHelper.EXPLODE.spawnAroundBlock(world, x, y, z, 1);
				break;
			case OLDMODS:
				CommandableUpdateChecker.instance.onClientReceiveOldModID(sg);
				break;
			case LOGIN:
				MinecraftForge.EVENT_BUS.post(new ClientLoginEvent(player));
				break;
			case BREAKPARTICLES:
				Block b = Block.getBlockById(data[0]);
				ReikaRenderHelper.spawnDropParticles(world, x, y, z, b, data[1]);
			default:
				break;
		}
	}

	public static enum PacketIDs {
		BIOMECHANGE(),
		BLOCKUPDATE(),
		PARTICLE(),
		KEYUPDATE(),
		TILESYNC(),
		VTILESYNC(),
		TILEDELETE(),
		PLAYERDATSYNC(),
		PLAYERDATSYNC_CLIENT(),
		//PLAYERATTRSYNC(),
		RERENDER(),
		COLOREDPARTICLE(),
		NUMBERPARTICLE(),
		IDDUMP(),
		ENTITYDUMP(),
		EXPLODE(),
		OLDMODS(),
		LOGIN(),
		SERVERSOUND(),
		BREAKPARTICLES(),
		PLAYERKICK();

		public static PacketIDs getEnum(int index) {
			return PacketIDs.values()[index];
		}

		public boolean isLongPacket() {
			return false;
		}

		public boolean hasLocation() {
			return this != KEYUPDATE && this != PLAYERKICK;
		}

		public int getNumberDataInts() {
			switch(this) {
				case PARTICLE:
					return 2;
				case NUMBERPARTICLE:
					return 1;
				case COLOREDPARTICLE:
					return 5;
				case BIOMECHANGE:
					return 1;
				case KEYUPDATE:
					return 2;
				case TILESYNC:
					return 1;
				case IDDUMP:
					return 1;
				case BREAKPARTICLES:
					return 2;
				default:
					return 0;
			}
		}
	}

}
