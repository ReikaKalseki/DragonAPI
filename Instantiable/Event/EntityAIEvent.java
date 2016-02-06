package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent;

//Unimplemented
public class EntityAIEvent extends EntityEvent {

	public EntityAIEvent(EntityLivingBase e) {
		super(e);
	}

	public static boolean fire(EntityLivingBase e) {
		return false;
	}

}
