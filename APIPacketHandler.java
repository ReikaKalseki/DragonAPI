/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public abstract class APIPacketHandler implements IPacketHandler {

	private final Random rand = new Random();

	protected PacketIDs pack;
	protected PacketTypes packetType;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		this.process(packet, (EntityPlayer)player);
	}

	public abstract void process(Packet250CustomPayload packet, EntityPlayer ep);

	public void handleData(Packet250CustomPayload packet, World world, EntityPlayer ep) {
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int control = Integer.MIN_VALUE;
		int len;
		int[] data = new int[0];
		long longdata = 0;
		float floatdata = 0;
		int x,y,z;
		boolean readinglong = false;
		String stringdata = null;
		//System.out.print(packet.length);
		try {
			//ReikaJavaLibrary.pConsole(inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt()+":"+inputStream.readInt());
			packetType = PacketTypes.getPacketType(inputStream.readInt());
			switch(packetType) {
			case SOUND:
				return;
			case STRING:
				control = inputStream.readInt();
				pack = PacketIDs.getEnum(control);
				stringdata = Packet.readString(inputStream, Short.MAX_VALUE);
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
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				String name = Packet.readString(inputStream, Short.MAX_VALUE);
				int value = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityData(world, x, y, z, name, value);
				return;
			case TANK:
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				String tank = Packet.readString(inputStream, Short.MAX_VALUE);
				int level = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityTankData(world, x, y, z, tank, level);
				return;
			}
			x = inputStream.readInt();
			y = inputStream.readInt();
			z = inputStream.readInt();
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
				world.markBlockForRenderUpdate(x, y, z);
				break;
			case PARTICLE:
				if (data[0] < 0 || data[0] >= ReikaParticleHelper.particleList.length) {
					ReikaParticleHelper p = ReikaParticleHelper.particleList[data[0]];
					world.spawnParticle(p.name, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), 0, 0, 0);
				}
				break;
			case BIOMECHANGE:
				ReikaWorldHelper.setBiomeForXZ(world, x, z, BiomeGenBase.biomeList[data[0]]);
				world.markBlockRangeForRenderUpdate(x, 0, z, x, world.provider.getActualHeight(), z);
				break;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static enum PacketIDs {
		BIOMECHANGE(),
		BLOCKUPDATE(),
		PARTICLE();

		public static PacketIDs getEnum(int index) {
			return PacketIDs.values()[index];
		}

		public boolean isLongPacket() {
			return false;
		}

		public int getNumberDataInts() {
			switch(this) {
			case PARTICLE:
				return 1;
			case BIOMECHANGE:
				return 1;
			default:
				return 0;
			}
		}
	}

}
