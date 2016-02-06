package Reika.DragonAPI.Interfaces;

import net.minecraft.world.IBlockAccess;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;


public interface MachineRegistryBlock {

	public TileEnum getMachine(IBlockAccess world, int x, int y, int z);

}
