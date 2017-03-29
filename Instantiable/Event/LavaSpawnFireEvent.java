package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class LavaSpawnFireEvent extends BlockEvent {

	public LavaSpawnFireEvent(World world, int x, int y, int z) {
		super(x, y, z, world, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public static boolean fire(Block liquid, World world, int x, int y, int z) {
		LavaSpawnFireEvent evt = new LavaSpawnFireEvent(world, x, y, z);
		MinecraftForge.EVENT_BUS.post(evt);
		return !evt.isCanceled() && evt.block.getMaterial().getCanBurn();
	}

}
