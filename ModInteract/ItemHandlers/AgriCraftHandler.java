package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;


public class AgriCraftHandler extends CropHandlerBase {

	private static final AgriCraftHandler instance = new AgriCraftHandler();

	private static final Random rand = new Random();

	private static final int GROWN = 7;

	/** Static method */
	private Method isValidSeed;

	/** CropPlant instance method */
	private Method getFruits;

	/** TileEntity instance field */
	private Field plant;

	private final Block cropBlock;

	private boolean init;

	private AgriCraftHandler() {
		super();
		Block idcrop = null;
		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();

				Field crop = blocks.getField("blockCrop");
				idcrop = (Block)crop.get(null);

				Class handler = Class.forName("com.InfinityRaider.AgriCraft.farming.CropPlantHandler");
				isValidSeed = handler.getMethod("isValidSeed", ItemStack.class);

				Class plant = Class.forName("com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlant");
				getFruits = plant.getMethod("getFruitsOnHarvest", int.class, Random.class);

				Class tile = Class.forName("com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop");
				this.plant = tile.getDeclaredField("plant");
				this.plant.setAccessible(true);

				init = true;
			}
			catch (ClassNotFoundException e) {
				DragonAPICore.logError(this.getMod()+" class not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NoSuchFieldException e) {
				DragonAPICore.logError(this.getMod()+" field not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (SecurityException e) {
				DragonAPICore.logError("Cannot read "+this.getMod()+" (Security Exception)! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalArgumentException e) {
				DragonAPICore.logError("Illegal argument for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (IllegalAccessException e) {
				DragonAPICore.logError("Illegal access exception for reading "+this.getMod()+"!");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NullPointerException e) {
				DragonAPICore.logError("Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
				e.printStackTrace();
				this.logFailure(e);
			}
			catch (NoSuchMethodException e) {
				DragonAPICore.logError(this.getMod()+" method not found! "+e.getMessage());
				e.printStackTrace();
				this.logFailure(e);
			}
		}
		else {
			this.noMod();
		}
		cropBlock = idcrop;
	}

	public static AgriCraftHandler getInstance() {
		return instance;
	}

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return 2;
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return id == cropBlock;
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		return this.getGrowthState(world, x, y, z) == GROWN;
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		world.setBlockMetadataWithNotify(x, y, z, GROWN, 3);
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		try {
			return (Boolean)isValidSeed.invoke(null, is);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		TileEntity te = world.getTileEntity(x, y, z);
		try {
			Object cp = plant.get(te);
			return (ArrayList<ItemStack>)getFruits.invoke(cp, 1+fortune*3/2, rand);
		}
		catch (Exception e) {
			return null;
		}
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return null;
	}

	@Override
	public boolean neverDropsSecondSeed() {
		return true;
	}

	@Override
	public boolean initializedProperly() {
		return cropBlock != null && init;
	}

	@Override
	public ModList getMod() {
		return ModList.AGRICRAFT;
	}

}
