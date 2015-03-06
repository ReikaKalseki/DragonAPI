package Reika.DragonAPI.Interfaces;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CropHandler {

	public abstract int getHarvestedMeta(World world, int x, int y, int z);

	public abstract boolean isCrop(Block id);

	public abstract boolean isRipeCrop(World world, int x, int y, int z);

	public abstract void makeRipe(World world, int x, int y, int z);

	public abstract boolean isSeedItem(ItemStack is);

	//public abstract float getSecondSeedDropRate();

	public abstract ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune);

	public void editTileDataForHarvest(World world, int x, int y, int z);

	public abstract boolean initializedProperly();

}
