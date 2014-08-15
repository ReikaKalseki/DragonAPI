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

import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class ReikaThaumHelper {
	/** Contains a helper function to avoid overwriting existing aspects. */
	public static void addAspects(ItemStack is, Object... aspects) {
		if (aspects.length%2 != 0) {
			ReikaJavaLibrary.pConsole("Could not add aspects to "+is+": You must specify a level for every aspect!");
			Thread.dumpStack();
			return;
		}
		AspectList has = ThaumcraftApi.objectTags.get(Arrays.asList(is.getItem(), is.getItemDamage()));
		AspectList ot = getAspectList(aspects);

		if (has != null) {
			for (Aspect as : has.aspects.keySet()) {
				ot.merge(as, has.getAmount(as));
			}
		}
		ThaumcraftApi.registerObjectTag(is.getItem(), is.getItemDamage(), ot);
	}

	public static void addAspects(Block b, Object... aspects) {
		addAspects(new ItemStack(b), aspects);
	}

	public static void addAspects(Item i, Object... aspects) {
		addAspects(new ItemStack(i), aspects);
	}

	public static void addAspects(Item id, int meta, Object... aspects) {
		addAspects(new ItemStack(id, 1, meta), aspects);
	}

	public static void clearAspects(ItemStack is) {
		AspectList ot = new AspectList();
		ThaumcraftApi.registerObjectTag(is.getItem(), is.getItemDamage(), ot);
	}

	public static void addAspects(Class<? extends Entity> entity, Object... aspects) {
		AspectList ot = getAspectList(aspects);
		String name = (String)EntityList.classToStringMapping.get(entity);
		ThaumcraftApi.registerEntityTag(name, ot);
	}

	private static AspectList getAspectList(Object... aspects) {
		AspectList ot = new AspectList();
		try {
			for (int i = 0; i < aspects.length; i += 2) {
				ot.add((Aspect)aspects[i], (Integer)aspects[i+1]);
			}
		}
		catch (ClassCastException e) {
			ReikaJavaLibrary.pConsole("Invalid parameters! Could not generate aspect list from "+Arrays.toString(aspects)+"!");
			e.printStackTrace();
		}
		return ot;
	}

	public static String aspectsToString(AspectList al) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (Aspect a : al.aspects.keySet()) {
			int amt = al.getAmount(a);
			sb.append(a.getTag()+"="+amt);
			sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}

}