package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class LightLevelForSpawnEvent extends EntityEvent {

	public final EntityMob mob;
	public final boolean defaultLightValidity;
	public final int entityX;
	public final int entityY;
	public final int entityZ;

	public LightLevelForSpawnEvent(EntityMob eb, boolean orig) {
		super(eb);
		defaultLightValidity = orig;
		mob = eb;
		entityX = MathHelper.floor_double(eb.posX);
		entityY = MathHelper.floor_double(eb.boundingBox.minY);
		entityZ = MathHelper.floor_double(eb.posZ);
	}

	public static boolean fire(boolean orig, EntityMob eb) {
		LightLevelForSpawnEvent e = new LightLevelForSpawnEvent(eb, orig);
		MinecraftForge.EVENT_BUS.post(e);
		switch(e.getResult()) {
			case ALLOW:
				return true;
			case DEFAULT:
			default:
				return e.defaultLightValidity;
			case DENY:
				return false;
		}
	}

	/** orig is NOT weight; it is light brightness, which will cause a spawn fail if > 0.5) */
	public static float firePathWeight(float orig, EntityMob eb) {
		LightLevelForSpawnEvent e = new LightLevelForSpawnEvent(eb, orig >= 0);
		MinecraftForge.EVENT_BUS.post(e);
		switch(e.getResult()) {
			case ALLOW:
				return 0;
			case DEFAULT:
			default:
				return orig;
			case DENY:
				return 1;
		}
	}

}
