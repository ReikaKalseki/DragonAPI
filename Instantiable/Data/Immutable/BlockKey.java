/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data.Immutable;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Interfaces.BlockCheck;

public final class BlockKey implements BlockCheck {

	public final Block blockID;
	public final int metadata;

	public BlockKey(Block b) {
		this(b, -1);
	}

	public BlockKey(Block b, int meta) {
		metadata = meta;
		blockID = b;
		if (b == null)
			throw new MisuseException("Cannot create a BlockKey from a null block!");
	}

	public BlockKey(ItemStack is) {
		this(Block.getBlockFromItem(is.getItem()), is.getItemDamage());
		if (is.getItem() == null)
			throw new MisuseException("Cannot create a BlockKey from a null item!");
		Block b = Block.getBlockFromItem(is.getItem());
		if (b == null)
			throw new MisuseException("Cannot create a BlockKey with an item with no block!");
	}

	public static BlockKey getAt(IBlockAccess world, int x, int y, int z) {
		return new BlockKey(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public static BlockKey getAtNoMeta(IBlockAccess world, int x, int y, int z) {
		return new BlockKey(world.getBlock(x, y, z), -1);
	}

	@Override
	public int hashCode() {
		return blockID.hashCode()/* + metadata << 24*/;
	}

	@Override
	public boolean equals(Object o) {
		//ReikaJavaLibrary.pConsole(this+" & "+o);
		if (o instanceof BlockKey) {
			BlockKey b = (BlockKey)o;
			return b.blockID == blockID && (!this.hasMetadata() || !b.hasMetadata() || b.metadata == metadata);
		}
		return false;
	}

	@Override
	public String toString() {
		return blockID.getUnlocalizedName()+":"+metadata;
	}

	public boolean hasMetadata() {
		return metadata >= 0 && metadata != OreDictionary.WILDCARD_VALUE;
	}

	public ItemStack asItemStack() {
		return new ItemStack(blockID, 1, this.hasMetadata() ? metadata : 0);
	}

	public ItemStack getDisplay() {
		return this.asItemStack();
	}

	public boolean match(Block b, int meta) {
		return b == blockID && (!this.hasMetadata() || meta == metadata);
	}

	public boolean matchInWorld(World world, int x, int y, int z) {
		return this.match(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z));
	}

	@Override
	public void place(World world, int x, int y, int z) {
		world.setBlock(x, y, z, blockID, this.hasMetadata() ? metadata : 0, 3);
	}

	@Override
	public BlockKey asBlockKey() {
		return this;
	}
}
