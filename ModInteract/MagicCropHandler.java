package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Field;
import java.util.ArrayList;

import net.minecraft.block.Block;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CropHandlerBase;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class MagicCropHandler extends CropHandlerBase {

	private static final String[] crops = {
		"Coal", "Redstone", "Glowstone", "Obsidian", "Dye", "Iron", "Gold", "Lapis", "Ender", "Nether", "XP", "Blaze", "Diamond",
		"Emerald", "Copper", "Tin", "Silver", "Lead", "Quartz"
	};

	private static final MagicCropHandler instance = new MagicCropHandler();

	private final ArrayList<Integer> blockIDs = new ArrayList();

	private MagicCropHandler() {
		super();
		if (this.hasMod()) {
			for (int i = 0; i < crops.length; i++) {
				String field = "mCrop"+crops[i];
				Class c = this.getMod().getBlockClass();
				try {
					Field f = c.getField(field);
					Block crop = (Block)f.get(null);
					blockIDs.add(crop.blockID);
				}
				catch (NoSuchFieldException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: "+this.getMod()+" field not found! "+e.getMessage());
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Illegal access exception for reading "+this.getMod()+"!");
					e.printStackTrace();
				}
				catch (NullPointerException e) {
					ReikaJavaLibrary.pConsole("DRAGONAPI: Null pointer exception for reading "+this.getMod()+"! Was the class loaded?");
					e.printStackTrace();
				}
			}
		}
		else {
			this.noMod();
		}
	}

	@Override
	public boolean isCrop(int id) {
		return blockIDs.contains(id);
	}

	@Override
	public boolean isRipeCrop(int id, int meta) {
		return this.isCrop(id) && meta == this.getRipeMeta();
	}

	public static MagicCropHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return !blockIDs.isEmpty();
	}

	@Override
	public ModList getMod() {
		return ModList.MAGICCROPS;
	}

	@Override
	public int getRipeMeta() {
		return 7;
	}

	@Override
	public int getFreshMeta() {
		return 0;
	}

}
