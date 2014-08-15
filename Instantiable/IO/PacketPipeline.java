package Reika.DragonAPI.Instantiable.IO;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Instantiable.WorldLocation;
import Reika.DragonAPI.Interfaces.IPacketHandler;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketPipeline {

	private ArrayList<Class<? extends PacketObj>> packets = new ArrayList();
	private boolean isPostInitialized = false;
	private final DragonAPIMod mod;
	public final String packetChannel;
	private final IPacketHandler handler;
	private final SimpleNetworkWrapper wrapper;

	public PacketPipeline(DragonAPIMod mod, String modChannel, IPacketHandler handler, SimpleNetworkWrapper wrapper) {
		packetChannel = modChannel;
		this.mod = mod;
		this.handler = handler;
		this.wrapper = wrapper;
	}

	public void registerPacket(Class<? extends PacketObj> cl) {
		int id = packets.size();
		wrapper.registerMessage(InternalHandler.class, (Class)cl, id, Side.SERVER);
		wrapper.registerMessage(InternalHandler.class, (Class)cl, id, Side.CLIENT);
	}

	public IPacketHandler getHandler() {
		return handler;
	}

	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
	/*
	public void replyToPacket(PacketObj p) {
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.REPLY);
		channels.get(Side.SERVER).writeAndFlush(p);
	}*/

	public void sendToAllOnServer(PacketObj p) {
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		//channels.get(Side.SERVER).writeAndFlush(p);
		wrapper.sendToAll(p);
	}

	public Packet getMinecraftPacket(PacketObj p) {
		return wrapper.getPacketFrom(p);
	}

	public void sendToPlayer(PacketObj p, EntityPlayerMP player) {
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		//channels.get(Side.SERVER).writeAndFlush(p);
		wrapper.sendTo(p, player);
	}

	public void sendToAllAround(PacketObj p, TileEntity te, int range) {
		this.sendToAllAround(p, new WorldLocation(te), range);
	}

	public void sendToAllAround(PacketObj p, World world, double x, double y, double z, int range) {
		TargetPoint pt = new TargetPoint(world.provider.dimensionId, x, y, z, range);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(pt);
		//channels.get(Side.SERVER).writeAndFlush(p);
		wrapper.sendToAllAround(p, pt);
	}

	public void sendToAllAround(PacketObj p, WorldLocation loc, int range) {
		TargetPoint pt = new TargetPoint(loc.dimensionID, loc.xCoord, loc.yCoord, loc.zCoord, range);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(pt);
		//channels.get(Side.SERVER).writeAndFlush(p);
		wrapper.sendToAllAround(p, pt);
	}

	public void sendToDimension(PacketObj p, World world) {
		this.sendToDimension(p, world.provider.dimensionId);
	}

	public void sendToDimension(PacketObj p, int dimensionId) {
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionId);
		//channels.get(Side.SERVER).writeAndFlush(p);
		wrapper.sendToDimension(p, dimensionId);
	}

	public void sendToServer(PacketObj p) {
		//channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		//channels.get(Side.CLIENT).writeAndFlush(p);
		wrapper.sendToServer(p);
	}

	public ModLogger getLogger() {
		return mod.getModLogger();
	}

	public static class InternalHandler implements IMessageHandler<PacketObj, IMessage> {

		public InternalHandler() {

		}

		@Override
		public IMessage onMessage(PacketObj message, MessageContext ctx) {
			switch(ctx.side) {
			case CLIENT:
				message.handleClient(ctx.getClientHandler());
				break;
			case SERVER:
				message.handleServer(ctx.getServerHandler());
				break;
			}
			return null; //return a packetObj if sending a reply
		}

	}
}
