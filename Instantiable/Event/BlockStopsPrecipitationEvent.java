package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

import cpw.mods.fml.common.eventhandler.Event.HasResult;

@HasResult
/** Result sets whether precipitation is BLOCKED by it, with ALLOW for yes */
public class BlockStopsPrecipitationEvent extends WorldPositionEvent {

	public final Block block;
	public final boolean defaultResult;

	public BlockStopsPrecipitationEvent(World world, int x, int y, int z, Block b) {
		super(world, x, y, z);
		block = b;
		defaultResult = b.getMaterial().isLiquid() || b.getMaterial().blocksMovement();
	}

	public static boolean fire(Chunk c, Block b, int x, int y, int z) {
		int dx = x+c.xPosition*16;
		int dz = z+c.zPosition*16;
		BlockStopsPrecipitationEvent evt = new BlockStopsPrecipitationEvent(c.worldObj, dx, y, dz, b);
		MinecraftForge.EVENT_BUS.post(evt);
		switch(evt.getResult()) {
			case ALLOW:
				return true;
			default:
			case DEFAULT:
				return evt.defaultResult;
			case DENY:
				return false;
		}
	}

}
