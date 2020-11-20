package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
public final class SnowOrIceOnGenEvent extends WorldPositionEvent {

	public final SnowType snowType;

	public SnowOrIceOnGenEvent(World world, int x, int y, int z, SnowType s) {
		super(world, x, y, z);
		snowType = s;
	}

	public static boolean fireSnow(World world, int x, int y, int z, boolean flag) {
		SnowOrIceOnGenEvent evt = new SnowOrIceOnGenEvent(world, x, y, z, SnowType.SNOW);
		return fireEvent(evt, flag);
	}

	public static boolean fireIce(World world, int x, int y, int z) {
		SnowOrIceOnGenEvent evt = new SnowOrIceOnGenEvent(world, x, y, z, SnowType.ICE);
		return fireEvent(evt, false);
	}

	private static boolean fireEvent(SnowOrIceOnGenEvent evt, boolean flag) {
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			case DENY:
				return false;
			case DEFAULT:
			default:
				return evt.snowType == SnowType.SNOW ? evt.world.func_147478_e(evt.xCoord, evt.yCoord, evt.zCoord, flag) : evt.world.isBlockFreezable(evt.xCoord, evt.yCoord, evt.zCoord);
		}
	}

	public static enum SnowType {
		SNOW,
		ICE;
	}

}
