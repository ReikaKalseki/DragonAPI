package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.WorldPositionEvent;

import cpw.mods.fml.common.eventhandler.Cancelable;


@Cancelable
public class SpawnerGenerationEvent extends WorldPositionEvent {

	public final SpawnerSource location;

	public SpawnerGenerationEvent(World world, int x, int y, int z, SpawnerSource src) {
		super(world, x, y, z);
		location = src;
	}

	public TileEntityMobSpawner getSpawner() {
		return (TileEntityMobSpawner)world.getTileEntity(xCoord, yCoord, zCoord);
	}

	public static void fire(World world, int x, int y, int z, SpawnerSource src) {
		if (MinecraftForge.EVENT_BUS.post(new SpawnerGenerationEvent(world, x, y, z, src))) {
			world.setBlockToAir(x, y, z);
		}
	}

	public static enum SpawnerSource {
		DUNGEON(),
		MINESHAFT(),
		STRONGHOLD(),
		NETHERFORTRESS(),
		//SPIDERTREE(), //ThaumCraft
		//BARROW(), //also TC
		//DESERTSTRUCT(), //CC
		//OCEANSTRUCT(), //CC
		OTHER();

		public void fire(World world, int x, int y, int z) {
			SpawnerGenerationEvent.fire(world, x, y, z, this);
		}
	}

}
