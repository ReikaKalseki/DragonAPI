package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.player.EntityPlayer;

public interface XPProducer {

	public void clearXP();

	public float getXP();

	public void addXPToPlayer(EntityPlayer ep);

}
