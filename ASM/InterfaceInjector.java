/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class InterfaceInjector implements IClassTransformer {

	private static final boolean DEBUG = true;
	private static final String SKIP_PROPERTY = "Reika.ignoreMismatchedInterfaces";

	private final boolean crashOnError;

	public InterfaceInjector() {
		crashOnError = !Boolean.valueOf(System.getProperty(SKIP_PROPERTY));
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Collection<String> c = this.getInterfacesFor(classNode);
		if (c == null)
			return bytes;

		this.tryInjectInterfaces(classNode, c);

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		classNode.check(classNode.version);
		return writer.toByteArray();
	}

	private Collection<String> getInterfacesFor(ClassNode cn) {
		if (cn.visibleAnnotations != null) {
			for (AnnotationNode ann : cn.visibleAnnotations) {
				if (ann.desc.equals("LReika/DragonAPI/ASM/InterfaceInjector$Injectable;")) {
					for (int x = 0; x < ann.values.size() - 1; x += 2) {
						Object key = ann.values.get(x);
						Object values = ann.values.get(x+1);
						if (key instanceof String && key.equals("value")) {
							if (values instanceof List && !((List)values).isEmpty() && ((List)values).get(0) instanceof String) {
								return (List<String>)values;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void tryInjectInterfaces(ClassNode cn, Collection<String> c) {
		if (DEBUG)
			ReikaJavaLibrary.pConsole("DRAGONAPI ASM: Injecting "+c.size()+" interfaces into "+cn.name+": "+c);
		for (String cl : c) {
			ClassNode inter = this.getInterfaceFromString(cl);
			if (inter == null) {
				if (DEBUG)
					ReikaJavaLibrary.pConsole("DRAGONAPI ASM: Interface class "+cl+" not found. Injection failed.");
			}
			else {
				this.tryInjectInterface(cn, inter);
			}
		}
	}

	private void tryInjectInterface(ClassNode cn, ClassNode inter) {
		Collection<MethodNode> missing = new ArrayList();
		for (MethodNode mn : inter.methods) {
			if (!ReikaASMHelper.classContainsMethod(cn, mn) && !ReikaASMHelper.checkIfClassInheritsMethod(cn, mn)) {
				missing.add(mn);
			}
		}
		if (!missing.isEmpty()) {
			ImproperImplementationException e = new ImproperImplementationException(cn, inter, missing);
			if (crashOnError)
				throw e;
			else {
				ReikaJavaLibrary.pConsole("DRAGONAPI ASM: Interface "+inter.name+" could not be injected to "+cn.name+"; improper implementation.");
				e.printStackTrace();
			}
		}
		else {
			cn.interfaces.add(inter.name);
			if (DEBUG)
				ReikaJavaLibrary.pConsole("DRAGONAPI ASM: Interface class "+inter.name+" successfully injected into "+cn.name+".");
		}
	}

	private ClassNode getInterfaceFromString(String s) {
		try {
			byte[] data = Launch.classLoader.getClassBytes(s);
			if (data == null)
				return null;
			else {
				ClassNode cn = new ClassNode();
				new ClassReader(data).accept(cn, 0);
				return cn;
			}
		}
		catch (IOException e) {
			return null;
		}
	}

	private static class ImproperImplementationException extends ASMException {

		private final ClassNode interface_;
		private final Collection<MethodNode> missingMethods;
		private final Collection<String> missingMethodNames = new ArrayList();

		protected ImproperImplementationException(ClassNode cn, ClassNode interf, Collection<MethodNode> ms) {
			super(cn);
			interface_ = interf;
			missingMethods = ms;
			for (MethodNode m : missingMethods)
				missingMethodNames.add(m.name);
		}

		@Override
		public final String getMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append(super.getMessage());
			sb.append("Could not inject interface "+interface_.name+";\n");
			sb.append(node.name+" does not implement the following required methods:\n");
			for (MethodNode m : missingMethods) {
				sb.append("\t"+m.name+" "+m.desc);
				sb.append("\n");
			}
			sb.append("\nIn all likelihood, the interface has changed and its implementation requires correction.\n");
			sb.append("The following methods were found on the class:\n");
			for (MethodNode m : node.methods) {
				sb.append("\t"+m.name+" "+m.desc);
				if (missingMethodNames.contains(m.name)) {
					sb.append(" << Method matches a missing method name but not signature; this is likely the error.");
				}
				sb.append("\n");
			}
			return sb.toString();
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface Injectable {

		public String[] value();

	}
}
