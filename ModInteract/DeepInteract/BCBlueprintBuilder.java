package Reika.DragonAPI.ModInteract.DeepInteract;

import java.util.LinkedList;

import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import buildcraft.BuildCraftBuilders;
import buildcraft.api.blueprints.IBuilderContext;
import buildcraft.api.blueprints.SchematicBlock;
import buildcraft.api.blueprints.SchematicBlockBase;
import buildcraft.core.Box;
import buildcraft.core.blueprints.Blueprint;
import buildcraft.core.blueprints.BptContext;
import buildcraft.core.blueprints.LibraryId;
import buildcraft.core.blueprints.SchematicRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BCBlueprintBuilder {

	public static Blueprint convertBA(World world, BlockArray ba) {
		Blueprint ret = new Blueprint(ba.getSizeX(), ba.getSizeY(), ba.getSizeZ());
		BptContext bpt = ret.getContext(world, new Box(ba.getMinX(), ba.getMinY(), ba.getMinZ(), ba.getMaxX(), ba.getMaxY(), ba.getMaxZ()));
		for (Coordinate c : ba.keySet()) {
			Block b = c.getBlock(world);
			int meta = c.getBlockMetadata(world);
			SchematicBlockBase s = SchematicRegistry.INSTANCE.createSchematicBlock(b, meta);
			s.idsToWorld(bpt.getMappingRegistry());
			//s.initializeFromObjectAt(bpt, c.xCoord, c.yCoord, c.zCoord);
			s.storeRequirements(bpt, c.xCoord, c.yCoord, c.zCoord);
			if (s instanceof SchematicBlock) {
				SchematicBlock sb = (SchematicBlock)s;
				sb.meta = meta;
				sb.storedRequirements = new ItemStack[]{new ItemStack(b, 1, meta)};
			}
			ret.put(c.xCoord-ba.getMinX(), c.yCoord-ba.getMinY(), c.zCoord-ba.getMinZ(), s);
		}
		return ret;
	}

	public static Blueprint convertBA(FilledBlockArray ba) {
		Blueprint ret = new Blueprint(ba.getSizeX(), ba.getSizeY(), ba.getSizeZ());
		BptContext bpt = ret.getContext(ba.world, new Box(ba.getMinX(), ba.getMinY(), ba.getMinZ(), ba.getMaxX(), ba.getMaxY(), ba.getMaxZ()));
		for (Coordinate c : ba.keySet()) {
			BlockKey bk = ba.getBlockKeyAt(c.xCoord, c.yCoord, c.zCoord);
			SchematicBlockBase s = SchematicRegistry.INSTANCE.createSchematicBlock(bk.blockID, bk.hasMetadata() ? bk.metadata : 0);
			s.idsToWorld(bpt.getMappingRegistry());
			//s.initializeFromObjectAt(bpt, c.xCoord, c.yCoord, c.zCoord);
			s.storeRequirements(bpt, c.xCoord, c.yCoord, c.zCoord);
			if (s instanceof SchematicBlock) {
				SchematicBlock sb = (SchematicBlock)s;
				sb.meta = bk.hasMetadata() ? bk.metadata : 0;
				sb.storedRequirements = new ItemStack[]{new ItemStack(bk.blockID, 1, sb.meta)};
			}
			ret.put(c.xCoord-ba.getMinX(), c.yCoord-ba.getMinY(), c.zCoord-ba.getMinZ(), s);
		}
		return ret;
	}

	private static LibraryId getExistingBlueprint(String name) {
		for (LibraryId id : BuildCraftBuilders.serverDB.getBlueprintIds()) {
			if (id.name != null && id.name.endsWith(name)) {
				return id;
			}
		}
		return null;
	}

	/** Returns the blueprint item referencing the blueprint; will not create a new entry if that file already exists. */
	public static ItemStack writeBlueprintToDisk(Blueprint b, String name, String author) {
		LibraryId id = getExistingBlueprint(name);
		if (id == null) {
			id = new LibraryId();
			id.name = name;
			BuildCraftBuilders.serverDB.add(id, b.getNBT());
		}
		b.id = id;
		b.author = author;
		b.anchorX = 0;
		b.anchorY = 0;
		b.anchorZ = 0;
		return b.getStack();
	}

	public static void registerBlockForDirectPlacement(Block b) {
		registerBlockForDirectPlacement(b, -1);
	}

	/** You must call this for any blocks that require silk-touch to be placed! */
	public static void registerBlockForDirectPlacement(Block b, int meta) {
		SchematicRegistry.INSTANCE.registerSchematicBlock(b, meta, SchematicDirect.class, new BlockKey(b, meta));
	}

	private static class SchematicDirect extends SchematicBlock {

		private final BlockKey key;

		public SchematicDirect(BlockKey bk) {
			key = bk;
			block = bk.blockID;
			meta = bk.hasMetadata() ? bk.metadata : 0;
		}

		@Override
		public void getRequirementsForPlacement(IBuilderContext context, LinkedList<ItemStack> requirements) {
			requirements.add(key.asItemStack());
		}

		@Override
		public void storeRequirements(IBuilderContext context, int x, int y, int z) {

		}
	}

}
