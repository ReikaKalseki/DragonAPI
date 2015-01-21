/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.IO;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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

		private final WorldLocation loc;
		private final int radius;

		public RadiusTarget(WorldLocation loc, int r) {
			this.loc = loc;
			radius = r;
		}

		public RadiusTarget(TileEntity te, int r) {
			this(new WorldLocation(te), r);
		}

		public RadiusTarget(World world, int x, int y, int z, int r) {
			this(new WorldLocation(world, x, y, z), r);
		}

		public RadiusTarget(World world, Coordinate c, int r) {
			this(new WorldLocation(world, c), r);
		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			p.sendToAllAround(pk, loc, radius);
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

	@SideOnly(Side.CLIENT)
	public static final class ServerTarget extends PacketTarget {

		public ServerTarget() {

		}

		@Override
		public void dispatch(PacketPipeline p, PacketObj pk) {
			p.sendToServer(pk);
		}
	}
}
