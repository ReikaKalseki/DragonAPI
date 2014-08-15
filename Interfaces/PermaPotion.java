package Reika.DragonAPI.Interfaces;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

public interface PermaPotion {

	public boolean canBeCleared(EntityLivingBase e, PotionEffect pot);

}