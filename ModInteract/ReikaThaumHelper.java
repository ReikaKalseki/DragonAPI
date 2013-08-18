/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.EnumTag;
import thaumcraft.api.ObjectTags;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aura.AuraNode;
import thaumcraft.api.aura.EnumNodeType;
import Reika.DragonAPI.Libraries.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.ReikaMathLibrary;

public class ReikaThaumHelper {

	public static void addAspects(ItemStack is, Object... aspects) {
		if (aspects.length%2 != 0) {
			ReikaJavaLibrary.pConsole("Could not add aspects to "+is+": You must specify a level for every aspect!");
			Thread.dumpStack();
			return;
		}
		ObjectTags ot = new ObjectTags();
		try {
			for (int i = 0; i < aspects.length; i += 2) {
				ot.add((EnumTag)aspects[i], (Integer)aspects[i+1]);
			}
		}
		catch (ClassCastException e) {
			ReikaJavaLibrary.pConsole("Invalid parameters! Could not add aspects to "+is+"!");
			e.printStackTrace();
		}
		ThaumcraftApi.registerObjectTag(is.itemID, is.getItemDamage(), ot);
	}

	public static void clearAspects(ItemStack is) {
		ObjectTags ot = new ObjectTags();
		ThaumcraftApi.registerObjectTag(is.itemID, is.getItemDamage(), ot);
	}

	public static List<Integer> getAllNodesNear(World world, double x, double y, double z, double range) {
		List li = new ArrayList<Integer>();
		double step = 2;
		for (double i = x-range; i <= x+range; i += step) {
			for (double j = y-range; j <= y+range; j += step) {
				for (double k = z-range; k <= z+range; k += step) {
					if (ReikaMathLibrary.py3d(x-i, y-j, z-k) <= range) {
						int id = ThaumcraftApi.getClosestAuraWithinRange(world, x, y, z, step*2);
						li.add(id);
					}
				}
			}
		}
		return li;
	}

	public static List<AuraNode> getAllNodeCopiesNear(World world, double x, double y, double z, double range) {
		List li = new ArrayList<Integer>();
		double step = 2;
		for (double i = x-range; i <= x+range; i += step) {
			for (double j = y-range; j <= y+range; j += step) {
				for (double k = z-range; k <= z+range; k += step) {
					if (ReikaMathLibrary.py3d(x-i, y-j, z-k) <= range) {
						int id = ThaumcraftApi.getClosestAuraWithinRange(world, x, y, z, step*2);
						li.add(ThaumcraftApi.getNodeCopy(id));
					}
				}
			}
		}
		return li;
	}

	public static List<AuraNode> getAllNodeCopiesOfTypeNear(World world, double x, double y, double z, double range, EnumNodeType type) {
		List li = new ArrayList<Integer>();
		double step = 2;
		for (double i = x-range; i <= x+range; i += step) {
			for (double j = y-range; j <= y+range; j += step) {
				for (double k = z-range; k <= z+range; k += step) {
					int id = ThaumcraftApi.getClosestAuraWithinRange(world, x, y, z, step*2);
					AuraNode node = ThaumcraftApi.getNodeCopy(id);
					if (node.type == type)
						li.add(node);
				}
			}
		}
		return li;
	}

	public static List<Integer> getAllNodesOfTypeNear(World world, double x, double y, double z, double range, EnumNodeType type) {
		List li = new ArrayList<Integer>();
		double step = 2;
		for (double i = x-range; i <= x+range; i += step) {
			for (double j = y-range; j <= y+range; j += step) {
				for (double k = z-range; k <= z+range; k += step) {
					int id = ThaumcraftApi.getClosestAuraWithinRange(world, x, y, z, step*2);
					if (ThaumcraftApi.getNodeCopy(id).type == type)
						li.add(id);
				}
			}
		}
		return li;
	}

	public static AuraNode getCopyOfNearestNode(World world, double x, double y, double z, double range) {
		int id = ThaumcraftApi.getClosestAuraWithinRange(world, x, y, z, range);
		if (id == -1)
			return null;
		return ThaumcraftApi.getNodeCopy(id);
	}

	public static void affectNodeNearest(World world, int aura_change, int size_change, boolean toggleLock, ObjectTags flux, float dx, float dy, float dz, double x, double y, double z, double range) {
		int id = ThaumcraftApi.getClosestAuraWithinRange(world, x, y, z, range);
		ThaumcraftApi.queueNodeChanges(id, aura_change, size_change, toggleLock, flux, dx, dy, dz);
	}

	public static void affectAllNodesNear(World world, int aura_change, int size_change, boolean toggleLock, ObjectTags flux, float dx, float dy, float dz, double x, double y, double z, double range) {
		List<Integer> li = getAllNodesNear(world, x, y, z, range);
		for (int i = 0; i < li.size(); i++) {
			ThaumcraftApi.queueNodeChanges(li.get(i), aura_change, size_change, toggleLock, flux, dx, dy, dz);
		}
	}

	public static void affectAllNodesOfTypeNear(World world, EnumNodeType type, int aura_change, int size_change, boolean toggleLock, ObjectTags flux, float dx, float dy, float dz, double x, double y, double z, double range) {
		List<Integer> li = getAllNodesNear(world, x, y, z, range);
		for (int i = 0; i < li.size(); i++) {
			int id = li.get(i);
			if (ThaumcraftApi.getNodeCopy(id).type == type)
				ThaumcraftApi.queueNodeChanges(id, aura_change, size_change, toggleLock, flux, dx, dy, dz);
		}
	}

	public static void affectNode(AuraNode copy, int aura_change, int size_change, boolean toggleLock, ObjectTags flux, float dx, float dy, float dz) {
		ThaumcraftApi.queueNodeChanges(copy.key, aura_change, size_change, toggleLock, flux, dx, dy, dz);
	}

	/** I will stop doing it directly when the API allows me to change node types with the queue */
	public static void setNodeType(AuraNode copy, EnumNodeType type) {
		AuraNode node = null;
		try {
			Class thaum = Class.forName("thaumcraft.common.aura.AuraManager");
			Method get = thaum.getMethod("getNode", int.class);
			node = (AuraNode)get.invoke(copy.key);
		}
		catch (Exception e) {
			ReikaJavaLibrary.pConsole("Could not invoke methods to get the real node!");
			e.printStackTrace();
		}
		if (node != null) {
			node.type = type;
		}
	}

}
