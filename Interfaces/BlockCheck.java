package Reika.DragonAPI.Interfaces;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface BlockCheck {

	public boolean matchInWorld(World world, int x, int y, int z);
	public boolean match(Block b, int meta);
	public void place(World world, int x, int y, int z);
}