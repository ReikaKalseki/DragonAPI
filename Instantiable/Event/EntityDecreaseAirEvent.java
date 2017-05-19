package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class EntityDecreaseAirEvent extends LivingEvent {

	public EntityDecreaseAirEvent(EntityLivingBase entity) {
		super(entity);
	}

	public static boolean fire(EntityLivingBase e) {
		EntityDecreaseAirEvent evt = new EntityDecreaseAirEvent(e);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DENY:
				return false;
			case DEFAULT:
			default:
				return e.isInsideOfMaterial(Material.water);
		}
	}

}
