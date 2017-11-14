package Reika.DragonAPI.Interfaces.Callbacks;

import net.minecraft.world.IBlockAccess;


public interface PositionCallable<V> {

	public V call(IBlockAccess world, int x, int y, int z);

}
