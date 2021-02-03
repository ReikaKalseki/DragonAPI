package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class LavaFreezeEvent extends WorldPositionEvent {

	public final Block originalPlacement;

	public LavaFreezeEvent(World world, int x, int y, int z, Block b) {
		super(world, x, y, z);
		originalPlacement = b;
	}

	public static boolean fire(World world, int x, int y, int z, Block b) {
		if (MinecraftForge.EVENT_BUS.post(new LavaFreezeEvent(world, x, y, z, b)))
			return false;
		return world.setBlock(x, y, z, b);
	}

}
