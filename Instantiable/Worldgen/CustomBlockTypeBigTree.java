/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Worldgen;

import net.minecraft.block.Block;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;

public final class CustomBlockTypeBigTree extends ModifiableBigTree {

	private final BlockKey wood;
	private final BlockKey leaves;

	public CustomBlockTypeBigTree(boolean updates, Block log, Block leaf) {
		this(updates, new BlockKey(log), new BlockKey(leaf));
	}

	public CustomBlockTypeBigTree(boolean updates, BlockKey log, BlockKey leaf) {
		super(updates);
		wood = log;
		leaves = leaf;
	}

	@Override
	public Block getLogBlock(int x, int y, int z) {
		return wood.blockID;
	}

	@Override
	public int getLogMetadata(int x, int y, int z) {
		return wood.metadata;
	}

	@Override
	public Block getLeafBlock(int x, int y, int z) {
		return leaves.blockID;
	}

	@Override
	public int getLeafMetadata(int x, int y, int z) {
		return leaves.metadata;
	}
}
