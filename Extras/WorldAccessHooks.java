package Reika.DragonAPI.Extras;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.BlockUpdateEvent;
import Reika.DragonAPI.Instantiable.Event.EntityRemovedEvent;


public class WorldAccessHooks implements IWorldAccess {

	private final World world;

	public WorldAccessHooks(World world) {
		this.world = world;
	}

	@Override
	public void markBlockForUpdate(int x, int y, int z) {
		MinecraftForge.EVENT_BUS.post(new BlockUpdateEvent(world, x, y, z, false));
	}

	@Override
	public void markBlockForRenderUpdate(int x, int y, int z) {
		MinecraftForge.EVENT_BUS.post(new BlockUpdateEvent(world, x, y, z, true));
	}

	@Override
	public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {/*
		for (int x = x1; x <= x2; x++)
			for (int y = y1; y <= y2; y++)
				for (int z = z1; z <= z2; z++)
					MinecraftForge.EVENT_BUS.post(new BlockUpdateEvent(world, x, y, z, true));*/
	}

	@Override
	public void playSound(String s, double x, double y, double z, float v, float p) {

	}

	@Override
	public void playSoundToNearExcept(EntityPlayer ep, String s, double x, double y, double z, float v, float p) {

	}

	@Override
	public void spawnParticle(String s, double x, double y, double z, double vx, double vy, double vz) {

	}

	@Override
	public void onEntityCreate(Entity e) {

	}

	@Override
	public void onEntityDestroy(Entity e) {
		MinecraftForge.EVENT_BUS.post(new EntityRemovedEvent(e));
	}

	@Override
	public void playRecord(String s, int x, int y, int z) {

	}

	@Override
	public void broadcastSound(int id, int x, int y, int z, int data) {

	}

	@Override
	public void playAuxSFX(EntityPlayer ep, int id, int x, int y, int z, int data) {

	}

	@Override
	public void destroyBlockPartially(int x, int y, int z, int a, int b) {

	}

	@Override
	public void onStaticEntitiesChanged() {

	}

}
