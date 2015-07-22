package Reika.DragonAPI.Instantiable.AI;

import net.minecraft.entity.EntityLiving;
import net.minecraft.tileentity.TileEntity;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.MobAttractor;

public class AITaskSeekMachine extends AITaskSeekLocation {

	private final MobAttractor tile;

	public AITaskSeekMachine(EntityLiving e, double sp, MobAttractor te) {
		super(e, sp, new Coordinate((TileEntity)te));
		tile = te;
	}

	@Override
	public final boolean shouldExecute() {
		return tile.canAttract(entity);
	}

}
