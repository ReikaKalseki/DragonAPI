package Reika.DragonAPI.Interfaces.TileEntity;

import net.minecraft.world.World;

public interface SimpleConnection {

	public boolean tryConnect(World world, int x, int y, int z);

}
