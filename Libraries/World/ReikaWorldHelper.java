/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidFinite;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import Reika.DragonAPI.APIPacketHandler;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Extras.BlockProperties;
import Reika.DragonAPI.Instantiable.Data.Collections.RelativePositionList;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Event.MobTargetingEvent;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaVectorHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReikaWorldHelper extends DragonAPICore {

	public static boolean softBlocks(IBlockAccess world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.air)
			return true;
		if (b == Blocks.piston_extension)
			return false;
		if (ReikaBlockHelper.isLiquid(b))
			return true;
		if (b.isReplaceable(world, x, y, z))
			return true;
		if (b.isAir(world, x, y, z))
			return true;
		if (b == Blocks.vine)
			return true;
		return (BlockProperties.isSoft(b));
	}

	public static boolean softBlocks(Block id) {
		if (id == Blocks.air || id.getMaterial() == Material.air)
			return true;
		return BlockProperties.isSoft(id);
	}

	public static boolean flammable(IBlockAccess world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.air)
			return false;
		if (b == Blocks.trapdoor || b == Blocks.chest)
			return false;
		if (b.getFlammability(world, x, y, z, ForgeDirection.UP) > 0)
			return true;
		return BlockProperties.isFlammable(b);
	}

	public static boolean flammable(Block id) {
		if (id == Blocks.air)
			return false;
		return BlockProperties.isFlammable(id);
	}

	public static boolean nonSolidBlocks(Block id) {
		return BlockProperties.isNonSolid(id);
	}

	/** Caps the metadata at a certain value (eg, for leaves, metas are from 0-11, but there are only 4 types, and each type has 3 metas).
	 *Args: Initial metadata, cap (# of types) */
	public static int capMetadata(int meta, int cap) {
		while (meta >= cap)
			meta -= cap;
		return meta;
	}

	public static Material getMaterial(World world, int x, int y, int z) {
		return world.checkChunksExist(x, y, z, x, y, z) ? world.getBlock(x, y, z).getMaterial() : Material.air;
	}

	public static boolean isAirBlock(World world, int x, int y, int z) {
		return world.getBlock(x, y, z).isAir(world, x, y, z);
	}

	public static void setBlock(World world, int x, int y, int z, ItemStack is) {
		setBlock(world, x, y, z, is, 3);
	}

	public static void setBlock(World world, int x, int y, int z, ItemStack is, int flag) {
		world.setBlock(x, y, z, Block.getBlockFromItem(is.getItem()), is.getItemDamage(), flag);
	}

	/** Finds the top edge of the top solid (nonair) block in the column. Args: World, this.x,y,z */
	public static double findSolidSurface(World world, double x, double y, double z) { //Returns double y-coord of top surface of top block

		int xp = (int)x;
		int zp = (int)z;
		boolean lowestsolid = false;
		boolean solidup = false;
		boolean soliddown = false;

		while (!(!solidup && soliddown)) {
			solidup = (getMaterial(world, xp, (int)y, zp) != Material.air);
			soliddown = (getMaterial(world, xp, (int)y-1, zp) != Material.air);
			if (solidup && soliddown) //Both blocks are solid -> below surface
				y++;
			if (solidup && !soliddown) //Upper only is solid -> should never happen
				y += 2;					// Fix attempt
			if (!solidup && soliddown) // solid lower only
				;						// the case we want
			if (!solidup && !soliddown) //Neither solid -> above surface
				y--;
		}
		return y;
	}

	/** Finds the top edge of the top water block in the column. Args: World, this.x,y,z.
	 *DO NOT CALL if there is no water there, as there is a possibility of infinite loop. */
	public static double findWaterSurface(World world, double x, double y, double z) { //Returns double y-coord of top surface of top block

		int xp = (int)x;
		int zp = (int)z;
		boolean lowestwater = false;
		boolean waterup = false;
		boolean waterdown = false;

		while (!(!waterup && waterdown)) {
			waterup = (getMaterial(world, xp, (int)y, zp) == Material.water);
			waterdown = (getMaterial(world, xp, (int)y-1, zp) == Material.water);
			if (waterup && waterdown) //Both blocks are water -> below surface
				y++;
			if (waterup && !waterdown) //Upper only is water -> should never happen
				return y+3;		//Return and exit function
			if (!waterup && waterdown) // Water lower only
				;						// the case we want
			if (!waterup && !waterdown) //Neither water -> above surface
				y--;
		}
		return y;
	}

	/** Finds the top edge of the top fluid block in the column. Args: World, this.x,y,z.
	 *DO NOT CALL if there is no fluid there, as there is a possibility of infinite loop. */
	public static double findFluidSurface(World world, double x, double y, double z) { //Returns double y-coord of top surface of top block

		int xp = (int)x;
		int zp = (int)z;
		boolean lowestfluid = false;
		boolean fluidup = false;
		boolean fluiddown = false;
		Fluid f = FluidRegistry.lookupFluidForBlock(world.getBlock(xp, (int)y, zp));
		if (f == null)
			return y+3;

		while (!(!fluidup && fluiddown)) {
			fluidup = (getFluid(world, xp, (int)y, zp) == f);
			fluiddown = (getFluid(world, xp, (int)y-1, zp) == f);
			if (fluidup && fluiddown) //Both blocks are fluid -> below surface
				y++;
			if (fluidup && !fluiddown) //Upper only is fluid -> should never happen
				return y+3;		//Return and exit function
			if (!fluidup && fluiddown) // fluid lower only
				;						// the case we want
			if (!fluidup && !fluiddown) //Neither fluid -> above surface
				y--;
		}
		return y;
	}

	/** Search for a specific block in a range. Returns true if found. Cannot identify if
	 *found more than one, or where the found one(s) is/are. May be CPU-intensive. Args: World, this.x,y,z, search range, target id */
	public static boolean findNearBlock(World world, int x, int y, int z, int range, Block id) {
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				for (int k = -range; k <= range; k++) {
					if (world.getBlock(x+i, y+j, z+k) == id)
						return true;
				}
			}
		}
		return false;
	}

	/** Search for a specific block in a range. Returns true if found. Cannot identify if
	 *found more than one, or where the found one(s) is/are. May be CPU-intensive. Args: World, this.x,y,z, search range, target id, meta */
	public static boolean findNearBlock(World world, int x, int y, int z, int range, Block id, int meta) {
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				for (int k = -range; k <= range; k++) {
					if (world.getBlock(x+i, y+j, z+k) == id && world.getBlockMetadata(x+i, y+j, z+k) == meta)
						return true;
				}
			}
		}
		return false;
	}

	/** Search for a specific block in a range. Returns number found. Cannot identify where they
	 *are. May be CPU-intensive. Args: World, this.x,y,z, search range, target id */
	public static int findNearBlocks(World world, int x, int y, int z, int range, Block id) {
		int count = 0;
		for (int i = -range; i <= range; i++) {
			for (int j = -range; j <= range; j++) {
				for (int k = -range; k <= range; k++) {
					if (world.getBlock(x+i, y+j, z+k) == id)
						count++;
				}
			}
		}
		return count;
	}

	/** Tests for if a block of a certain id is in the "sights" of a directional block (eg dispenser).
	 *Returns the number of blocks away it is. If not found, returns 0 (an impossibility).
	 *Args: World, this.x,y,z, search range, target id, direction "f" */
	public static int isLookingAt(World world, int x, int y, int z, int range, Block id, int f) {
		Block idfound = Blocks.air;

		switch (f) {
			case 0:		//facing north (-z);
				for (int i = 0; i < range; i++) {
					idfound = world.getBlock(x, y, z-i);
					if (idfound == id)
						return i;
				}
				break;
			case 1:		//facing east (-x);
				for (int i = 0; i < range; i++) {
					idfound = world.getBlock(x-i, y, z);
					if (idfound == id)
						return i;
				}
				break;
			case 2:		//facing south (+z);
				for (int i = 0; i < range; i++) {
					idfound = world.getBlock(x, y, z+i);
					if (idfound == id)
						return i;
				}
				break;
			case 3:		//facing west (+x);
				for (int i = 0; i < range; i++) {
					idfound = world.getBlock(x+i, y, z);
					if (idfound == id)
						return i;
				}
				break;
		}
		return 0;
	}

	public static ForgeDirection checkForAdjNonCube(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				Block id2 = world.getBlock(dx, dy, dz);
				if (!id2.isOpaqueCube())
					return dir;
			}
		}
		return null;
	}

	/** Returns the direction in which a block of the specified ID was found.
	 *Returns null if not found. Args: World, x,y,z, id to search. */
	public static ForgeDirection checkForAdjBlock(World world, int x, int y, int z, Block id) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				Block id2 = world.getBlock(dx, dy, dz);
				if (id == id2)
					return dir;
			}
		}
		return null;
	}

	/** Returns the direction in which a block of the specified ID was found.
	 *Returns null if not found. Args: World, x,y,z, id to search. */
	public static Coordinate checkForAdjBlockWithCorners(World world, int x, int y, int z, Block id) {
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
						Block id2 = world.getBlock(dx, dy, dz);
						if (id == id2)
							return new Coordinate(i, j, k);
					}
				}
			}
		}
		return null;
	}

	/** Returns the direction in which a block of the specified ID was found.
	 *Returns -1 if not found. Args: World, x,y,z, id to search. */
	public static ForgeDirection checkForAdjBlock(World world, int x, int y, int z, Block id, int meta) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				Block id2 = world.getBlock(dx, dy, dz);
				int meta2 = world.getBlockMetadata(dx, dy, dz);
				if (id == id2 && meta2 == meta)
					return dir;
			}
		}
		return null;
	}

	/** Returns the direction in which a block of the specified material was found.
	 *Returns -1 if not found. Args: World, x,y,z, material to search. */
	public static ForgeDirection checkForAdjMaterial(World world, int x, int y, int z, Material mat) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				Material mat2 = getMaterial(world, dx, dy, dz);
				if (ReikaBlockHelper.matchMaterialsLoosely(mat, mat2))
					return dir;
			}
		}
		return null;
	}

	/** Returns the direction in which a source block of the specified liquid was found.
	 *Returns -1 if not found. Args: World, x,y,z, material (water/lava) to search. */
	public static ForgeDirection checkForAdjSourceBlock(World world, int x, int y, int z, Material mat) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				Material mat2 = getMaterial(world, dx, dy, dz);
				if (mat == mat2 && world.getBlockMetadata(dx, dy, dz) == 0)
					return dir;
			}
		}
		return null;
	}

	public static ForgeDirection checkForAdjTile(World world, int x, int y, int z, Class<? extends TileEntity> c, boolean inherit) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
				TileEntity te = world.getTileEntity(dx, dy, dz);
				if (te != null) {
					if (te.getClass() == c || (inherit && te.getClass().isAssignableFrom(c)))
						return dir;
				}
			}
		}
		return null;
	}

	/** Edits a block adjacent to the passed arguments, on the specified side.
	 *Args: World, x, y, z, side, id to change to, metadata to change to */
	public static void changeAdjBlock(World world, int x, int y, int z, ForgeDirection side, Block id, int meta) {
		int dx = x+side.offsetX;
		int dy = y+side.offsetY;
		int dz = z+side.offsetZ;
		if (world.checkChunksExist(dx, dy, dz, dx, dy, dz)) {
			world.setBlock(dx, dy, dz, id, meta, 3);
		}
	}

	/** Applies temperature effects to the environment. Args: World, x, y, z, temperature */
	public static void temperatureEnvironment(World world, int x, int y, int z, int temperature) {
		if (temperature < 0) {
			for (int i = 0; i < 6; i++) {
				ForgeDirection side = checkForAdjMaterial(world, x, y, z, Material.water);
				if (side != null)
					changeAdjBlock(world, x, y, z, side, Blocks.ice, 0);
			}
		}
		if (temperature > 450)	{ // Wood autoignition
			for (int i = 0; i < 4; i++) {
				if (getMaterial(world, x-i, y, z) == Material.wood)
					ignite(world, x-i, y, z);
				if (getMaterial(world, x+i, y, z) == Material.wood)
					ignite(world, x+i, y, z);
				if (getMaterial(world, x, y-i, z) == Material.wood)
					ignite(world, x, y-i, z);
				if (getMaterial(world, x, y+i, z) == Material.wood)
					ignite(world, x, y+i, z);
				if (getMaterial(world, x, y, z-i) == Material.wood)
					ignite(world, x, y, z-i);
				if (getMaterial(world, x, y, z+i) == Material.wood)
					ignite(world, x, y, z+i);
			}
		}
		if (temperature > 600)	{ // Wool autoignition
			for (int i = 0; i < 4; i++) {
				if (getMaterial(world, x-i, y, z) == Material.cloth)
					ignite(world, x-i, y, z);
				if (getMaterial(world, x+i, y, z) == Material.cloth)
					ignite(world, x+i, y, z);
				if (getMaterial(world, x, y-i, z) == Material.cloth)
					ignite(world, x, y-i, z);
				if (getMaterial(world, x, y+i, z) == Material.cloth)
					ignite(world, x, y+i, z);
				if (getMaterial(world, x, y, z-i) == Material.cloth)
					ignite(world, x, y, z-i);
				if (getMaterial(world, x, y, z+i) == Material.cloth)
					ignite(world, x, y, z+i);
			}
		}
		if (temperature > 300)	{ // TNT autoignition
			for (int i = 0; i < 4; i++) {
				if (getMaterial(world, x-i, y, z) == Material.tnt)
					ignite(world, x-i, y, z);
				if (getMaterial(world, x+i, y, z) == Material.tnt)
					ignite(world, x+i, y, z);
				if (getMaterial(world, x, y-i, z) == Material.tnt)
					ignite(world, x, y-i, z);
				if (getMaterial(world, x, y+i, z) == Material.tnt)
					ignite(world, x, y+i, z);
				if (getMaterial(world, x, y, z-i) == Material.tnt)
					ignite(world, x, y, z-i);
				if (getMaterial(world, x, y, z+i) == Material.tnt)
					ignite(world, x, y, z+i);
			}
		}
		if (temperature > 230)	{ // Grass/leaves/plant autoignition
			for (int i = 0; i < 4; i++) {
				if (flammable(world, x-i, y, z))
					if (getMaterial(world, x-i, y, z) == Material.leaves || getMaterial(world, x-i, y, z) == Material.vine || getMaterial(world, x-i, y, z) == Material.plants || getMaterial(world, x-i, y, z) == Material.web)
						ignite(world, x-i, y, z);
				if (flammable(world, x+i, y, z))
					if (getMaterial(world, x+i, y, z) == Material.leaves || getMaterial(world, x+i, y, z) == Material.vine || getMaterial(world, x+i, y, z) == Material.plants || getMaterial(world, x+i, y, z) == Material.web)
						ignite(world, x+i, y, z);
				if (flammable(world, x, y-i, z))
					if (getMaterial(world, x, y-i, z) == Material.leaves || getMaterial(world, x, y-i, z) == Material.vine || getMaterial(world, x, y-i, z) == Material.plants || getMaterial(world, x, y-i, z) == Material.web)
						ignite(world, x, y-i, z);
				if (flammable(world, x, y+i, z))
					if (getMaterial(world, x, y+i, z) == Material.leaves || getMaterial(world, x, y+i, z) == Material.vine || getMaterial(world, x, y+i, z) == Material.plants || getMaterial(world, x, y+i, z) == Material.web)
						ignite(world, x, y+i, z);
				if (flammable(world, x, y, z-i))
					if (getMaterial(world, x, y, z-i) == Material.leaves || getMaterial(world, x, y, z-i) == Material.vine || getMaterial(world, x, y, z-i) == Material.plants || getMaterial(world, x, y, z-i) == Material.web)
						ignite(world, x, y, z-i);
				if (flammable(world, x, y, z+i))
					if (getMaterial(world, x, y, z+i) == Material.leaves || getMaterial(world, x, y, z+i) == Material.vine || getMaterial(world, x, y, z+i) == Material.plants || getMaterial(world, x, y, z+i) == Material.web)
						ignite(world, x, y, z+i);
			}
		}

		if (temperature > 0)	{ // Melting snow/ice
			for (int i = 0; i < 3; i++) {
				if (getMaterial(world, x-i, y, z) == Material.ice)
					world.setBlock(x-i, y, z, Blocks.flowing_water);
				if (getMaterial(world, x+i, y, z) == Material.ice)
					world.setBlock(x+i, y, z, Blocks.flowing_water);
				if (getMaterial(world, x, y-i, z) == Material.ice)
					world.setBlock(x, y-i, z, Blocks.flowing_water);
				if (getMaterial(world, x, y+i, z) == Material.ice)
					world.setBlock(x, y+i, z, Blocks.flowing_water);
				if (getMaterial(world, x, y, z-i) == Material.ice)
					world.setBlock(x, y, z-i, Blocks.flowing_water);
				if (getMaterial(world, x, y, z+i) == Material.ice)
					world.setBlock(x, y, z+i, Blocks.flowing_water);
			}
		}
		if (temperature > 0)	{ // Melting snow/ice
			for (int i = 0; i < 3; i++) {
				if (getMaterial(world, x-i, y, z) == Material.snow)
					world.setBlockToAir(x-i, y, z);
				if (getMaterial(world, x+i, y, z) == Material.snow)
					world.setBlockToAir(x+i, y, z);
				if (getMaterial(world, x, y-i, z) == Material.snow)
					world.setBlockToAir(x, y-i, z);
				if (getMaterial(world, x, y+i, z) == Material.snow)
					world.setBlockToAir(x, y+i, z);
				if (getMaterial(world, x, y, z-i) == Material.snow)
					world.setBlockToAir(x, y, z-i);
				if (getMaterial(world, x, y, z+i) == Material.snow)
					world.setBlockToAir(x, y, z+i);

				if (getMaterial(world, x-i, y, z) == Material.craftedSnow)
					world.setBlockToAir(x-i, y, z);
				if (getMaterial(world, x+i, y, z) == Material.craftedSnow)
					world.setBlockToAir(x+i, y, z);
				if (getMaterial(world, x, y-i, z) == Material.craftedSnow)
					world.setBlockToAir(x, y-i, z);
				if (getMaterial(world, x, y+i, z) == Material.craftedSnow)
					world.setBlockToAir(x, y+i, z);
				if (getMaterial(world, x, y, z-i) == Material.craftedSnow)
					world.setBlockToAir(x, y, z-i);
				if (getMaterial(world, x, y, z+i) == Material.craftedSnow)
					world.setBlockToAir(x, y, z+i);
			}
		}
		if (temperature > 900)	{ // Melting sand, ground
			for (int i = 0; i < 3; i++) {
				if (getMaterial(world, x-i, y, z) == Material.sand)
					world.setBlock(x-i, y, z, Blocks.glass);
				if (getMaterial(world, x+i, y, z) == Material.sand)
					world.setBlock(x+i, y, z, Blocks.glass);
				if (getMaterial(world, x, y-i, z) == Material.sand)
					world.setBlock(x, y-i, z, Blocks.glass);
				if (getMaterial(world, x, y+i, z) == Material.sand)
					world.setBlock(x, y+i, z, Blocks.glass);
				if (getMaterial(world, x, y, z-i) == Material.sand)
					world.setBlock(x, y, z-i, Blocks.glass);
				if (getMaterial(world, x, y, z+i) == Material.sand)
					world.setBlock(x, y, z+i, Blocks.glass);

				if (getMaterial(world, x-i, y, z) == Material.ground)
					world.setBlock(x-i, y, z, Blocks.glass);
				if (getMaterial(world, x+i, y, z) == Material.ground)
					world.setBlock(x+i, y, z, Blocks.glass);
				if (getMaterial(world, x, y-i, z) == Material.ground)
					world.setBlock(x, y-i, z, Blocks.glass);
				if (getMaterial(world, x, y+i, z) == Material.ground)
					world.setBlock(x, y+i, z, Blocks.glass);
				if (getMaterial(world, x, y, z-i) == Material.ground)
					world.setBlock(x, y, z-i, Blocks.glass);
				if (getMaterial(world, x, y, z+i) == Material.ground)
					world.setBlock(x, y, z+i, Blocks.glass);

				if (getMaterial(world, x-i, y, z) == Material.grass)
					world.setBlock(x-i, y, z, Blocks.glass);
				if (getMaterial(world, x+i, y, z) == Material.grass)
					world.setBlock(x+i, y, z, Blocks.glass);
				if (getMaterial(world, x, y-i, z) == Material.grass)
					world.setBlock(x, y-i, z, Blocks.glass);
				if (getMaterial(world, x, y+i, z) == Material.grass)
					world.setBlock(x, y+i, z, Blocks.glass);
				if (getMaterial(world, x, y, z-i) == Material.grass)
					world.setBlock(x, y, z-i, Blocks.glass);
				if (getMaterial(world, x, y, z+i) == Material.grass)
					world.setBlock(x, y, z+i, Blocks.glass);
			}
		}
		if (temperature > 1500)	{ // Melting rock
			for (int i = 0; i < 3; i++) {
				if (isMeltable(world, x-i, y, z, temperature))
					world.setBlock(x-i, y, z, Blocks.flowing_lava);
				if (isMeltable(world, x+i, y, z, temperature))
					world.setBlock(x+i, y, z, Blocks.flowing_lava);
				if (isMeltable(world, x, y-i, z, temperature))
					world.setBlock(x, y-i, z, Blocks.flowing_lava);
				if (isMeltable(world, x, y+i, z, temperature))
					world.setBlock(x, y+i, z, Blocks.flowing_lava);
				if (isMeltable(world, x, y, z-i, temperature))
					world.setBlock(x, y, z-i, Blocks.flowing_lava);
				if (isMeltable(world, x, y, z+i, temperature))
					world.setBlock(x, y, z+i, Blocks.flowing_lava);
			}
		}
	}

	public static boolean isMeltable(World world, int x, int y, int z, int temperature) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.air || b == Blocks.bedrock)
			return false;
		if (b == Blocks.obsidian) {
			return temperature > 1800;
		}
		Material m = b.getMaterial();
		if (m == Material.rock) {
			return temperature > 1500;
		}
		if (m == Material.iron) {
			return temperature > 2000;
		}
		return false;
	}

	/** Surrounds the block with fire. Args: World, x, y, z */
	public static void ignite(World world, int x, int y, int z) {
		if (world.getBlock(x-1, y, z) == Blocks.air)
			world.setBlock(x-1, y, z, Blocks.fire);
		if (world.getBlock(x+1, y, z) == Blocks.air)
			world.setBlock(x+1, y, z, Blocks.fire);
		if (world.getBlock(x, y-1, z) == Blocks.air)
			world.setBlock(x, y-1, z, Blocks.fire);
		if (world.getBlock(x, y+1, z) == Blocks.air)
			world.setBlock(x, y+1, z, Blocks.fire);
		if (world.getBlock(x, y, z-1) == Blocks.air)
			world.setBlock(x, y, z-1, Blocks.fire);
		if (world.getBlock(x, y, z+1) == Blocks.air)
			world.setBlock(x, y, z+1, Blocks.fire);
	}

	/** Returns the number of water blocks directly and continuously above the passed coordinates.
	 *Returns -1 if invalid liquid specified. Args: World, x, y, z */
	public static int getDepth(World world, int x, int y, int z, String liq) {
		int i = 1;
		if (liq == "water") {
			while (world.getBlock(x, y+i, z) == Blocks.flowing_water || world.getBlock(x, y+i, z) == Blocks.water) {
				i++;
			}
			return (i-1);
		}
		if (liq == "lava") {
			while (world.getBlock(x, y+i, z) == Blocks.flowing_lava || world.getBlock(x, y+i, z) == Blocks.lava) {
				i++;
			}
			return (i-1);
		}
		return -1;
	}

	/** Returns true if the block ID is one associated with caves, like air, cobwebs,
	 *spawners, mushrooms, etc. Args: Block ID */
	public static boolean caveBlock(Block id) {
		if (id == Blocks.air || id == Blocks.flowing_water || id == Blocks.water || id == Blocks.flowing_lava ||
				id == Blocks.lava || id == Blocks.web || id == Blocks.mob_spawner || id == Blocks.red_mushroom ||
				id == Blocks.brown_mushroom)
			return true;
		return false;
	}

	/** Performs machine overheat effects (primarily intended for RotaryCraft).
	 *Args: World, x, y, z, item drop id, item drop metadata, min drops, max drops,
	 *spark particles yes/no, number-of-sparks multiplier (default 20-40),
	 *flaming explosion yes/no, smoking explosion yes/no, explosion force (0 for none) */
	public static void overheat(World world, int x, int y, int z, ItemStack drop, int mindrops, int maxdrops, boolean sparks, float sparkmultiplier, boolean flaming, boolean smoke, float force) {
		world.setBlock(x, y, z, Blocks.air);
		if (force > 0 && !world.isRemote) {
			if (flaming)
				world.newExplosion(null, x, y, z, force, true, smoke);
			else
				world.createExplosion(null, x, y, z, force, smoke);
		}
		int numsparks = rand.nextInt(20)+20;
		numsparks *= sparkmultiplier;
		if (sparks)
			for (int i = 0; i < numsparks; i++)
				world.spawnParticle("lava", x+rand.nextFloat(), y+1, z+rand.nextFloat(), 0, 0, 0);
		if (drop != null) {
			ItemStack scrap = drop.copy();
			int numdrops = rand.nextInt(1+maxdrops-mindrops)+mindrops;
			if (!world.isRemote) {
				for (int i = 0; i < numdrops; i++) {
					EntityItem ent = new EntityItem(world, x+rand.nextFloat(), y+0.5, z+rand.nextFloat(), scrap);
					ent.motionX = -0.2+0.4*rand.nextFloat();
					ent.motionY = 0.5*rand.nextFloat();
					ent.motionZ = -0.2+0.4*rand.nextFloat();
					world.spawnEntityInWorld(ent);
					ent.velocityChanged = true;
				}
			}
		}
	}

	/** Takes a specified amount of XP and splits it randomly among a bunch of orbs.
	 *Args: World, x, y, z, amount */
	public static void splitAndSpawnXP(World world, double x, double y, double z, int xp) {
		splitAndSpawnXP(world, x, y, z, xp, 6000);
	}

	/** Takes a specified amount of XP and splits it randomly among a bunch of orbs.
	 *Args: World, x, y, z, amount, life */
	public static void splitAndSpawnXP(World world, double x, double y, double z, int xp, int life) {
		int max = xp/5+1;

		while (xp > 0) {
			int value = rand.nextInt(max)+1;
			while (value > xp)
				value = rand.nextInt(max)+1;
			xp -= value;
			EntityXPOrb orb = new EntityXPOrb(world, x, y, z, value);
			orb.motionX = -0.2+0.4*rand.nextFloat();
			orb.motionY = 0.3*rand.nextFloat();
			orb.motionZ = -0.2+0.4*rand.nextFloat();
			orb.xpOrbAge = 6000-life;
			if (!world.isRemote) {
				orb.velocityChanged = true;
				world.spawnEntityInWorld(orb);
			}
		}
	}

	/** Returns true if the coordinate specified is a lava source block and would be recreated according to the lava-duplication rules
	 *that existed for a short time in Beta 1.9. Args: World, x, y, z */
	public static boolean is1p9InfiniteLava(World world, int x, int y, int z) {
		if (getMaterial(world, x, y, z) != Material.lava || world.getBlockMetadata(x, y, z) != 0)
			return false;
		if (getMaterial(world, x+1, y, z) != Material.lava || world.getBlockMetadata(x+1, y, z) != 0)
			return false;
		if (getMaterial(world, x, y, z+1) != Material.lava || world.getBlockMetadata(x, y, z+1) != 0)
			return false;
		if (getMaterial(world, x-1, y, z) != Material.lava || world.getBlockMetadata(x-1, y, z) != 0)
			return false;
		if (getMaterial(world, x, y, z-1) != Material.lava || world.getBlockMetadata(x, y, z-1) != 0)
			return false;
		return true;
	}

	/** Returns the y-coordinate of the top non-air block at the given xz coordinates, at or
	 *below the specified y-coordinate. Returns -1 if none. Args: World, x, y, z */
	public static int findTopBlockBelowY(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		while (b == Blocks.air && y >= 0) {
			y--;
			b = world.getBlock(x, y, z);
		}
		return y;
	}

	/** Returns true if the coordinate is a liquid source Blocks. Args: World, x, y, z */
	public static boolean isLiquidSourceBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.air)
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		if (b instanceof BlockFluidFinite)
			return meta == 7;
		if (meta != 0)
			return false;
		return b instanceof BlockLiquid || b instanceof BlockFluidBase;
	}

	/** Breaks a contiguous area of blocks recursively (akin to a fill tool in image editors).
	 *Args: World, start x, start y, start z, id, metadata (-1 for any) */
	public static void recursiveBreak(World world, int x, int y, int z, Block id, int meta) {
		if (id == Blocks.air)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
			return;
		int metad = world.getBlockMetadata(x, y, z);
		ReikaItemHelper.dropItems(world, x, y, z, id.getDrops(world, x, y, z, metad, 0));
		ReikaSoundHelper.playBreakSound(world, x, y, z, id);
		world.setBlockToAir(x, y, z);
		world.markBlockForUpdate(x, y, z);
		recursiveBreak(world, x+1, y, z, id, meta);
		recursiveBreak(world, x-1, y, z, id, meta);
		recursiveBreak(world, x, y+1, z, id, meta);
		recursiveBreak(world, x, y-1, z, id, meta);
		recursiveBreak(world, x, y, z+1, id, meta);
		recursiveBreak(world, x, y, z-1, id, meta);
	}

	/** Like the ordinary recursive break but with a spherical bounded volume. Args: World, x, y, z,
	 *id to replace, metadata to replace (-1 for any), origin x,y,z, max radius */
	public static void recursiveBreakWithinSphere(World world, int x, int y, int z, Block id, int meta, int x0, int y0, int z0, double r) {
		if (id == Blocks.air)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
			return;
		if (ReikaMathLibrary.py3d(x-x0, y-y0, z-z0) > r)
			return;
		int metad = world.getBlockMetadata(x, y, z);
		ReikaItemHelper.dropItems(world, x, y, z, id.getDrops(world, x, y, z, metad, 0));
		ReikaSoundHelper.playBreakSound(world, x, y, z, id);
		world.setBlockToAir(x, y, z);
		world.markBlockForUpdate(x, y, z);
		recursiveBreakWithinSphere(world, x+1, y, z, id, meta, x0, y0, z0, r);
		recursiveBreakWithinSphere(world, x-1, y, z, id, meta, x0, y0, z0, r);
		recursiveBreakWithinSphere(world, x, y+1, z, id, meta, x0, y0, z0, r);
		recursiveBreakWithinSphere(world, x, y-1, z, id, meta, x0, y0, z0, r);
		recursiveBreakWithinSphere(world, x, y, z+1, id, meta, x0, y0, z0, r);
		recursiveBreakWithinSphere(world, x, y, z-1, id, meta, x0, y0, z0, r);
	}

	/** Like the ordinary recursive break but with a bounded volume. Args: World, x, y, z,
	 *id to replace, metadata to replace (-1 for any), min x,y,z, max x,y,z */
	public static void recursiveBreakWithBounds(World world, int x, int y, int z, Block id, int meta, int x1, int y1, int z1, int x2, int y2, int z2) {
		if (id == Blocks.air)
			return;
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
			return;
		int metad = world.getBlockMetadata(x, y, z);
		ReikaItemHelper.dropItems(world, x, y, z, id.getDrops(world, x, y, z, metad, 0));
		ReikaSoundHelper.playBreakSound(world, x, y, z, id);
		world.setBlockToAir(x, y, z);
		world.markBlockForUpdate(x, y, z);
		recursiveBreakWithBounds(world, x+1, y, z, id, meta, x1, y1, z1, x2, y2, z2);
		recursiveBreakWithBounds(world, x-1, y, z, id, meta, x1, y1, z1, x2, y2, z2);
		recursiveBreakWithBounds(world, x, y+1, z, id, meta, x1, y1, z1, x2, y2, z2);
		recursiveBreakWithBounds(world, x, y-1, z, id, meta, x1, y1, z1, x2, y2, z2);
		recursiveBreakWithBounds(world, x, y, z+1, id, meta, x1, y1, z1, x2, y2, z2);
		recursiveBreakWithBounds(world, x, y, z-1, id, meta, x1, y1, z1, x2, y2, z2);
	}

	/** Recursively fills a contiguous area of one block type with another, akin to a fill tool.
	 *Args: World, start x, start y, start z, id to replace, id to fill with,
	 *metadata to replace (-1 for any), metadata to fill with */
	public static void recursiveFill(World world, int x, int y, int z, Block id, Block idto, int meta, int metato) {
		if (world.getBlock(x, y, z) != id)
			return;
		if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
			return;
		int metad = world.getBlockMetadata(x, y, z);
		world.setBlock(x, y, z, idto, metato, 3);
		world.markBlockForUpdate(x, y, z);
		recursiveFill(world, x+1, y, z, id, idto, meta, metato);
		recursiveFill(world, x-1, y, z, id, idto, meta, metato);
		recursiveFill(world, x, y+1, z, id, idto, meta, metato);
		recursiveFill(world, x, y-1, z, id, idto, meta, metato);
		recursiveFill(world, x, y, z+1, id, idto, meta, metato);
		recursiveFill(world, x, y, z-1, id, idto, meta, metato);
	}

	/** Like the ordinary recursive fill but with a bounded volume. Args: World, x, y, z,
	 *id to replace, id to fill with, metadata to replace (-1 for any),
	 *metadata to fill with, min x,y,z, max x,y,z */
	public static void recursiveFillWithBounds(World world, int x, int y, int z, Block id, Block idto, int meta, int metato, int x1, int y1, int z1, int x2, int y2, int z2) {
		if (x < x1 || y < y1 || z < z1 || x > x2 || y > y2 || z > z2)
			return;
		if (world.getBlock(x, y, z) != id)
			return;
		if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
			return;
		int metad = world.getBlockMetadata(x, y, z);
		world.setBlock(x, y, z, idto, metato, 3);
		world.markBlockForUpdate(x, y, z);
		recursiveFillWithBounds(world, x+1, y, z, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
		recursiveFillWithBounds(world, x-1, y, z, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
		recursiveFillWithBounds(world, x, y+1, z, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
		recursiveFillWithBounds(world, x, y-1, z, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
		recursiveFillWithBounds(world, x, y, z+1, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
		recursiveFillWithBounds(world, x, y, z-1, id, idto, meta, metato, x1, y1, z1, x2, y2, z2);
	}

	/** Like the ordinary recursive fill but with a spherical bounded volume. Args: World, x, y, z,
	 *id to replace, id to fill with, metadata to replace (-1 for any),
	 *metadata to fill with, origin x,y,z, max radius */
	public static void recursiveFillWithinSphere(World world, int x, int y, int z, Block id, Block idto, int meta, int metato, int x0, int y0, int z0, double r) {
		/*DragonAPICore.log(world.getBlock(x, y, z)+" & "+id+" @ "+x0+", "+y0+", "+z0);
		DragonAPICore.log(world.getBlockMetadata(x, y, z)+" & "+meta+" @ "+x0+", "+y0+", "+z0);
		DragonAPICore.log(ReikaMathLibrary.py3d(x-x0, y-y0, z-z0)+" & "+r+" @ "+x0+", "+y0+", "+z0);*/
		if (world.getBlock(x, y, z) != id)
			return;
		if (meta != world.getBlockMetadata(x, y, z) && meta != -1)
			return;
		if (ReikaMathLibrary.py3d(x-x0, y-y0, z-z0) > r)
			return;
		int metad = world.getBlockMetadata(x, y, z);
		world.setBlock(x, y, z, idto, metato, 3);
		world.markBlockForUpdate(x, y, z);
		recursiveFillWithinSphere(world, x+1, y, z, id, idto, meta, metato, x0, y0, z0, r);
		recursiveFillWithinSphere(world, x-1, y, z, id, idto, meta, metato, x0, y0, z0, r);
		recursiveFillWithinSphere(world, x, y+1, z, id, idto, meta, metato, x0, y0, z0, r);
		recursiveFillWithinSphere(world, x, y-1, z, id, idto, meta, metato, x0, y0, z0, r);
		recursiveFillWithinSphere(world, x, y, z+1, id, idto, meta, metato, x0, y0, z0, r);
		recursiveFillWithinSphere(world, x, y, z-1, id, idto, meta, metato, x0, y0, z0, r);
	}

	/** Returns true if there is a clear line of sight between two points. Args: World, Start x,y,z, End x,y,z
	 *NOTE: If one point is a block, use canBlockSee instead, as this method will always return false. */
	public static boolean lineOfSight(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
		if (world.isRemote)
			;//return false;
		Vec3 v1 = Vec3.createVectorHelper(x1, y1, z1);
		Vec3 v2 = Vec3.createVectorHelper(x2, y2, z2);
		return (world.rayTraceBlocks(v1, v2) == null);
	}

	/** Returns true if there is a clear line of sight between two entites. Args: World, Entity 1, Entity 2 */
	public static boolean lineOfSight(World world, Entity e1, Entity e2) {
		Vec3 v1 = Vec3.createVectorHelper(e1.posX, e1.posY+e1.getEyeHeight(), e1.posZ);
		Vec3 v2 = Vec3.createVectorHelper(e2.posX, e2.posY+e2.getEyeHeight(), e2.posZ);
		return (world.rayTraceBlocks(v1, v2) == null);
	}

	/** Returns true if a block can see an point. Args: World, block x,y,z, Point x,y,z, Max Range */
	public static boolean canBlockSee(World world, int x, int y, int z, double x0, double y0, double z0, double range) {
		Block locid = world.getBlock(x, y, z);
		range += 2;
		for (int k = 0; k < 10; k++) {
			float a = 0; float b = 0; float c = 0;
			switch(k) {
				case 1:
					a = 1;
					break;
				case 2:
					b = 1;
					break;
				case 3:
					a = 1;
					b = 1;
					break;
				case 4:
					c = 1;
					break;
				case 5:
					a = 1;
					c = 1;
					break;
				case 6:
					b = 1;
					c = 1;
					break;
				case 7:
					a = 1;
					b = 1;
					c = 1;
					break;
				case 8:
					a = 0.5F;
					b = 0.5F;
					c = 0.5F;
					break;
				case 9:
					b = 0.5F;
					break;
			}
			for (float i = 0; i <= range; i += 0.25) {
				Vec3 vec2 = ReikaVectorHelper.getVec2Pt(x+a, y+b, z+c, x0, y0, z0).normalize();
				vec2 = ReikaVectorHelper.scaleVector(vec2, i);
				vec2.xCoord += x0;
				vec2.yCoord += y0;
				vec2.zCoord += z0;
				//ReikaColorAPI.write(String.format("%f -->  %.3f,  %.3f, %.3f", i, vec2.xCoord, vec2.yCoord, vec2.zCoord));
				int dx = MathHelper.floor_double(vec2.xCoord);
				int dy = MathHelper.floor_double(vec2.yCoord);
				int dz = MathHelper.floor_double(vec2.zCoord);
				Block id = world.getBlock(dx, dy, dz);
				if (dx == x && dy == y && dz == z) {
					//ReikaColorAPI.writeCoords(world, (int)vec2.xCoord, (int)vec2.yCoord, (int)vec2.zCoord);
					return true;
				}
				else if (id != locid && ReikaBlockHelper.isCollideable(world, dx, dy, dz) && !softBlocks(world, dx, dy, dz)) {
					i = (float)(range+1); //Hard loop break
				}
			}
		}
		return false;
	}

	/** a, b, c, are the "internal offset" of the vector origins */
	public static boolean rayTraceTwoBlocks(World world, int x1, int y1, int z1, int x2, int y2, int z2, float a, float b, float c) {
		Vec3 vec1 = Vec3.createVectorHelper(x1+a, y1+b, z1+c);
		Vec3 vec2 = Vec3.createVectorHelper(x2+a, y2+b, z2+c);
		Vec3 ray = ReikaVectorHelper.subtract(vec1, vec2);
		double dx = vec2.xCoord-vec1.xCoord;
		double dy = vec2.yCoord-vec1.yCoord;
		double dz = vec2.zCoord-vec1.zCoord;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		for (double d = 0.25; d <= dd; d += 0.5) {
			Vec3 vec0 = ReikaVectorHelper.scaleVector(ray, d);
			Vec3 vec = ReikaVectorHelper.scaleVector(ray, d-0.25);
			vec0.xCoord += vec1.xCoord;
			vec0.yCoord += vec1.yCoord;
			vec0.zCoord += vec1.zCoord;
			vec.xCoord += vec1.xCoord;
			vec.yCoord += vec1.yCoord;
			vec.zCoord += vec1.zCoord;
			MovingObjectPosition mov = world.rayTraceBlocks(vec, vec0);
			if (mov != null) {
				if (mov.typeOfHit == MovingObjectType.BLOCK) {
					int bx = mov.blockX;
					int by = mov.blockY;
					int bz = mov.blockZ;
					if (bx == x1 && by == y1 && bz == z1) {

					}
					else if (bx == x2 && by == y2 && bz == z2) {

					}
					else {
						if (!softBlocks(world, bx, by, bz) && ReikaBlockHelper.isCollideable(world, bx, by, bz))
							return false;
					}
				}
			}
		}
		return true;
	}

	public static boolean rayTraceTwoBlocks(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
		return rayTraceTwoBlocks(world, x1, y1, z1, x2, y2, z2, 0.5F, 0.5F, 0.5F);
	}

	/** Returns true if the entity can see a block, or if it could be moved to a position where it could see the block.
	 *Args: World, Block x,y,z, Entity, Max Move Distance
	 *DO NOT USE THIS - CPU INTENSIVE TO ALL HELL! */
	public static boolean canSeeOrMoveToSeeBlock(World world, int x, int y, int z, Entity ent, double r) {
		double d = 4;//+ReikaMathLibrary.py3d(x-ent.posX, y-ent.posY, z-ent.posZ);
		if (canBlockSee(world, x, y, z, ent.posX, ent.posY, ent.posZ, d))
			return true;
		double xmin; double ymin; double zmin;
		double xmax; double ymax; double zmax;
		double[] pos = new double[3];
		boolean[] signs = new boolean[3];
		boolean[] signs2 = new boolean[3];
		signs[0] = (ReikaMathLibrary.isSameSign(ent.posX, x));
		signs[1] = (ReikaMathLibrary.isSameSign(ent.posY, y));
		signs[2] = (ReikaMathLibrary.isSameSign(ent.posZ, z));
		for (double i = ent.posX-r; i <= ent.posX+r; i += 0.5) {
			for (double j = ent.posY-r; j <= ent.posY+r; j += 0.5) {
				for (double k = ent.posZ-r; k <= ent.posZ+r; k += 0.5) {
					if (canBlockSee(world, x, y, z, ent.posX+i, ent.posY+j, ent.posZ+k, d))
						return true;
				}
			}
		}
		/*
    	for (double i = ent.posX; i > ent.posX-r; i -= 0.5) {
    		Block b = world.getBlock((int)i, (int)ent.posY, (int)ent.posZ);
    		if (isCollideable(world, (int)i, (int)ent.posY, (int)ent.posZ)) {
    			xmin = i+Blocks.blocksList[id].getBlockBoundsMaxX();
    		}
    	}
    	for (double i = ent.posX; i < ent.posX+r; i += 0.5) {
    		Block b = world.getBlock((int)i, (int)ent.posY, (int)ent.posZ);
    		if (isCollideable(world, (int)i, (int)ent.posY, (int)ent.posZ)) {
    			xmax = i+Blocks.blocksList[id].getBlockBoundsMinX();
    		}
    	}
    	for (double i = ent.posY; i > ent.posY-r; i -= 0.5) {
    		Block b = world.getBlock((int)ent.posX, (int)i, (int)ent.posZ);
    		if (isCollideable(world, (int)ent.posX, (int)i, (int)ent.posZ)) {
    			ymin = i+Blocks.blocksList[id].getBlockBoundsMaxX();
    		}
    	}
    	for (double i = ent.posY; i < ent.posY+r; i += 0.5) {
    		Block b = world.getBlock((int)ent.posX, (int)i, (int)ent.posZ);
    		if (isCollideable(world, (int)ent.posX, (int)i, (int)ent.posZ)) {
    			ymax = i+Blocks.blocksList[id].getBlockBoundsMinX();
    		}
    	}
    	for (double i = ent.posZ; i > ent.posZ-r; i -= 0.5) {
    		Block b = world.getBlock((int)ent.posX, (int)ent.posY, (int)i);
    		if (isCollideable(world, (int)ent.posX, (int)ent.posY, (int)i)) {
    			zmin = i+Blocks.blocksList[id].getBlockBoundsMaxX();
    		}
    	}
    	for (double i = ent.posZ; i < ent.posZ+r; i += 0.5) {
    		Block b = world.getBlock((int)ent.posX, (int)ent.posY, (int)i);
    		if (isCollideable(world, (int)ent.posX, (int)ent.posY, (int)i)) {
    			zmax = i+Blocks.blocksList[id].getBlockBoundsMinX();
    		}
    	}*/
		signs2[0] = (ReikaMathLibrary.isSameSign(pos[0], x));
		signs2[1] = (ReikaMathLibrary.isSameSign(pos[1], y));
		signs2[2] = (ReikaMathLibrary.isSameSign(pos[2], z));
		if (signs[0] != signs2[0] || signs[1] != signs2[1] || signs[2] != signs2[2]) //Cannot pull the item "Across" (so that it moves away)
			return false;
		return false;
	}

	/*
	public static boolean lenientSeeThrough(World world, double x, double y, double z, double x0, double y0, double z0) {
		MovingObjectPosition pos;
		Vec3 par1Vec3 = Vec3.createVectorHelper(x, y, z);
		Vec3 par2Vec3 = Vec3.createVectorHelper(x0, y0, z0);
		if (!Double.isNaN(par1Vec3.xCoord) && !Double.isNaN(par1Vec3.yCoord) && !Double.isNaN(par1Vec3.zCoord)) {
			if (!Double.isNaN(par2Vec3.xCoord) && !Double.isNaN(par2Vec3.yCoord) && !Double.isNaN(par2Vec3.zCoord)) {
				int var5 = MathHelper.floor_double(par2Vec3.xCoord);
				int var6 = MathHelper.floor_double(par2Vec3.yCoord);
				int var7 = MathHelper.floor_double(par2Vec3.zCoord);
				int var8 = MathHelper.floor_double(par1Vec3.xCoord);
				int var9 = MathHelper.floor_double(par1Vec3.yCoord);
				int var10 = MathHelper.floor_double(par1Vec3.zCoord);
				Block var11 = world.getBlock(var8, var9, var10);
				int var12 = world.getBlockMetadata(var8, var9, var10);
				Block var13 = var11;
				//ReikaColorAPI.write(var11);
				if (var13 != null && (var11 != Blocks.air && !softBlocks(var11) && (var11 != Blocks.leaves) && (var11 != Blocks.web)) && var13.canCollideCheck(var12, false)) {
					MovingObjectPosition var14 = var13.collisionRayTrace(world, var8, var9, var10, par1Vec3, par2Vec3);
					if (var14 != null)
						pos = var14;
				}
				var11 = 200;
				while (var11-- >= 0) {
					if (Double.isNaN(par1Vec3.xCoord) || Double.isNaN(par1Vec3.yCoord) || Double.isNaN(par1Vec3.zCoord))
						pos = null;
					if (var8 == var5 && var9 == var6 && var10 == var7)
						pos = null;
					boolean var39 = true;
					boolean var40 = true;
					boolean var41 = true;
					double var15 = 999.0D;
					double var17 = 999.0D;
					double var19 = 999.0D;
					if (var5 > var8)
						var15 = var8+1.0D;
					else if (var5 < var8)
						var15 = var8+0.0D;
					else
						var39 = false;
					if (var6 > var9)
						var17 = var9+1.0D;
					else if (var6 < var9)
						var17 = var9+0.0D;
					else
						var40 = false;
					if (var7 > var10)
						var19 = var10+1.0D;
					else if (var7 < var10)
						var19 = var10+0.0D;
					else
						var41 = false;
					double var21 = 999.0D;
					double var23 = 999.0D;
					double var25 = 999.0D;
					double var27 = par2Vec3.xCoord - par1Vec3.xCoord;
					double var29 = par2Vec3.yCoord - par1Vec3.yCoord;
					double var31 = par2Vec3.zCoord - par1Vec3.zCoord;
					if (var39)
						var21 = (var15 - par1Vec3.xCoord) / var27;
					if (var40)
						var23 = (var17 - par1Vec3.yCoord) / var29;
					if (var41)
						var25 = (var19 - par1Vec3.zCoord) / var31;
					boolean var33 = false;
					byte var42;
					if (var21 < var23 && var21 < var25) {
						if (var5 > var8)
							var42 = 4;
						else
							var42 = 5;
						par1Vec3.xCoord = var15;
						par1Vec3.yCoord += var29*var21;
						par1Vec3.zCoord += var31*var21;
					}
					else if (var23 < var25) {
						if (var6 > var9)
							var42 = 0;
						else
							var42 = 1;
						par1Vec3.xCoord += var27*var23;
						par1Vec3.yCoord = var17;
						par1Vec3.zCoord += var31*var23;
					}
					else {
						if (var7 > var10)
							var42 = 2;
						else
							var42 = 3;

						par1Vec3.xCoord += var27*var25;
						par1Vec3.yCoord += var29*var25;
						par1Vec3.zCoord = var19;
					}
					Vec3 var34 = Vec3.createVectorHelper(par1Vec3.xCoord, par1Vec3.yCoord, par1Vec3.zCoord);
					var8 = (int)(var34.xCoord = MathHelper.floor_double(par1Vec3.xCoord));
					if (var42 == 5) {
						--var8;
						++var34.xCoord;
					}
					var9 = (int)(var34.yCoord = MathHelper.floor_double(par1Vec3.yCoord));
					if (var42 == 1) {
						--var9;
						++var34.yCoord;
					}
					var10 = (int)(var34.zCoord = MathHelper.floor_double(par1Vec3.zCoord));
					if (var42 == 3) {
						--var10;
						++var34.zCoord;
					}
					Block var35 = world.getBlock(var8, var9, var10);
					int var36 = world.getBlockMetadata(var8, var9, var10);
					Block var37 = var35;
					if (var35 != Blocks.air && var37.canCollideCheck(var36, false)) {
						MovingObjectPosition var38 = var37.collisionRayTrace(world, var8, var9, var10, par1Vec3, par2Vec3);
						if (var38 != null)
							pos = var38;
					}
				}
				pos = null;
			}
			else
				pos = null;
		}
		else
			pos = null;
		return (pos == null);
	}*/

	/** Returns true if the specified corner has at least one air block adjacent to it,
	 *but is not surrounded by air on all sides or in the void. Args: World, x, y, z */
	public static boolean cornerHasAirAdjacent(World world, int x, int y, int z) {
		if (y <= 0)
			return false;
		int airs = 0;
		if (world.getBlock(x, y, z) == Blocks.air)
			airs++;
		if (world.getBlock(x-1, y, z) == Blocks.air)
			airs++;
		if (world.getBlock(x, y, z-1) == Blocks.air)
			airs++;
		if (world.getBlock(x-1, y, z-1) == Blocks.air)
			airs++;
		if (world.getBlock(x, y-1, z) == Blocks.air)
			airs++;
		if (world.getBlock(x-1, y-1, z) == Blocks.air)
			airs++;
		if (world.getBlock(x, y-1, z-1) == Blocks.air)
			airs++;
		if (world.getBlock(x-1, y-1, z-1) == Blocks.air)
			airs++;
		return (airs > 0 && airs != 8);
	}

	/** Returns true if the specified corner has at least one nonopaque block adjacent to it,
	 *but is not surrounded by air on all sides or in the void. Args: World, x, y, z */
	public static boolean cornerHasTransAdjacent(World world, int x, int y, int z) {
		if (y <= 0)
			return false;
		Block id;
		int airs = 0;
		boolean nonopq = false;
		id = world.getBlock(x, y, z);
		if (id == Blocks.air)
			airs++;
		else if (!id.isOpaqueCube())
			nonopq = true;
		id = world.getBlock(x-1, y, z);
		if (id == Blocks.air)
			airs++;
		else if (!id.isOpaqueCube())
			nonopq = true;
		id = world.getBlock(x, y, z-1);
		if (id == Blocks.air)
			airs++;
		else if (!id.isOpaqueCube())
			nonopq = true;
		id = world.getBlock(x-1, y, z-1);
		if (id == Blocks.air)
			airs++;
		else if (!id.isOpaqueCube())
			nonopq = true;
		id = world.getBlock(x, y-1, z);
		if (id == Blocks.air)
			airs++;
		else if (!id.isOpaqueCube())
			nonopq = true;
		id = world.getBlock(x-1, y-1, z);
		if (id == Blocks.air)
			airs++;
		else if (!id.isOpaqueCube())
			nonopq = true;
		id = world.getBlock(x, y-1, z-1);
		if (id == Blocks.air)
			airs++;
		else if (!id.isOpaqueCube())
			nonopq = true;
		id = world.getBlock(x-1, y-1, z-1);
		if (id == Blocks.air)
			airs++;
		else if (!id.isOpaqueCube())
			nonopq = true;
		return (airs != 8 && nonopq);
	}

	/** Spawns a line of particles between two points. Args: World, start x,y,z, end x,y,z, particle type, particle speed x,y,z, number of particles */
	public static void spawnParticleLine(World world, double x1, double y1, double z1, double x2, double y2, double z2, String name, double vx, double vy, double vz, int spacing) {
		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		double sx = dx/spacing;
		double sy = dy/spacing;
		double sz = dy/spacing;
		double[][] parts = new double[spacing+1][3];
		for (int i = 0; i <= spacing; i++) {
			parts[i][0] = i*sx+x1;
			parts[i][1] = i*sy+y1;
			parts[i][2] = i*sz+z1;
		}
		for (int i = 0; i < parts.length; i++) {
			world.spawnParticle(name, parts[i][0], parts[i][1], parts[i][2], vx, vy, vz);
		}
	}

	/** Checks if a liquid block is part of a column (has same liquid above and below and none of them are source blocks).
	 *Args: World, x, y, z */
	public static boolean isLiquidAColumn(World world, int x, int y, int z) {
		Fluid f = getFluid(world, x, y, z);
		if (f == null)
			return false;
		if (isLiquidSourceBlock(world, x, y, z))
			return false;
		if (getFluid(world, x, y+1, z) != f)
			return false;
		if (isLiquidSourceBlock(world, x, y+1, z))
			return false;
		if (getFluid(world, x, y-1, z) != f)
			return false;
		if (isLiquidSourceBlock(world, x, y-1, z))
			return false;
		return true;
	}

	public static Fluid getFluid(World world, int x, int y, int z) {
		return FluidRegistry.lookupFluidForBlock(world.getBlock(x, y, z));
	}

	/** Updates all blocks adjacent to the coordinate given. Args: World, x, y, z */
	public static void causeAdjacentUpdates(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		world.notifyBlocksOfNeighborChange(x, y, z, b);
	}

	public static ArrayList<ItemStack> getDropsAt(World world, int x, int y, int z, int fortune, EntityPlayer ep) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.air)
			return new ArrayList();
		int meta = world.getBlockMetadata(x, y, z);
		ArrayList<ItemStack> li = b.getDrops(world, x, y, z, meta, fortune);
		if (ep != null) {
			HarvestDropsEvent evt = new HarvestDropsEvent(x, y, z, world, b, meta, fortune, 1F, li, ep, false);
			MinecraftForge.EVENT_BUS.post(evt);
			li = evt.drops;
		}
		return li;
	}

	/** Drops all items from a given Blocks. Args: World, x, y, z, fortune level */
	public static ArrayList<ItemStack> dropBlockAt(World world, int x, int y, int z, int fortune, EntityPlayer ep) {
		ArrayList<ItemStack> li = getDropsAt(world, x, y, z, fortune, ep);
		ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, li);
		return li;
	}

	/** Drops all items from a given block with no fortune effect. Args: World, x, y, z */
	public static ArrayList<ItemStack> dropBlockAt(World world, int x, int y, int z, EntityPlayer ep) {
		return dropBlockAt(world, x, y, z, 0, ep);
	}

	/** Sets the biome type at an xz column. Args: World, x, z, biome */
	public static void setBiomeForXZ(World world, int x, int z, BiomeGenBase biome) {
		Chunk ch = world.getChunkFromBlockCoords(x, z);

		int ax = x-ch.xPosition*16;
		int az = z-ch.zPosition*16;

		byte[] biomes = ch.getBiomeArray();
		int index = az*16+ax;
		if (index < 0 || index >= biomes.length) {
			DragonAPICore.logError("BIOME CHANGE ERROR: "+x+"&"+z+" @ "+ch.xPosition+"&"+ch.zPosition+": "+ax+"%"+az+" -> "+index, Side.SERVER);
			return;
		}
		biomes[index] = (byte)biome.biomeID;
		ch.setBiomeArray(biomes);
		ch.setChunkModified();
		for (int i = 0; i < 256; i++)
			temperatureEnvironment(world, x, i, z, ReikaBiomeHelper.getBiomeTemp(world, biome));

		if (!world.isRemote) {
			int packet = APIPacketHandler.PacketIDs.BIOMECHANGE.ordinal();
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, packet, world, x, 0, z, biome.biomeID);
		}
	}

	public static BiomeGenBase getNaturalGennedBiomeAt(World world, int x, int z) {
		BiomeGenBase[] biomes = world.getWorldChunkManager().loadBlockGeneratorData(null, x, z, 1, 1);
		BiomeGenBase natural = biomes != null && biomes.length > 0 ? biomes[0] : null;
		return natural;
	}

	/** Sets the biome type at an xz column and mimics its generation. Args: World, x, z, biome */
	public static void setBiomeAndBlocksForXZ(World world, int x, int z, BiomeGenBase biome) {
		Chunk ch = world.getChunkFromBlockCoords(x, z);

		int ax = x-ch.xPosition*16;
		int az = z-ch.zPosition*16;

		byte[] biomes = ch.getBiomeArray();
		int index = az*16+ax;
		if (index < 0 || index >= biomes.length) {
			DragonAPICore.logError("BIOME CHANGE ERROR: "+x+"&"+z+" @ "+ch.xPosition+"&"+ch.zPosition+": "+ax+"%"+az+" -> "+index, Side.SERVER);
			return;
		}

		BiomeGenBase from = BiomeGenBase.biomeList[biomes[index]];

		biomes[index] = (byte)biome.biomeID;
		ch.setBiomeArray(biomes);
		for (int i = 0; i < 256; i++)
			temperatureEnvironment(world, x, i, z, ReikaBiomeHelper.getBiomeTemp(world, biome));

		if (!world.isRemote) {
			int packet = APIPacketHandler.PacketIDs.BIOMECHANGE.ordinal();
			ReikaPacketHelper.sendDataPacket(DragonAPIInit.packetChannel, packet, world, x, 0, z, biome.biomeID);
		}

		Block fillerID = from.fillerBlock;
		Block topID = from.topBlock;

		for (int y = 30; y < world.provider.getHeight(); y++) {
			Block id = world.getBlock(x, y, z);
			if (id == fillerID) {
				world.setBlock(x, y, z, biome.fillerBlock);
			}
			if (id == topID && y == world.getTopSolidOrLiquidBlock(x, z)-1) {
				world.setBlock(x, y, z, biome.topBlock);
			}

			if (biome.getEnableSnow()) {
				if (world.canBlockFreeze(x, y, z, false))
					world.setBlock(x, y, z, Blocks.ice);
				else if (world.canBlockSeeTheSky(x, y+1, z) && world.isAirBlock(x, y+1, z))
					world.setBlock(x, y+1, z, Blocks.snow);
			}
			else {
				if (id == Blocks.snow)
					world.setBlockToAir(x, y, z);
				if (id == Blocks.ice)
					world.setBlock(x, y, z, Blocks.flowing_water);
			}
		}

		if (world.isRemote)
			return;

		BiomeDecorator dec = biome.theBiomeDecorator;

		int trees = dec.treesPerChunk;
		int grass = dec.grassPerChunk;
		int flowers = dec.flowersPerChunk;
		int cactus = dec.cactiPerChunk;
		int bushes = dec.deadBushPerChunk;
		int sugar = dec.reedsPerChunk;
		int mushrooms = dec.mushroomsPerChunk;
		int lily = dec.waterlilyPerChunk;
		int bigmush = dec.bigMushroomsPerChunk;

		double fac = 1/3D;
		int top = world.getTopSolidOrLiquidBlock(x, z);

		if (ReikaRandomHelper.doWithChance(fac*trees/96D)) {
			WorldGenerator gen = biome.func_150567_a(rand);
			if (ReikaPlantHelper.SAPLING.canPlantAt(world, x, top, z)) {
				if (softBlocks(world, x, top, z))
					world.setBlockToAir(x, top, z);
				gen.generate(world, rand, x, top, z);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*grass/64D)) {
			WorldGenerator gen = biome.getRandomWorldGenForGrass(rand);
			if (softBlocks(world, x, top, z))
				world.setBlockToAir(x, top, z);
			gen.generate(world, rand, x, top, z);
		}

		if (ReikaRandomHelper.doWithChance(fac*bigmush/96D)) {
			if (softBlocks(world, x, top, z))
				world.setBlockToAir(x, top, z);
			biome.theBiomeDecorator.bigMushroomGen.generate(world, rand, x, top, z);
		}

		if (ReikaRandomHelper.doWithChance(fac*cactus/256D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.CACTUS.canPlantAt(world, x, y, z)) {
				int h = 1+rand.nextInt(3);
				for (int i = 0; i < h; i++)
					world.setBlock(x, y+i, z, Blocks.cactus);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*sugar/64D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.SUGARCANE.canPlantAt(world, x, y, z)) {
				int h = 1+rand.nextInt(3);
				for (int i = 0; i < h; i++)
					world.setBlock(x, y+i, z, Blocks.reeds);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*bushes/64D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.BUSH.canPlantAt(world, x, y, z)) {
				world.setBlock(x, y, z, Blocks.deadbush);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*lily/64D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.LILYPAD.canPlantAt(world, x, y, z)) {
				world.setBlock(x, y, z, Blocks.waterlily);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*flowers/256D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.FLOWER.canPlantAt(world, x, y, z)) {
				if (rand.nextInt(3) == 0)
					world.setBlock(x, y, z, Blocks.red_flower);
				else
					world.setBlock(x, y, z, Blocks.yellow_flower);
			}
		}

		if (ReikaRandomHelper.doWithChance(fac*128*mushrooms/256D)) {
			int y = world.getTopSolidOrLiquidBlock(x, z);
			if (ReikaPlantHelper.MUSHROOM.canPlantAt(world, x, y, z)) {
				if (rand.nextInt(4) == 0)
					world.setBlock(x, y, z, Blocks.red_mushroom);
				else
					world.setBlock(x, y, z, Blocks.brown_mushroom);
			}
		}

		biome.decorate(world, rand, x, z);

		for (int i = 40; i < 80; i++) {
			world.markBlockForUpdate(x, i, z);
			causeAdjacentUpdates(world, x, i, z);
		}
	}

	/** Get the sun brightness as a fraction from 0-1. Args: World */
	public static float getSunIntensity(World world) {
		float ang = world.getCelestialAngle(0);
		float base = 1.0F - (MathHelper.cos(ang*(float)Math.PI*2.0F)*2.0F+0.2F);

		if (base < 0.0F)
			base = 0.0F;

		if (base > 1.0F)
			base = 1.0F;

		base = 1.0F - base;
		base = (float)(base*(1.0D - world.getRainStrength(0)*5.0F / 16.0D));
		base = (float)(base*(1.0D - world.getWeightedThunderStrength(0)*5.0F / 16.0D));
		return base*0.8F+0.2F;
	}

	/** Returns the sun's declination, clamped to 0-90. Args: World */
	public static float getSunAngle(World world) {
		int time = (int)(world.getWorldTime()%12000);
		float suntheta = 0.5F*(float)(90*Math.sin(Math.toRadians(time*90D/6000D)));
		return suntheta;
	}

	/** Tests if a block is nearby, yes/no. Args: World, x, y, z, id to test, meta to test, range */
	public static boolean testBlockProximity(World world, int x, int y, int z, Block id, int meta, int r) {
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int rx = x+i;
					int ry = y+j;
					int rz = z+k;
					Block rid = world.getBlock(rx, ry, rz);
					int rmeta = world.getBlockMetadata(rx, ry, rz);
					if (rid == id && (meta == -1 || rmeta == meta))
						return true;
				}
			}
		}
		return false;
	}

	/** Tests if a liquid is nearby, yes/no. Args: World, x, y, z, liquid material, range */
	public static boolean testLiquidProximity(World world, int x, int y, int z, Material mat, int r) {
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int rx = x+i;
					int ry = y+j;
					int rz = z+k;
					Material rmat = getMaterial(world, rx, ry, rz);
					if (rmat == mat)
						return true;
				}
			}
		}
		return false;
	}

	/** A less intensive but less accurate block proximity test. Args: World, x, y, z, range */
	public static boolean testBlockProximityLoose(World world, int x, int y, int z, Block id, int meta, int r) {
		int total = r*r*r*8; //(2r)^3
		int frac = total/16;
		for (int i = 0; i < frac; i++) {
			int rx = ReikaRandomHelper.getRandomPlusMinus(x, r);
			int ry = ReikaRandomHelper.getRandomPlusMinus(y, r);
			int rz = ReikaRandomHelper.getRandomPlusMinus(z, r);
			Block rid = world.getBlock(rx, ry, rz);
			int rmeta = world.getBlockMetadata(rx, ry, rz);
			if (rid == id && (meta == -1 || rmeta == meta))
				return true;
		}
		return false;
	}

	public static EntityLivingBase getClosestLivingEntity(World world, double x, double y, double z, AxisAlignedBB box) {
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		double d = Double.MAX_VALUE;
		EntityLivingBase index = null;
		for (EntityLivingBase e : li) {
			if (!e.isDead && e.getHealth() > 0) {
				double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
				if (dd < d) {
					index = e;
					d = dd;
				}
			}
		}
		return index;
	}

	public static EntityLivingBase getClosestLivingEntityNoPlayers(World world, double x, double y, double z, AxisAlignedBB box, boolean excludeCreativeOnly) {
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		double d = Double.MAX_VALUE;
		EntityLivingBase index = null;
		for (EntityLivingBase e : li) {
			if (!(e instanceof EntityPlayer) || (excludeCreativeOnly && !((EntityPlayer)e).capabilities.isCreativeMode)) {
				if (!e.isDead && e.getHealth() > 0) {
					double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
					if (dd < d) {
						index = e;
						d = dd;
					}
				}
			}
		}
		return index;
	}

	public static EntityLivingBase getClosestHostileEntity(World world, double x, double y, double z, AxisAlignedBB box) {
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(EntityLivingBase.class, box);
		double d = Double.MAX_VALUE;
		EntityLivingBase index = null;
		for (EntityLivingBase e : li) {
			if (ReikaEntityHelper.isHostile(e)) {
				if (!e.isDead && e.getHealth() > 0) {
					double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
					if (dd < d) {
						index = e;
						d = dd;
					}
				}
			}
		}
		return index;
	}

	public static EntityLivingBase getClosestLivingEntityOfClass(Class<? extends EntityLivingBase> c, World world, double x, double y, double z, AxisAlignedBB box) {
		List<EntityLivingBase> li = world.getEntitiesWithinAABB(c, box);
		double d = Double.MAX_VALUE;
		EntityLivingBase index = null;
		for (EntityLivingBase e : li) {
			if (!e.isDead && e.getHealth() > 0) {
				double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
				if (dd < d) {
					index = e;
					d = dd;
				}
			}
		}
		return index;
	}

	public static Entity getClosestEntityOfClass(Class<? extends Entity> c, World world, double x, double y, double z, double range) {
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(range, range, range);
		List<Entity> li = world.getEntitiesWithinAABB(c, box);
		double d = Double.MAX_VALUE;
		Entity index = null;
		for (Entity e : li) {
			if (!e.isDead) {
				double dd = ReikaMathLibrary.py3d(e.posX-x, e.posY-y, e.posZ-z);
				if (dd < d) {
					index = e;
					d = dd;
				}
			}
		}
		return index;
	}

	public static EntityLivingBase getClosestLivingEntityOfClass(Class<? extends EntityLivingBase> c, World world, double x, double y, double z, double range) {
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x, y, z).expand(range, range, range);
		return getClosestLivingEntityOfClass(c, world, x, y, z, box);
	}

	public static boolean otherDimensionsExist() {
		return ModList.MYSTCRAFT.isLoaded() || ModList.TWILIGHT.isLoaded() || ModList.EXTRAUTILS.isLoaded();
	}

	public static int getAmbientTemperatureAt(World world, int x, int y, int z) {
		int Tamb = ReikaBiomeHelper.getBiomeTemp(world, x, z);
		float temp = Tamb;

		if (!world.provider.hasNoSky) {
			if (world.canBlockSeeTheSky(x, y+1, z)) {
				float sun = getSunIntensity(world);
				int mult = world.isRaining() ? 10 : 20;
				temp += (sun-0.75F)*mult;
			}
			int h = world.provider.getAverageGroundLevel();
			int dy = h-y;
			if (dy > 0) {
				if (dy < 20) {
					temp -= dy;
					temp = Math.max(temp, Tamb-20);
				}
				else if (dy < 25) {
					temp -= 2*(25-dy);
					temp = Math.max(temp, Tamb-20);
				}
				else {
					temp += 100*(dy-20)/h;
					temp = Math.min(temp, Tamb+70);
				}
			}
			if (y > 96) {
				temp -= (y-96)/4;
			}
		}

		return (int)temp;
	}

	/** Returns whether there is a TileEntity at the specified position. Does not call getTileEntity(). */
	public static boolean tileExistsAt(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == Blocks.air)
			return false;
		if (b == null)
			return false;
		int meta = world.getBlockMetadata(x, y, z);
		if (!b.hasTileEntity(meta))
			return false;
		return true;
	}

	public static int getFreeDistance(World world, int x, int y, int z, ForgeDirection dir, int maxdist) {
		for (int i = 1; i < maxdist; i++) {
			int dx = x+dir.offsetX*i;
			int dy = y+dir.offsetY*i;
			int dz = z+dir.offsetZ*i;
			if (!softBlocks(world, dx, dy, dz)) {
				return i-1;
			}
		}
		return maxdist;
	}

	public static boolean isExposedToAir(World world, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetZ;
			int dz = z+dir.offsetY;
			Block b = world.getBlock(dx, dy, dz);
			if (b == Blocks.air)
				return true;
			;
			if (b == null)
				return true;
			if (b.getCollisionBoundingBoxFromPool(world, dx, dy, dz) == null)
				return true;
			Material mat = b.getMaterial();
			if (mat != null) {
				if (mat == Material.circuits || mat == Material.air || mat == Material.cactus || mat == Material.fire)
					return true;
				if (mat == Material.plants || mat == Material.portal || mat == Material.vine || mat == Material.web)
					return true;
				if (!mat.isSolid())
					return true;
			}
		}
		return false;
	}

	public static int countAdjacentBlocks(World world, int x, int y, int z, Block id, boolean checkCorners) {
		int count = 0;
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetZ;
			int dz = z+dir.offsetY;
			Block id2 = world.getBlock(dx, dy, dz);
			if (id == id2)
				count++;
		}

		if (checkCorners) {
			for (int n = 0; n < RelativePositionList.cornerDirections.getSize(); n++) {
				Coordinate d = RelativePositionList.cornerDirections.getNthPosition(x, y, z, n);
				int dx = d.xCoord;
				int dy = d.yCoord;
				int dz = d.zCoord;
				Block id2 = world.getBlock(dx, dy, dz);
				if (id == id2) {
					count++;
				}
			}
		}

		return count;
	}

	public static void forceGenAndPopulate(World world, int x, int z) {
		forceGenAndPopulate(world, x, z, 0);
	}

	public static void forceGenAndPopulate(World world, int x, int z, int range) {
		for (int i = -range; i <= range; i++) {
			for (int k = -range; k <= range; k++) {

				int dx = x+i*16;
				int dz = z+k*16;

				Chunk ch = world.getChunkFromBlockCoords(dx, dz);
				IChunkProvider p = world.getChunkProvider();
				if (!ch.isTerrainPopulated) {
					try {
						p.populate(p, dx >> 4, dz >> 4);
					}
					catch (ConcurrentModificationException e) {
						DragonAPICore.logError("Chunk at "+dx+", "+dz+" failed to allow population due to a ConcurrentModificationException! Contact Reika with information on any mods that might be multithreading worldgen!");
						e.printStackTrace();
					}
					catch (Exception e) {
						DragonAPICore.logError("Chunk at "+dx+", "+dz+" failed to allow population!");
						e.printStackTrace();
					}
				}

			}
		}
	}

	public static Collection<IInventory> getAllInventories(World world, int x, int y, int z, int r) {
		Collection<IInventory> c = new ArrayList();
		for (int i = -r; i <= r; i++) {
			for (int j = -r; j <= r; j++) {
				for (int k = -r; k <= r; k++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (tileExistsAt(world, dx, dy, dz)) {
						TileEntity te = world.getTileEntity(dx, dy, dz);
						if (te instanceof IInventory)
							c.add((IInventory)te);
					}
				}
			}
		}
		return c;
	}

	public static void dropAndDestroyBlockAt(World world, int x, int y, int z, EntityPlayer ep, boolean breakAll) {
		Block b = world.getBlock(x, y, z);
		if (b.blockHardness < 0 && !breakAll)
			return;
		dropBlockAt(world, x, y, z, ep);
		world.setBlock(x, y, z, Blocks.air);
	}

	public static boolean matchWithItemStack(World world, int x, int y, int z, ItemStack is) {
		return ReikaItemHelper.matchStackWithBlock(is, world.getBlock(x, y, z)) && is.getItemDamage() == world.getBlockMetadata(x, y, z);
	}

	public static boolean isSubmerged(IBlockAccess iba, int x, int y, int z) {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = iba.getBlock(dx, dy, dz);
			if (b == Blocks.water || b == Blocks.flowing_water)
				continue;
			if (!b.isOpaqueCube() && !b.renderAsNormalBlock() && b.getRenderType() != 0)
				return false;
		}
		return true;
	}

	/** Returns true if the chunk here (block coords) has been generated, whether or not it is currently loaded. */
	public static boolean isChunkGenerated(WorldServer world, int x, int z) {
		return isChunkGeneratedChunkCoords(world, x >> 4, z >> 4);
	}

	/** Returns true if the chunk here has been generated, whether or not it is currently loaded. */
	public static boolean isChunkGeneratedChunkCoords(WorldServer world, int x, int z) {
		IChunkLoader loader = world.theChunkProviderServer.currentChunkLoader;
		return loader instanceof AnvilChunkLoader && ((AnvilChunkLoader)loader).chunkExists(world, x, z);
	}

	public static int getWaterDepth(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int c = 0;
		while (b == Blocks.water) {
			y--;
			c++;
			b = world.getBlock(x, y, z);
		}
		return c;
	}

	public static boolean isWorldLoaded(int dim) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			throw new MisuseException("This cannot be called clientside!");
		return DimensionManager.getWorld(dim) != null;
	}

	public static int getTopNonAirBlock(World world, int x, int z) {
		Chunk ch = world.getChunkFromBlockCoords(x, z);
		int top = ch.getTopFilledSegment()+15;
		for (int y = top; y > 0; y--) {
			Block b = ch.getBlock(x&15, y, z&15);
			if (!b.isAir(world, x, y, z)) {
				return y;
			}
		}
		return 0;
	}

	public static World getBasicReferenceWorld() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? getClientWorld() : DimensionManager.getWorld(0);
	}

	@SideOnly(Side.CLIENT)
	private static World getClientWorld() {
		return Minecraft.getMinecraft().theWorld;
	}

	public static ArrayList<BlockKey> getBlocksAlongVector(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
		HashSet<Coordinate> set = new HashSet();
		ArrayList<BlockKey> li = new ArrayList();
		double dd = ReikaMathLibrary.py3d(x2-x1, y2-y1, z2-z1);
		for (double d = 0; d <= dd; d += 0.25) {
			double f = d/dd;
			double dx = x1+f*(x2-x1);
			double dy = y1+f*(y2-y1);
			double dz = z1+f*(z2-z1);
			Coordinate c = new Coordinate(dx, dy, dz);
			if (!set.contains(c)) {
				set.add(c);
				li.add(new BlockKey(c.getBlock(world), c.getBlockMetadata(world)));
			}
		}
		return li;
	}

	public static FluidStack getDrainableFluid(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (b instanceof IFluidBlock) {
			return ((IFluidBlock)b).drain(world, x, y, z, false);
		}
		else if (b instanceof BlockLiquid) {
			if (meta != 0)
				return null;
			Fluid f = FluidRegistry.lookupFluidForBlock(b);
			return f != null ? new FluidStack(f, FluidContainerRegistry.BUCKET_VOLUME) : null;
		}
		else {
			return null;
		}
	}

	/** A surrogate for the method in world, more performant and fires an event. */
	public static EntityPlayer getClosestVulnerablePlayer(World world, double x, double y, double z, double r) {
		double dd = Double.POSITIVE_INFINITY;
		double dist = -1;
		EntityPlayer ret = null;

		for (EntityPlayer ep : ((List<EntityPlayer>)world.playerEntities)) {
			boolean flag = false;
			Result res = MobTargetingEvent.firePre(ep, world, x, y, z, r);
			if (res != Result.DENY && (!ep.capabilities.disableDamage || res == Result.ALLOW) && ep.isEntityAlive()) {
				dist = ep.getDistanceSq(x, y, z);

				if (ep.isSneaking()) {
					r *= 0.8;
				}

				if (ep.isInvisible()) {
					float f = ep.getArmorVisibility();

					if (f < 0.1F) {
						f = 0.1F;
					}

					r *= 0.7F*f;
				}

				flag = (r < 0 || dist < r*r) && dist < dd;
				flag = MobTargetingEvent.fire(ep, world, x, y, z, r, flag);
			}

			if (flag) {
				dd = dist;
				ret = ep;
			}
		}

		EntityPlayer post = MobTargetingEvent.firePost(world, x, y, z, r);
		if (post != null)
			ret = post;

		return ret;
	}

	public static void erodeBlock(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		Material mat = b.getMaterial();
		if (ReikaBlockHelper.isLiquid(b) || mat == Material.plants || mat == Material.leaves) {
			world.setBlock(x, y, z, Blocks.air);
		}
		else if (b == Blocks.cobblestone) {
			world.setBlock(x, y, z, Blocks.gravel);
		}
		else if (b == Blocks.gravel) {
			world.setBlock(x, y, z, Blocks.sand);
		}
		else if (b == Blocks.sand) {
			world.setBlock(x, y, z, Blocks.air);
		}
		else if (mat == Material.rock) {
			world.setBlock(x, y, z, Blocks.cobblestone);
		}
		else if (b instanceof BlockLog) {
			world.setBlock(x, y, z, Blocks.fire);
		}
		else if (mat == Material.ground) {
			world.setBlock(x, y, z, Blocks.sand);
		}
		else if (mat == Material.grass || b == Blocks.grass) {
			world.setBlock(x, y, z, Blocks.dirt);
		}
	}

	public static void hydrateFarmland(World world, int x, int y, int z, boolean fullHydrate) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta < 7) {
			world.setBlockMetadataWithNotify(x, y, z, fullHydrate ? 7 : meta+1, 3);
		}
	}
}
