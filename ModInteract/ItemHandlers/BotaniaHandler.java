/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.ItemHandlers;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.ModHandlerBase;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class BotaniaHandler extends ModHandlerBase {

	private static final BotaniaHandler instance = new BotaniaHandler();

	public final Block flowerID;
	public final Block livingRockID;
	public final Block livingWoodID;

	public final Item wandID;
	public final Item petalID;
	public final Item runeID;

	private BotaniaHandler() {
		super();
		Block idflower = null;
		Block idlivingrock = null;
		Block idlivingwood = null;

		Item idwand = null;
		Item idpetal = null;
		Item idrune = null;

		if (this.hasMod()) {
			try {
				Class blocks = this.getMod().getBlockClass();
				Field f = blocks.getField("flower");
				idflower = ((Block)f.get(null));

				f = blocks.getField("livingrock");
				idlivingrock = ((Block)f.get(null));

				f = blocks.getField("livingwood");
				idlivingwood = ((Block)f.get(null));


				Class items = this.getMod().getItemClass();
				f = items.getField("twigWand");
				idwand = ((Item)f.get(null));

				f = items.getField("petal");
				idpetal = ((Item)f.get(null));

				f = items.getField("rune");
				idrune = ((Item)f.get(null));
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
		}
		else {
			this.noMod();
		}

		livingRockID = idlivingrock;
		livingWoodID = idlivingwood;
		flowerID = idflower;

		wandID = idwand;
		petalID = idpetal;
		runeID = idrune;
	}

	public static BotaniaHandler getInstance() {
		return instance;
	}

	@Override
	public boolean initializedProperly() {
		return livingRockID != null && livingWoodID != null && flowerID != null && wandID != null && petalID != null && runeID != null;
	}

	@Override
	public ModList getMod() {
		return ModList.BOTANIA;
	}

	public boolean isMysticalFlower(ItemStack block) {
		if (!this.initializedProperly())
			return false;
		return ReikaItemHelper.matchStackWithBlock(block, flowerID);
	}

	public ItemStack getPetal(ReikaDyeHelper dye) {
		return new ItemStack(petalID, 1, 15-dye.ordinal());
	}

	public int[] getWandColors(ItemStack wand) {
		int c1 = wand.stackTagCompound.getInteger("color1");
		int c2 = wand.stackTagCompound.getInteger("color2");
		return new int[] {c1, c2};
	}

}
