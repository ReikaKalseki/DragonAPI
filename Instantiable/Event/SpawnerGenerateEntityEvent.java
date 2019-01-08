package Reika.DragonAPI.Instantiable.Event;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;


public class SpawnerGenerateEntityEvent extends EntityEvent {

	public final TileEntityMobSpawner spawner;

	public SpawnerGenerateEntityEvent(TileEntityMobSpawner tile, Entity e) {
		super(e);
		spawner = tile;
	}

	public static void fire(MobSpawnerBaseLogic lgc, Entity e) {
		TileEntityMobSpawner te = (TileEntityMobSpawner)lgc.getSpawnerWorld().getTileEntity(lgc.getSpawnerX(), lgc.getSpawnerY(), lgc.getSpawnerZ());
		MinecraftForge.EVENT_BUS.post(new SpawnerGenerateEntityEvent(te, e));
	}

}
