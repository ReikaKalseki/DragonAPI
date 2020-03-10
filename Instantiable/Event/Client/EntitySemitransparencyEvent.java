package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class EntitySemitransparencyEvent extends EntityEvent {

	public final float defaultOpacity;
	public float opacity;

	public EntitySemitransparencyEvent(Entity e) {
		super(e);
		defaultOpacity = 0.15F;
		opacity = defaultOpacity;
	}

	public static float fire(Entity e) {
		EntitySemitransparencyEvent evt = new EntitySemitransparencyEvent(e);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.opacity;
	}

}
