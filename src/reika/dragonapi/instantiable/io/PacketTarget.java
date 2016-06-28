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
import java.util.Collection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import reika.dragonapi.instantiable.data.immutable.Coordinate;
import reika.dragonapi.instantiable.data.immutable.WorldLocation;
import reika.dragonapi.libraries.io.ReikaPacketHelper.PacketObj;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;

public abstract class PacketTarget {

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

	public static final class CompoundPlayerTarget extends PacketTarget {

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
		private final int radius;

		public RadiusTarget(WorldLocation loc, int r) {
			this(loc.dimensionID, loc.xCoord, loc.yCoord, loc.zCoord, r);
		}

		public RadiusTarget(Entity e, int r) {
			this(e.worldObj, e.posX, e.posY, e.posZ, r);
		}

		public RadiusTarget(TileEntity te, int r) {
			this(te.worldObj, te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, r);
		}

		public RadiusTarget(World world, double x, double y, double z, int r) {
			this(world.provider.dimensionId, x, y, z, r);
		}

		private RadiusTarget(int world, double x, double y, double z, int r) {
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

	public static final class AllPlayersTarget extends PacketTarget {

		public AllPlayersTarget() {

		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			p.sendToAllOnServer(pk);
		}
	}

	//@SideOnly(Side.CLIENT)
	public static final class ServerTarget extends PacketTarget {

		public ServerTarget() {

		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			p.sendToServer(pk);
		}
	}
}
