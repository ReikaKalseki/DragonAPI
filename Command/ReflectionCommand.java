/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.UUID;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import Reika.DragonAPI.Instantiable.Data.Maps.PlayerMap;
import Reika.DragonAPI.Libraries.Java.ReikaReflectionHelper;


public class ReflectionCommand extends DragonCommandBase {

	private final UUID consoleUUID = UUID.randomUUID();

	private final PlayerMap<Object> referenceObjects = new PlayerMap();

	private static final HashMap<String, String> SRGMap = new HashMap();

	static {
		SRGMap.put("getBlock", "func_147439_a");
		SRGMap.put("getBlockMetadata", "func_72805_g");
		SRGMap.put("getTileEntity", "func_147438_o");
		SRGMap.put("getPlayerEntityByName", "func_72924_a");
		SRGMap.put("getPlayerEntityByUUID", "func_152378_a");
	}

	@Override
	public void processCommand(ICommandSender ics, String[] args) {

		HashMap<String, Object> prefabReferences = new HashMap();
		//prefabReferences.put("world", DimensionManager.g) //not necessary (or possible if from console)

		if (args.length < 2) {
			this.error(ics, "Not enough arguments. Need to specify at least class and member name.");
			return;
		}

		Class c = null;
		try {
			c = Class.forName(args[0]);
		}
		catch (ClassNotFoundException e) {
			this.error(ics, "No such class '"+args[0]+"'");
			return;
		}

		if (SRGMap.containsKey(args[1]))
			args[1] = SRGMap.get(args[1]);

		Field f = ReikaReflectionHelper.getProtectedInheritedField(c, args[1]);
		Method m = null;

		if (f == null) {
			Class[] types = null;
			try {
				types = this.parseTypes(args[2]);
			}
			catch (ClassNotFoundException e) {
				this.error(ics, e.toString());
				return;
			}
			m = ReikaReflectionHelper.getProtectedInheritedMethod(c, args[1], types);
		}

		if (f == null && m == null) {
			this.error(ics, "No such field or method '"+args[1]+"' inherited or declared by '"+c.getName()+"'");
		}

		if (f != null) {
			this.tryInvokeField(ics, args, f, prefabReferences);
		}
		else if (m != null) {
			if (args.length < 5) {
				this.error(ics, "Not enough arguments. Need to specify class, method name, reference object (optional), method arg types, and method args.");
				return;
			}
			this.tryInvokeMethod(ics, args, m, prefabReferences);
		}
	}

	private Class[] parseTypes(String arg) throws ClassNotFoundException {
		String[] parts = arg.split(";");
		Class[] types = new Class[parts.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = Class.forName(parts[i]);
		}
		return types;
	}

	private Object[] parseArgs(String arg) {
		String[] parts = arg.split(";");
		Object[] vals = new Object[parts.length];
		for (int i = 0; i < vals.length; i++) {
			vals[i] = this.parseObject(parts[i]);
		}
		return vals;
	}

	private Object parseObject(String s) {
		if (s.equalsIgnoreCase("null") || s.equalsIgnoreCase("nil"))
			return null;
		if (s.equalsIgnoreCase("true"))
			return true;
		if (s.equalsIgnoreCase("false"))
			return false;
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e) {

		}
		try {
			return Double.parseDouble(s);
		}
		catch (NumberFormatException e) {

		}
		return s;
	}

	private void tryInvokeField(ICommandSender ics, String[] args, Field f, HashMap<String, Object> prefabReferences) {
		f.setAccessible(true);
		boolean canInvoke = false;
		if (Modifier.isStatic(f.getModifiers())) {
			referenceObjects.directRemove(this.getUUID(ics));
			canInvoke = true;
		}
		else if (args.length == 3 && prefabReferences.containsKey(args[2])) {
			referenceObjects.directPut(this.getUUID(ics), prefabReferences.get(args[2]));
			canInvoke = true;
		}
		else {
			canInvoke = referenceObjects.directGet(this.getUUID(ics)) != null;
		}

		if (canInvoke) {
			try {
				UUID uid = this.getUUID(ics);
				Object ret = f.get(referenceObjects.directGet(uid));
				referenceObjects.directPut(uid, ret);
				this.sendChatToSender(ics, ret != null ? ret.getClass()+": "+String.valueOf(ret) : "null");
			}
			catch (ReflectiveOperationException e) {
				this.error(ics, e.toString());
			}
		}
		else {
			this.error(ics, "Field "+f.getName()+" is not static, and there is no reference object for it.");
		}
	}

	private void tryInvokeMethod(ICommandSender ics, String[] args, Method m, HashMap<String, Object> prefabReferences) {
		m.setAccessible(true);
		boolean canInvoke = false;
		if (Modifier.isStatic(m.getModifiers())) {
			referenceObjects.directRemove(this.getUUID(ics));
			canInvoke = true;
		}
		else if (prefabReferences.containsKey(args[3])) {
			referenceObjects.directPut(this.getUUID(ics), prefabReferences.get(args[3]));
			canInvoke = true;
		}
		else {
			canInvoke = referenceObjects.directGet(this.getUUID(ics)) != null;
		}

		if (canInvoke) {
			try {
				UUID uid = this.getUUID(ics);
				Object ret = m.invoke(referenceObjects.directGet(uid), this.parseArgs(args[4]));
				referenceObjects.directPut(uid, ret);
				this.sendChatToSender(ics, ret != null ? ret.getClass()+": "+String.valueOf(ret) : "null");
			}
			catch (ReflectiveOperationException e) {
				this.error(ics, e.toString());
			}
		}
		else {
			this.error(ics, "Method "+m.getName()+" is not static, and there is no reference object for it.");
		}
	}

	private UUID getUUID(ICommandSender ics) {
		try {
			return this.getCommandSenderAsPlayer(ics).getPersistentID();
		}
		catch (Exception e) {
			return consoleUUID;
		}
	}

	private void error(ICommandSender ics, String s) {
		this.sendChatToSender(ics, EnumChatFormatting.RED+s);
	}

	@Override
	public String getCommandString() {
		return "reflectiveget";
	}

	@Override
	protected boolean isAdminOnly() {
		return true;
	}
	/*
	private static abstract class Reflection { //A reflective get

		protected final Class type;
		protected final Object reference;
		protected final String member;

		private Reflection(Class c, Object o, String s) {
			type = c;
			reference = o;
			member = s;
		}

		protected abstract Object invoke() throws ReflectiveOperationException;

	}

	private static final class FieldReflection extends Reflection {

		private FieldReflection(Class c, Object o, String s) {
			super(c, o, s);
		}

		@Override
		protected Object invoke() throws ReflectiveOperationException {
			Field f = ReikaReflectionHelper.getProtectedInheritedField(reference, member);
			if (f == null)
				throw new NoSuchFieldException("No such field '"+member+"' inherited or declared by "+type.getName()+"'");
			f.setAccessible(true);
			return f.get(reference);
		}

	}

	private static final class MethodReflection extends Reflection {

		private final Class[] argTypes;
		private final Object[] args;

		private MethodReflection(Class c, Object o, String s, Class[] at, Object[] a) {
			super(c, o, s);
			argTypes = at;
			args = a;
		}

		@Override
		protected Object invoke() throws ReflectiveOperationException {
			Method m = ReikaReflectionHelper.getProtectedInheritedMethod(reference, member, argTypes);
			if (m == null)
				throw new NoSuchMethodException("No such method '"+member+"' inherited or declared by "+type.getName()+"'");
			m.setAccessible(true);
			return m.invoke(reference, args);
		}

	}*/
}
