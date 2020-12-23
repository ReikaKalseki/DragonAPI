package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public interface WinterBiomeStrengthControl {

	float getWinterSkyStrength(World world, EntityPlayer ep);

}
