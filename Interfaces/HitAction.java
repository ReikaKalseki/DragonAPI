package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface HitAction {

	public void onHit(World world, int x, int y, int z, EntityPlayer ep);

}
