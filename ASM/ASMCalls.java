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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenMutated;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonOptions;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.VillageBuilding.PerVillageWeight;
import Reika.DragonAPI.Interfaces.Block.CollisionDelegate;
import Reika.DragonAPI.Interfaces.Block.CustomSnowAccumulation;
import Reika.DragonAPI.Interfaces.Entity.TameHostile;
import Reika.DragonAPI.Interfaces.Item.MetadataSpecificTrade;

import cpw.mods.fml.common.registry.VillagerRegistry;

/** The methods called by ASMed-in hooks */
public class ASMCalls {

	public static MerchantRecipe getMatchingTrade(MerchantRecipeList li, ItemStack is1, ItemStack is2, int idx) {
		if (idx > 0 && idx < li.size()) {
			MerchantRecipe rec = (MerchantRecipe)li.get(idx);
			return matchFirst(rec, is1) && matchSecond(rec, is2) ? rec : null;
		}
		else {
			for (int j = 0; j < li.size(); ++j) {
				MerchantRecipe ret = (MerchantRecipe)li.get(j);
				if (matchFirst(ret, is1) && matchSecond(ret, is2)) {
					return ret;
				}
			}
			return null;
		}
	}

	private static boolean matchFirst(MerchantRecipe rec, ItemStack is1) {
		return matchItem(rec.getItemToBuy(), is1);
	}

	private static boolean matchSecond(MerchantRecipe rec, ItemStack is2) {
		if (rec.getSecondItemToBuy() == null) {
			return is2 == null;
		}
		else {
			return is2 != null && matchItem(rec.getSecondItemToBuy(), is2);
		}
	}

	private static boolean matchItem(ItemStack a, ItemStack b) {
		return a.getItem() == b.getItem() && b.stackSize >= a.stackSize && ((a.getItem() instanceof MetadataSpecificTrade || b.getItem() instanceof MetadataSpecificTrade) ? a.getItemDamage() == b.getItemDamage() : true);
	}

	public static void preTessellatorStart() {
		GL11.glAlphaFunc(GL11.GL_GEQUAL, 1/255F);
	}

	public static List buildVillageStructureList(Random rand, int val, MapGenVillage.Start s) {
		ArrayList li = new ArrayList();
		li.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House4Garden.class, 4, MathHelper.getRandomIntegerInRange(rand, 2 + val, 4 + val * 2)));
		li.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Church.class, 20, MathHelper.getRandomIntegerInRange(rand, 0 + val, 1 + val)));
		li.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House1.class, 20, MathHelper.getRandomIntegerInRange(rand, 0 + val, 2 + val)));
		li.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.WoodHut.class, 3, MathHelper.getRandomIntegerInRange(rand, 2 + val, 5 + val * 3)));
		li.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Hall.class, 15, MathHelper.getRandomIntegerInRange(rand, 0 + val, 2 + val)));
		li.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field1.class, 3, MathHelper.getRandomIntegerInRange(rand, 1 + val, 4 + val)));
		li.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field2.class, 3, MathHelper.getRandomIntegerInRange(rand, 2 + val, 4 + val * 2)));
		li.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House2.class, 15, MathHelper.getRandomIntegerInRange(rand, 0, 1 + val)));
		li.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House3.class, 8, MathHelper.getRandomIntegerInRange(rand, 0 + val, 3 + val * 2)));
		VillagerRegistry.addExtraVillageComponents(li, rand, val);

		Iterator<PieceWeight> it = li.iterator();
		while (it.hasNext()) {
			PieceWeight pw = it.next();
			if (pw.villagePiecesLimit == 0) {
				it.remove();
			}
			else if (pw instanceof PerVillageWeight && !((PerVillageWeight)pw).canGenerate(s)) {
				it.remove();
			}
		}

		return li;
	}

	public static boolean isGitFile(File f) {
		return f.getName().contains(".git");
	}

	public static ChunkPosition getStrongholdSeekPos(StructureStrongholdPieces.Stairs2 struct) {
		if (struct == null) {
			DragonAPICore.logError("Cannot get the stronghold position for a null structure!");
			return null;
		}
		else if (struct.strongholdPortalRoom == null) {
			DragonAPICore.log("Structure has no portal!");
			return new ChunkPosition(struct.getBoundingBox().getCenterX(), struct.getBoundingBox().getCenterY(), struct.getBoundingBox().getCenterZ());
		}
		return DragonOptions.REROUTEEYES.getState() ? new ChunkPosition(struct.getBoundingBox().getCenterX(), struct.getBoundingBox().getCenterY(), struct.getBoundingBox().getCenterZ()) : struct.strongholdPortalRoom.func_151553_a();
	}

	public static boolean canSnowAccumulate(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		if (block == Blocks.ice || block == Blocks.packed_ice)
			return false;
		if (block.isLeaves(world, x, y - 1, z))
			return true;
		if (block == Blocks.snow_layer)
			return (world.getBlockMetadata(x, y - 1, z) & 7) == 7;
		//return block.isOpaqueCube() && block.getMaterial().blocksMovement(); //the original
		if (block instanceof CustomSnowAccumulation)
			return ((CustomSnowAccumulation)block).canSnowAccumulate(world, x, y-1, z);
		return block.getMaterial().blocksMovement() && block.isSideSolid(world, x, y-1, z, ForgeDirection.UP);
	}

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
					int dx = x;
					int dy = y;
					int dz = z;
					Block b = world.getBlock(x, y, z);
					if (b instanceof CollisionDelegate) {
						Coordinate c = ((CollisionDelegate)b).getDelegatedCollision(world, x, y, z);
						b = c.getBlock(world);
						dx = c.xCoord;
						dy = c.yCoord;
						dz = c.zCoord;
					}
					Fluid f = FluidRegistry.lookupFluidForBlock(b);
					if (f == FluidRegistry.LAVA || (f != null && f.getTemperature(world, dx, dy, dz) >= FluidRegistry.LAVA.getTemperature(world, dx, dy, dz))) {
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
