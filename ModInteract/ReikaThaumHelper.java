/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ReikaThaumHelper {
	/** Contains a helper function to avoid overwriting existing aspects. */
	public static void addAspects(ItemStack is, Object... aspects) {
		if (aspects.length%2 != 0) {
			ReikaJavaLibrary.pConsole("Could not add aspects to "+is+": You must specify a level for every aspect!");
			Thread.dumpStack();
			return;
		}
		AspectList has = ThaumcraftApi.objectTags.get(Arrays.asList(is.itemID, is.getItemDamage()));
		AspectList ot = new AspectList();
		try {
			for (int i = 0; i < aspects.length; i += 2) {
				ot.add((Aspect)aspects[i], (Integer)aspects[i+1]);
			}
		}
		catch (ClassCastException e) {
			ReikaJavaLibrary.pConsole("Invalid parameters! Could not add aspects to "+is+"!");
			e.printStackTrace();
		}

		if (has != null) {
			for (Aspect as : has.aspects.keySet()) {
				ot.merge(as, has.getAmount(as));
			}
		}
		ThaumcraftApi.registerObjectTag(is.itemID, is.getItemDamage(), ot);
	}

	public static void addAspects(Block b, Object... aspects) {
		addAspects(new ItemStack(b));
	}

	public static void addAspects(Item i, Object... aspects) {
		addAspects(new ItemStack(i));
	}

	public static void clearAspects(ItemStack is) {
		AspectList ot = new AspectList();
		ThaumcraftApi.registerObjectTag(is.itemID, is.getItemDamage(), ot);
	}

}
