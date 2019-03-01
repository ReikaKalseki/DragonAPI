package Reika.DragonAPI.Instantiable.Event;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public class SpawnerCheckPlayerEvent extends Event {

	public final MobSpawnerBaseLogic spawner;
	public final EntityPlayer player;

	public SpawnerCheckPlayerEvent(MobSpawnerBaseLogic tile, EntityPlayer ep) {
		spawner = tile;
		player = ep;
	}

	public static boolean runCheck(MobSpawnerBaseLogic lgc) {
		//List<EntityPlayer> ep = lgc.getSpawnerWorld().getClosestPlayer(lgc.getSpawnerX()+0.5, lgc.getSpawnerY(), lgc.getSpawnerZ(), 16);
		for (EntityPlayer ep : ((List<EntityPlayer>)lgc.getSpawnerWorld().playerEntities)) {
			SpawnerCheckPlayerEvent evt = new SpawnerCheckPlayerEvent(lgc, ep);
			MinecraftForge.EVENT_BUS.post(evt);
			switch(evt.getResult()) {
				case ALLOW:
					return true;
				case DENY:
					continue;
				default:
					if (ep.getDistanceSq(lgc.getSpawnerX()+0.5, lgc.getSpawnerY()+0.5, lgc.getSpawnerZ()+0.5) <= lgc.activatingRangeFromPlayer*lgc.activatingRangeFromPlayer) {
						return true;
					}
					break;
			}
		}
		return false;
	}

}
