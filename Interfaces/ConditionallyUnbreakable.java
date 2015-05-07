package Reika.DragonAPI.Interfaces;

import net.minecraft.world.World;

public interface ConditionallyUnbreakable {

	public boolean isUnbreakable(World world, int x, int y, int z, int meta);

}
