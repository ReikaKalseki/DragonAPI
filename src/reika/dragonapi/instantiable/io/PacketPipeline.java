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

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.instantiable.data.immutable.WorldLocation;
import reika.dragonapi.interfaces.PacketHandler;
import reika.dragonapi.libraries.io.ReikaPacketHelper.PacketObj;
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
	private final PacketHandler handler;
	private final SimpleNetworkWrapper wrapper;

	public PacketPipeline(DragonAPIMod mod, String modChannel, PacketHandler handler, SimpleNetworkWrapper wrapper) {
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

	public PacketHandler getHandler() {
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

	public void sendToAllAround(PacketObj p, TileEntity te, double range) {
		this.sendToAllAround(p, new WorldLocation(te), range);
	}

	public void sendToAllAround(PacketObj p, World world, double x, double y, double z, double range) {
		this.sendToAllAround(p, world.provider.dimensionId, x, y, z, range);
	}

	public void sendToAllAround(PacketObj p, int world, double x, double y, double z, double range) {
		TargetPoint pt = new TargetPoint(world, x, y, z, range);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(pt);
		//channels.get(Side.SERVER).writeAndFlush(p);
		wrapper.sendToAllAround(p, pt);
	}

	public void sendToAllAround(PacketObj p, Entity e, double range) {
		TargetPoint pt = new TargetPoint(e.worldObj.provider.dimensionId, e.posX, e.posY, e.posZ, range);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		//channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(pt);
		//channels.get(Side.SERVER).writeAndFlush(p);
		wrapper.sendToAllAround(p, pt);
	}

	public void sendToAllAround(PacketObj p, WorldLocation loc, double range) {
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
