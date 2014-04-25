/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkDataEvent;
import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class RetroGenController {

	private ArrayList<RetroactiveGenerator> retrogens = new ArrayList();
	private ArrayList<Integer> banlist = new ArrayList();

	public static final String NBT_TAG = "DRAGONAPI_RETROGEN";

	private static final Random rand = new Random();

	private static final RetroGenController instance = new RetroGenController();

	public static RetroGenController getInstance() {
		return instance;
	}

	/** Adds a retroactive generator to the API's retro-gen registry. Note that this ALWAYS
	 * runs; it is up to the source mod to control when it should be registered. */
	public void addRetroGenerator(RetroactiveGenerator gen) {
		retrogens.add(gen);
	}

	public void addDisallowedDimension(int dim) {
		if (!banlist.contains(dim))
			banlist.add(dim);
	}

	public boolean isAllowedToRetrogen(World world) {
		int dim = world.provider.dimensionId;
		return !banlist.contains(dim);
	}

	private void generate(ChunkDataEvent.Load event) {
		World world = event.world;
		if (!this.isAllowedToRetrogen(world))
			return;
		NBTTagCompound tag = (NBTTagCompound)event.getData().getTag(NBT_TAG);
		boolean regenall = false;
		if (tag == null) {
			regenall = true;
			event.getData().setTag(NBT_TAG, new NBTTagCompound());
		}
		NBTTagCompound nbt = (NBTTagCompound)event.getData().getTag(NBT_TAG);
		int x = event.getChunk().xPosition*16;
		int z = event.getChunk().zPosition*16;
		for (int i = 0; i < retrogens.size(); i++) {
			RetroactiveGenerator gen = retrogens.get(i);
			if ((regenall || this.shouldRegen(tag, gen)) && gen.canGenerateAt(rand, world, x, z)) {
				gen.generate(rand, world, x, z);
				nbt.setBoolean(gen.getIDString(), true);
				ReikaJavaLibrary.pConsole("Retroactively generating "+gen.getIDString()+" @ "+x+", "+z+" (DIM "+world.provider.dimensionId+")");
			}
		}
	}

	private boolean shouldRegen(NBTTagCompound tag, RetroactiveGenerator gen) {
		if (tag == null)
			return true;
		return !tag.hasKey(gen.getIDString()) || !tag.getBoolean(gen.getIDString());
	}

	@ForgeSubscribe
	public void handleChunkLoadEvent(ChunkDataEvent.Load event)
	{
		//this.generate(event);
	}

}
