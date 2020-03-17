package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;

@Cancelable
public class TileEntityMoveEvent extends Event {

	public final World world;
	public final int xCoord;
	public final int yCoord;
	public final int zCoord;

	public final Block block;
	public final int metadata;
	public final TileEntity tile;

	public TileEntityMoveEvent(World w, int x, int y, int z, Block b, int meta, TileEntity te) {
		world = w;
		xCoord = x;
		yCoord = y;
		zCoord = z;
		block = b;
		metadata = meta;
		tile = te;
	}

	public static boolean fireTileMoveEvent(World world, int x, int y, int z) {
		return MinecraftForge.EVENT_BUS.post(new TileEntityMoveEvent(world, x, y, z, world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), world.getTileEntity(x, y, z)));
	}
}
