/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMutated;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.DragonAPI.Interfaces.Entity.TameHostile;

/** The methods called by ASMed-in hooks */
public class ASMCalls {

	public static void registerPermutedBiomesToDictionary() { //Kept here to prevent premature init of ReikaBiomeHelper
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b instanceof BiomeGenMutated) {
				BiomeGenBase parent = ((BiomeGenMutated)b).baseBiome;
				BiomeDictionary.registerBiomeType(b, BiomeDictionary.getTypesForBiome(parent));
			}
		}
	}

	public static boolean handleLavaMovement(Entity e) {
		AxisAlignedBB box = e.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D); //from vanilla
		World world = e.worldObj;
		int x0 = MathHelper.floor_double(box.minX);
		int x1 = MathHelper.floor_double(box.maxX + 1.0D);
		int y0 = MathHelper.floor_double(box.minY);
		int y1 = MathHelper.floor_double(box.maxY + 1.0D);
		int z0 = MathHelper.floor_double(box.minZ);
		int z1 = MathHelper.floor_double(box.maxZ + 1.0D);

		for (int x = x0; x < x1; ++x) {
			for (int y = y0; y < y1; ++y) {
				for (int z = z0; z < z1; ++z) {
					Block b = world.getBlock(x, y, z);
					Fluid f = FluidRegistry.lookupFluidForBlock(b);
					if (f == FluidRegistry.LAVA || (f != null && f.getTemperature(world, x, y, z) >= FluidRegistry.LAVA.getTemperature(world, x, y, z))) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public static boolean allowMobSleeping(List<EntityMob> li) {
		for (EntityMob e : li) {
			if (!(e instanceof TameHostile))
				return false;
		}
		return true;
	}
}
