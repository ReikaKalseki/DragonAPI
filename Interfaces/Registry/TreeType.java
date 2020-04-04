/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Registry;

import java.util.List;

import net.minecraft.block.Block;

import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;

public interface TreeType {

	public BlockKey getItem();
	public Block getLogID();
	public Block getLeafID();
	public Block getSaplingID();
	public List<Integer> getLogMetadatas();
	public List<Integer> getLeafMetadatas();
	public int getSaplingMeta();
	public boolean canBePlacedSideways();
	public boolean exists();
	public BlockKey getBasicLeaf();
	public BlockBox getTypicalMaximumSize();

}
