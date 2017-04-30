package Reika.DragonAPI.Interfaces.Block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;


public interface ShearablePlant {

	public void shearAll(World world, int x, int y, int z, EntityPlayer ep);

}
