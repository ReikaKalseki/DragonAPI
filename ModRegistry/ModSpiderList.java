/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import Reika.DragonAPI.Auxiliary.APIRegistry;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public enum ModSpiderList {

	HEATSCAR(APIRegistry.NATURA, "mods.natura.entity.FlameSpider"),
	KING(APIRegistry.TWILIGHT, "twilightforest.entity.EntityTFKingSpider"),
	HEDGE(APIRegistry.TWILIGHT, "twilightforest.entity.EntityTFHedgeSpider");

	private Class entityClass;
	private APIRegistry mod;
	private int entityHealth;
	private final EntityLiving instance;

	public static final ModSpiderList[] spiderList = ModSpiderList.values();

	private ModSpiderList(APIRegistry req, String className) {
		mod = req;
		EntityLiving ent = null;
		try {
			entityClass = Class.forName(className);
			ent = this.instantiate();
		}
		catch (ClassNotFoundException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: ERROR LOADING "+this);
			e.printStackTrace();
		}
		instance = ent;
	}

	public int getHealth() {
		return instance.getMaxHealth();
	}

	private EntityLiving instantiate() {
		Constructor c;
		try {
			c = entityClass.getConstructor(World.class);
			return (EntityLiving)c.newInstance((Object[])null);
		}
		catch (NoSuchMethodException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: ERROR LOADING "+this);
			e.printStackTrace();
		}
		catch (SecurityException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: ERROR LOADING "+this);
			e.printStackTrace();
		}
		catch (InstantiationException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: ERROR LOADING "+this);
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: ERROR LOADING "+this);
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: ERROR LOADING "+this);
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			ReikaJavaLibrary.pConsole("DRAGONAPI: ERROR LOADING "+this);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString() {
		return this.name()+" from "+mod;
	}

}
