package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;

import cpw.mods.fml.common.eventhandler.Event.HasResult;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@HasResult
@SideOnly(Side.CLIENT)
public class EntityInvisibilityDeRenderEvent extends EntityEvent {

	public final EntityPlayer caller;

	public EntityInvisibilityDeRenderEvent(Entity e, EntityPlayer ep) {
		super(e);
		caller = ep;
	}

	public static boolean fire(Entity e, EntityPlayer caller) {
		EntityInvisibilityDeRenderEvent evt = new EntityInvisibilityDeRenderEvent(e, caller);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DENY:
				return false;
			case DEFAULT:
			default:
				return e instanceof EntityPlayer ? isPlayerInvisible((EntityPlayer)e, caller) : e.isInvisible();
		}
	}

	private static boolean isPlayerInvisible(EntityPlayer ep, EntityPlayer caller) {
		if (!ep.isInvisible()) {
			return false;
		}
		else {
			Team team = ep.getTeam();
			return team == null || caller == null || caller.getTeam() != team || !team.func_98297_h();
		}
	}

}
