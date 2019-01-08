package Reika.DragonAPI.Interfaces.Block;

import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public interface CollisionDelegate {

	Coordinate getDelegatedCollision(World world, int x, int y, int z);

}
