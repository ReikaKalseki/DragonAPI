package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;

/** Fired when an entity is removed from the world, almost always because it was setDead()-ed. */
public class EntityRemovedEvent extends EntityEvent {

	public EntityRemovedEvent(Entity e) {
		super(e);
	}

}
