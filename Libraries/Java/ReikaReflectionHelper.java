/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.IDConflictException;
import Reika.DragonAPI.Exception.MisuseException;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.IO.ModLogger;
import Reika.DragonAPI.Interfaces.RegistrationList;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public final class ReikaReflectionHelper extends DragonAPICore {

	public static Block createBlockInstance(DragonAPIMod mod, RegistrationList list) {
		try {
			Constructor c = list.getObjectClass().getConstructor(list.getConstructorParamTypes());
			Block instance = (Block)(c.newInstance(list.getConstructorParams()));
			return (instance.setBlockName(list.getUnlocalizedName()));
		}
		catch (NoSuchMethodException e) {
			throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" does not have the specified constructor "+Arrays.toString(list.getConstructorParamTypes())+"! Check visibility and material args!");
		}
		catch (SecurityException e) {
			throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" threw security exception!");
		}
		catch (InstantiationException e) {
			throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" did not allow instantiation!");
		}
		catch (IllegalAccessException e) {
			throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" threw illegal access exception! (Nonpublic constructor)");
		}
		catch (IllegalArgumentException e) {
			throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" was given invalid parameters!");
		}
		catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if (t instanceof IllegalArgumentException)
				throw new IDConflictException(mod, t.getMessage());
			else {
				mod.getModLogger().logError("ITE on instantiating "+list);
				e.getCause().printStackTrace();
				throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" threw invocation target exception: "+e+" with "+e.getCause()+" ("+e.getCause().getMessage()+")");
			}
			//return null;
		}
	}

	public static Item createItemInstance(DragonAPIMod mod, RegistrationList list) {
		try {
			Constructor c = list.getObjectClass().getConstructor(list.getConstructorParamTypes());
			Item instance = (Item)(c.newInstance(list.getConstructorParams()));
			return (instance.setUnlocalizedName(list.getUnlocalizedName()));
		}
		catch (NoSuchMethodException e) {
			throw new RegistrationException(mod, "Item Class "+list.getObjectClass().getSimpleName()+" does not have the specified constructor "+Arrays.toString(list.getConstructorParamTypes())+"!");
		}
		catch (SecurityException e) {
			throw new RegistrationException(mod, "Item Class "+list.getObjectClass().getSimpleName()+" threw security exception!");
		}
		catch (InstantiationException e) {
			throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" did not allow instantiation!");
		}
		catch (IllegalAccessException e) {
			throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" threw illegal access exception! (Nonpublic constructor)");
		}
		catch (IllegalArgumentException e) {
			throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" was given invalid parameters!");
		}
		catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if (t instanceof IllegalArgumentException)
				throw new IDConflictException(mod, t.getMessage());
			else
				throw new RegistrationException(mod, list.getObjectClass().getSimpleName()+" threw invocation target exception: "+e+" with "+e.getCause()+" ("+e.getCause().getMessage()+")");
		}
	}

	public static Item createBasicItemInstance(DragonAPIMod mod, Class<? extends Item> cl, int id, String unloc, boolean overwrite) {
		Item instance;
		try {
			Constructor c = cl.getConstructor(int.class);
			instance = (Item)(c.newInstance(id));
			return (instance.setUnlocalizedName(unloc));
		}
		catch (NoSuchMethodException e) {
			throw new MisuseException("Item Class "+cl.getSimpleName()+" does not have the specified constructor!");
		}
		catch (SecurityException e) {
			throw new MisuseException("Item Class "+cl.getSimpleName()+" threw security exception!");
		}
		catch (InstantiationException e) {
			throw new MisuseException(cl.getSimpleName()+" did not allow instantiation!");
		}
		catch (IllegalAccessException e) {
			throw new MisuseException(cl.getSimpleName()+" threw illegal access exception! (Nonpublic constructor)");
		}
		catch (IllegalArgumentException e) {
			throw new MisuseException(cl.getSimpleName()+" was given invalid parameters!");
		}
		catch (InvocationTargetException e) {
			Throwable t = e.getCause();
			if (t instanceof IllegalArgumentException)
				throw new IllegalArgumentException(t.getMessage());
			else
				throw new MisuseException(cl.getSimpleName()+" threw invocation target exception: "+e+" with "+e.getCause()+" ("+e.getCause().getMessage()+")");
		}
	}

	/** Gets the value of a private int in an instance of obj. */
	public static int getPrivateInteger(Object obj, String field, ModLogger log) {
		try {
			Class c = obj.getClass();
			Field f = null;
			while (f == null && c != null) {
				try {
					f = c.getDeclaredField(field);
				}
				catch (NoSuchFieldException e2) {
					c = c.getSuperclass();
				}
			}
			if (f == null) {
				if (log.shouldDebug()) {
					ReikaJavaLibrary.pConsole("Could not find field "+field+" in "+obj);
					ReikaChatHelper.write("Could not find field "+field+" in "+obj);
				}
				throw new NoSuchFieldException();
			}
			int val = Integer.MIN_VALUE;
			if (!f.isAccessible()) {
				f.setAccessible(true);
				val = f.getInt(obj);
				f.setAccessible(false);
			}
			else
				val = f.getInt(obj);
			return val;
		}
		catch (NoSuchFieldException e) {
			if (log.shouldDebug()) {
				ReikaJavaLibrary.pConsole("Could not find field "+field+" in "+obj);
				ReikaChatHelper.write("Could not find field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			if (log.shouldDebug()) {
				ReikaJavaLibrary.pConsole("Could not access field "+field+" in "+obj);
				ReikaChatHelper.write("Could not access field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		catch (SecurityException e) {
			if (log.shouldDebug()) {
				ReikaJavaLibrary.pConsole("Security Manager locked field "+field+" in "+obj);
				ReikaChatHelper.write("Security Manager locked field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		return Integer.MIN_VALUE;
	}

	/** Gets the value of a private boolean in an instance of obj. */
	public static boolean getPrivateBoolean(Object obj, String field, ModLogger log) {
		try {
			Class c = obj.getClass();
			Field f = null;
			while (f == null && c != null) {
				try {
					f = c.getDeclaredField(field);
				}
				catch (NoSuchFieldException e2) {
					c = c.getSuperclass();
				}
			}
			if (f == null) {
				if (log.shouldDebug()) {
					ReikaJavaLibrary.pConsole("Could not find field "+field+" in "+obj);
					ReikaChatHelper.write("Could not find field "+field+" in "+obj);
				}
				throw new NoSuchFieldException();
			}
			boolean val = false;
			if (!f.isAccessible()) {
				f.setAccessible(true);
				val = f.getBoolean(obj);
				f.setAccessible(false);
			}
			else
				val = f.getBoolean(obj);
			return val;
		}
		catch (NoSuchFieldException e) {
			if (log.shouldDebug()) {
				ReikaJavaLibrary.pConsole("Could not find field "+field+" in "+obj);
				ReikaChatHelper.write("Could not find field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			if (log.shouldDebug()) {
				ReikaJavaLibrary.pConsole("Could not access field "+field+" in "+obj);
				ReikaChatHelper.write("Could not access field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		catch (SecurityException e) {
			if (log.shouldDebug()) {
				ReikaJavaLibrary.pConsole("Security Manager locked field "+field+" in "+obj);
				ReikaChatHelper.write("Security Manager locked field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		return false;
	}

	/** Gets a nonvisible field that may be inherited by any of the superclasses. Returns null if none exists. */
	public static Field getProtectedInheritedField(Object obj, String field) {
		Class c = obj.getClass();
		Field f = null;
		while (f == null && c != null) {
			try {
				f = c.getDeclaredField(field);
			}
			catch (NoSuchFieldException e2) {
				c = c.getSuperclass();
			}
		}
		return f;
	}

	public static void setFinalField(Field f, Object instance, Object o) throws Exception {
		f.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
		f.set(instance, o);
	}

}