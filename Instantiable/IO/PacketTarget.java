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

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public abstract class PacketTarget {

	public static final PacketTarget server = new ServerTarget();
	public static final PacketTarget allPlayers = new AllPlayersTarget();

	public abstract void dispatch(PacketPipeline p, PacketObj pk);

	public static final class PlayerTarget extends PacketTarget {

		private final EntityPlayerMP player;

		public PlayerTarget(EntityPlayerMP ep) {
			player = ep;
		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			p.sendToPlayer(pk, player);
		}

	}

	public static final class OtherPlayersTarget extends CompoundPlayerTarget {

		public OtherPlayersTarget(EntityPlayer ep, double r) {
			super(ep.worldObj.getEntitiesWithinAABBExcludingEntity(ep, ReikaAABBHelper.getEntityCenteredAABB(ep, r), new ReikaEntityHelper.ClassEntitySelector(EntityPlayerMP.class, true)));
		}

	}

	public static class CompoundPlayerTarget extends PacketTarget {

		private final Collection<EntityPlayerMP> player;

		public CompoundPlayerTarget(EntityPlayerMP... ep) {
			player = ReikaJavaLibrary.makeListFromArray(ep);
		}

		public CompoundPlayerTarget(Collection<EntityPlayerMP> ep) {
			player = new ArrayList(ep);
		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			for (EntityPlayerMP ep : player)
				p.sendToPlayer(pk, ep);
		}

	}

	public static final class RadiusTarget extends PacketTarget {

		private final int dim;
		private final double x;
		private final double y;
		private final double z;
		private final double radius;

		public RadiusTarget(WorldLocation loc, double r) {
			this(loc.dimensionID, loc.xCoord, loc.yCoord, loc.zCoord, r);
		}

		public RadiusTarget(Entity e, double r) {
			this(e.worldObj, e.posX, e.posY, e.posZ, r);
		}

		public RadiusTarget(TileEntity te, double r) {
			this(te.worldObj, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, r);
		}

		public RadiusTarget(World world, double x, double y, double z, double r) {
			this(world.provider.dimensionId, x, y, z, r);
		}

		private RadiusTarget(int world, double x, double y, double z, double r) {
			dim = world;
			this.x = x;
			this.y = y;
			this.z = z;
			radius = r;
		}

		public RadiusTarget(World world, Coordinate c, int r) {
			this(new WorldLocation(world, c), r);
		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			p.sendToAllAround(pk, dim, x, y, z, radius);
		}
	}

	public static final class DimensionTarget extends PacketTarget {

		private final int dimension;

		public DimensionTarget(int dim) {
			dimension = dim;
		}

		public DimensionTarget(World world) {
			this(world.provider.dimensionId);
		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			p.sendToDimension(pk, dimension);
		}
	}

	private static final class AllPlayersTarget extends PacketTarget {

		private AllPlayersTarget() {

		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			p.sendToAllOnServer(pk);
		}
	}

	//@SideOnly(Side.CLIENT)
	private static final class ServerTarget extends PacketTarget {

		private ServerTarget() {

		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			p.sendToServer(pk);
		}
	}
}
