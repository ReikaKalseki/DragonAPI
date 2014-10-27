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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class ReikaThaumHelper {

	public static void addAspects(ItemStack is, AspectList aspects) {
		AspectList has = ThaumcraftApi.objectTags.get(Arrays.asList(is.getItem(), is.getItemDamage()));

		if (has != null) {
			for (Aspect as : has.aspects.keySet()) {
				aspects.merge(as, has.getAmount(as));
			}
		}
		ThaumcraftApi.registerObjectTag(is, new int[]{is.getItemDamage()}, aspects);
	}

	public static void addAspectsToBlock(Block b, AspectList aspects) {
		addAspects(new ItemStack(b), aspects);
	}

	public static void addAspectsToBlockMeta(Block b, int meta, AspectList aspects) {
		addAspects(new ItemStack(b, 1, meta), aspects);
	}

	public static void addAspectsToItem(Item i, AspectList aspects) {
		addAspects(new ItemStack(i), aspects);
	}

	public static void addAspectsToItemMeta(Item id, int meta, AspectList aspects) {
		addAspects(new ItemStack(id, 1, meta), aspects);
	}

	/** Contains a helper function to avoid overwriting existing aspects. */
	public static void addAspects(ItemStack is, Object... aspects) {
		if (aspects.length%2 != 0) {
			ReikaJavaLibrary.pConsole("Could not add aspects to "+is+": You must specify a level for every aspect!");
			ReikaJavaLibrary.dumpStack();
			return;
		}
		AspectList has = ThaumcraftApi.objectTags.get(Arrays.asList(is.getItem(), is.getItemDamage()));
		AspectList ot = getAspectList(aspects);

		if (has != null) {
			for (Aspect as : has.aspects.keySet()) {
				ot.merge(as, has.getAmount(as));
			}
		}
		ReikaJavaLibrary.pConsole(Block.getBlockFromItem(is.getItem())+"_"+is+" > "+ot.aspects.toString()+" W "+has);
		ThaumcraftApi.registerObjectTag(is, ot);
	}

	public static void addAspectsToBlock(Block b, Object... aspects) {
		addAspects(new ItemStack(b), aspects);
	}

	public static void addAspectsToBlockMeta(Block b, int meta, Object... aspects) {
		addAspects(new ItemStack(b, 1, meta), aspects);
	}

	public static void addAspectsToItem(Item i, Object... aspects) {
		addAspects(new ItemStack(i), aspects);
	}

	public static void addAspectsToItemMeta(Item id, int meta, Object... aspects) {
		addAspects(new ItemStack(id, 1, meta), aspects);
	}

	public static void clearAspects(ItemStack is) {
		AspectList ot = new AspectList();
		ThaumcraftApi.registerObjectTag(is, new int[]{is.getItemDamage()}, ot);
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
