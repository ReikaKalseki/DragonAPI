package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Entity;


public class BasicEntityInvisibilityEvent extends EntityInvisibilityEvent {

	public BasicEntityInvisibilityEvent() {
		super("net.minecraft.entity.Entity", "sa");
	}

}
