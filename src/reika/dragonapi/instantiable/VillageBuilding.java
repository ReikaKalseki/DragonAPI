/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import reika.dragonapi.libraries.ReikaAABBHelper;
import reika.dragonapi.libraries.world.ReikaBlockHelper;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;


public class VillageBuilding implements IVillageCreationHandler {

	public final Class buildingClass;
	public final int weight;
	private final Constructor<VillagePiece> constructor;

	public final int xSize;
	public final int ySize;
	public final int zSize;

	public VillageBuilding(Class<? extends VillagePiece> c, int w, String n, int x, int y, int z) {
		buildingClass = c;
		weight = w;
		try {
			constructor = (Constructor<VillagePiece>)c.getConstructor(Start.class, int.class, Random.class, StructureBoundingBox.class, int.class, int.class, int.class, int.class);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException("You must specify an appropriate constructor!", e);
		}
		MapGenStructureIO.func_143031_a(buildingClass, n);
		xSize = x;
		ySize = y;
		zSize = z;
	}

	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i) {
		return new StructureVillagePieces.PieceWeight(buildingClass, weight, MathHelper.getRandomIntegerInRange(random, 0 + i, 1 + i));
	}

	@Override
	public Class<?> getComponentClass() {
		return buildingClass;
	}

	@Override
	public Object buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
	{
		return this.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
	}

	private VillagePiece buildComponent(StructureVillagePieces.Start startPiece, List pieces, Random random, int par3, int par4, int par5, int par6, int par7) {
		StructureBoundingBox var8 = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, xSize, ySize, zSize, par6);
		return (!this.canVillageGoDeeper(var8)) || (StructureComponent.findIntersecting(pieces, var8) != null) ? null : this.createInstance(startPiece, par7, random, var8, par6);
	}

	private VillagePiece createInstance(Start start, int par7, Random random, StructureBoundingBox var8, int par6) {
		try {
			return constructor.newInstance(start, par7, random, var8, par6, xSize, ySize, zSize);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private boolean canVillageGoDeeper(StructureBoundingBox var8) {
		return var8 != null && var8.minY > 10;
	}

	public static abstract class VillagePiece extends Village {

		public final int xSize;
		public final int ySize;
		public final int zSize;

		private int averageGroundLevel = -1;

		private StructureBoundingBox structureBox;

		protected VillagePiece(StructureVillagePieces.Start start, int par2, Random rand, StructureBoundingBox bb, int par5, int x, int y, int z) {
			super(start, par2);
			coordBaseMode = par5;
			boundingBox = bb;

			xSize = x;
			ySize = y;
			zSize = z;
		}

		@Override
		public final boolean addComponentParts(World world, Random rand, StructureBoundingBox box) {
			structureBox = box;

			if (averageGroundLevel < 0)
			{
				averageGroundLevel = this.getAverageGroundLevel(world, box);

				if (averageGroundLevel < 0)
				{
					return true;
				}

				boundingBox.offset(0, averageGroundLevel - boundingBox.maxY + ySize - 1, 0);
			}

			return this.generate(world, rand);
		}

		@Override
		public final int hashCode() {
			return structureBox.func_151535_h().hashCode() ^ this.getClass().hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o.getClass() == this.getClass() && ((VillagePiece)o).structureBox.func_151535_h().equals(structureBox.func_151535_h());
		}

		protected final void clearDroppedItems(World world) {
			AxisAlignedBB box = ReikaAABBHelper.structureToAABB(boundingBox).expand(2, 2, 2);
			List<EntityItem> li = world.getEntitiesWithinAABB(EntityItem.class, box);
			for (EntityItem ei : li) {
				ei.setDead();
			}
		}

		protected abstract boolean generate(World world, Random rand);

		protected final void placeBlockAtCurrentPosition(World world, int i, int j, int k, Block b) {
			this.placeBlockAtCurrentPosition(world, i, j, k, b, 0);
		}

		protected final void placeBlockAtCurrentPosition(World world, int i, int j, int k, Block b, int meta) {
			//this.placeBlockAtCurrentPosition(world, b, meta, i, j, k, structureBox);

			int i1 = this.getXWithOffset(i, k);
			int j1 = this.getYWithOffset(j);
			int k1 = this.getZWithOffset(i, k);

			if (b == Blocks.wall_sign)
				meta = ReikaBlockHelper.getSignMetadataToConnectToWall(world, i1, j1, k1, meta);

			//if (structureBox.isVecInside(i1, j1, k1))  {
			this.tryPlaceBlock(world, i1, j1, k1, b, meta, 3);
			//}
		}

		private void tryPlaceBlock(World world, int x, int y, int z, Block b, int meta, int flags) {
			Block b2 = world.getBlock(x, y, z);
			int meta2 = world.getBlockMetadata(x, y, z);
			if (b2 != b || meta2 != meta)
				world.setBlock(x, y, z, b, meta, flags);
		}

		protected final void placeBlockAtFixedPosition(World world, int i, int j, int k, Block b) {
			this.placeBlockAtFixedPosition(world, i, j, k, b, 0);
		}

		protected final void placeBlockAtFixedPosition(World world, int i, int j, int k, Block b, int meta) {
			this.tryPlaceBlock(world, i+boundingBox.minX, j+boundingBox.minY, k+boundingBox.minZ, b, meta, 3);
		}

		protected final TileEntity placeTileEntityAtFixedPosition(World world, int i, int j, int k, Block b, int meta) {
			this.tryPlaceBlock(world, i+boundingBox.minX, j+boundingBox.minY, k+boundingBox.minZ, b, meta, 3);
			return world.getTileEntity(i+boundingBox.minX, j+boundingBox.minY, k+boundingBox.minZ);
		}

		protected final TileEntity generateTileEntity(World world, int i, int j, int k, Block b, int meta) {
			this.placeBlockAtCurrentPosition(world, i, j, k, b, meta);
			int x = this.getXWithOffset(i, k);
			int y = this.getYWithOffset(j);
			int z = this.getZWithOffset(i, k);
			return world.getTileEntity(x, y, z);
		}

		protected void clearVolume(World world) {
			this.clearVolume(world, 0, 0, 0, xSize, ySize, zSize);
		}

		protected void clearVolume(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
			for (int i = x1; i < x2; i++) {
				for (int k = z1; k < z2; k++) {
					for (int j = y1; j < y2; j++) {
						this.placeBlockAtFixedPosition(world, i, j, k, Blocks.air);
					}
				}
			}
		}

	}

	public static class StructureEntry {

		public final Class structureClass;
		public final String identifier;
		public final int weight;

		public final int xSize;
		public final int ySize;
		public final int zSize;

		public StructureEntry(Class<? extends VillagePiece> c, int w, String s, int x, int y, int z) {
			structureClass = c;
			weight = w;
			identifier = s;
			xSize = x;
			ySize = y;
			zSize = z;
		}

		public VillageBuilding build() {
			return new VillageBuilding(structureClass, weight, identifier, xSize, ySize, zSize);
		}

	}

}
