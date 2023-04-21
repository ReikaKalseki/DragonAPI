package Reika.DragonAPI.Interfaces.Callbacks;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface BlockPlaceCallback {

	/** The boolean return value is "cancel the placement", but be aware various implementations may not respect it, and that this is intentional. */
	public boolean onPlacement(World world, int x, int y, int z, Block b, int meta, int flags);

}
