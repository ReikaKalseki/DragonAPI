/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.base.DragonAPIMod;
import reika.dragonapi.exception.IDConflictException;
import reika.dragonapi.exception.MisuseException;
import reika.dragonapi.exception.RegistrationException;
import reika.dragonapi.instantiable.data.maps.PluralMap;
import reika.dragonapi.instantiable.io.ModLogger;
import reika.dragonapi.interfaces.registry.RegistrationList;
import reika.dragonapi.libraries.io.ReikaChatHelper;

public final class ReikaReflectionHelper extends DragonAPICore {

	private static final PluralMap<Method> methodCache = new PluralMap(2);

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
				throw new RegistrationException(mod, list+" ("+list.getObjectClass().getSimpleName()+") threw invocation target exception: "+e+" with "+e.getCause()+" ("+e.getCause().getMessage()+")");
			}
			//return null;
		}
		catch (NoClassDefFoundError e) {
			e.printStackTrace();
			throw new RegistrationException(mod, "Failed to load "+list+" due to a missing class: "+e);
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
				throw new RegistrationException(mod, list+" ("+list.getObjectClass().getSimpleName()+") threw invocation target exception: "+e+" with "+e.getCause()+" ("+e.getCause().getMessage()+")");
		}
		catch (NoClassDefFoundError e) {
			e.printStackTrace();
			throw new RegistrationException(mod, "Failed to load "+list+" due to a missing class: "+e);
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
		catch (NoClassDefFoundError e) {
			e.printStackTrace();
			throw new RegistrationException(mod, "Failed to load "+cl+" due to a missing class: "+e);
		}
	}

	public static Enchantment createEnchantmentInstance(DragonAPIMod mod, Class<? extends Enchantment> cl, int id, String unloc, boolean overwrite) {
		Enchantment instance;
		try {
			Constructor c = cl.getConstructor(int.class);
			instance = (Enchantment)(c.newInstance(id));
			return (instance.setName(unloc));
		}
		catch (NoSuchMethodException e) {
			throw new MisuseException("Enchantment Class "+cl.getSimpleName()+" does not have the specified constructor!");
		}
		catch (SecurityException e) {
			throw new MisuseException("Enchantment Class "+cl.getSimpleName()+" threw security exception!");
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
		catch (NoClassDefFoundError e) {
			e.printStackTrace();
			throw new RegistrationException(mod, "Failed to load "+cl+" due to a missing class: "+e);
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
					DragonAPICore.logError("Could not find field "+field+" in "+obj);
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
				DragonAPICore.logError("Could not find field "+field+" in "+obj);
				ReikaChatHelper.write("Could not find field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			if (log.shouldDebug()) {
				DragonAPICore.logError("Could not access field "+field+" in "+obj);
				ReikaChatHelper.write("Could not access field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		catch (SecurityException e) {
			if (log.shouldDebug()) {
				DragonAPICore.logError("Security Manager locked field "+field+" in "+obj);
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
					DragonAPICore.logError("Could not find field "+field+" in "+obj);
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
				DragonAPICore.logError("Could not find field "+field+" in "+obj);
				ReikaChatHelper.write("Could not find field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			if (log.shouldDebug()) {
				DragonAPICore.logError("Could not access field "+field+" in "+obj);
				ReikaChatHelper.write("Could not access field "+field+" in "+obj);
			}
			e.printStackTrace();
		}
		catch (SecurityException e) {
			if (log.shouldDebug()) {
				DragonAPICore.logError("Security Manager locked field "+field+" in "+obj);
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

	public static void setFinalField(Class c, String s, Object instance, Object o) throws Exception {
		setFinalField(c.getField(s), instance, o);
	}

	public static void setFinalField(Field f, Object instance, Object o) throws Exception {
		f.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
		f.set(instance, o);
	}

	public static Collection<Field> getFields(Class c, FieldSelector sel) {
		Collection<Field> li = new ArrayList();
		while (c != null) {
			Field[] fd = c.getDeclaredFields();
			for (int i = 0; i < fd.length; i++) {
				Field f = fd[i];
				if (sel.isValid(f))
					li.add(f);
			}
			c = c.getSuperclass();
		}
		return li;
	}

	public static Collection<Method> getMethods(Class c, MethodSelector sel) {
		Collection<Method> li = new ArrayList();
		while (c != null) {
			Method[] fd = c.getDeclaredMethods();
			for (int i = 0; i < fd.length; i++) {
				Method f = fd[i];
				if (sel.isValid(f))
					li.add(f);
			}
			c = c.getSuperclass();
		}
		return li;
	}

	public static interface FieldSelector {
		public boolean isValid(Field f);
	}

	public static interface MethodSelector {
		public boolean isValid(Method m);
	}

	public static final class TypeSelector implements FieldSelector {

		public final Class type;

		public TypeSelector(Class c) {
			type = c;
		}

		@Override
		public boolean isValid(Field f) {
			return f.getType() == type;
		}
	}

	public static boolean checkForField(Class c, String name, int... modifiers) {
		try {
			Field f = c.getDeclaredField(name);
			for (int i = 0; i < modifiers.length; i++) {
				int mod = modifiers[i];
				if ((f.getModifiers() & mod) == 0) {
					return false;
				}
			}
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	public static boolean checkForMethod(Class c, String name, Class[] args, int... modifiers) {
		try {
			Method f = c.getDeclaredMethod(name, args);
			for (int i = 0; i < modifiers.length; i++) {
				int mod = modifiers[i];
				if ((f.getModifiers() & mod) == 0) {
					return false;
				}
			}
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/** Note that this does not support overloading, as to do so would compromise performance. */
	public static Object cacheAndInvokeMethod(String cl, String name, Object ref, Object... args) {
		try {
			Method m = methodCache.get(cl, name);
			if (m == null) {
				try {
					Class c = Class.forName(cl);
					m = c.getDeclaredMethod(cl, getArgTypesFromArgs(args));
					m.setAccessible(true);
					methodCache.put(m, cl, name);
				}
				catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
			return m.invoke(ref, args);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static Class[] getArgTypesFromArgs(Object[] args) {
		Class[] arr = new Class[args.length];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = args[i].getClass();
		}
		return arr;
	}

}
