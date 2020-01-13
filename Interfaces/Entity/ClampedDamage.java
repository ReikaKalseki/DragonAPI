package Reika.DragonAPI.Interfaces.Entity;

import net.minecraft.util.DamageSource;

/** For entities that have damage caps per hit. */
public interface ClampedDamage {

	public float getDamageCap(DamageSource src, float dmg);

}
