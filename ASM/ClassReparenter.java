/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Exception.ASMException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class ClassReparenter implements IClassTransformer {

	private static final boolean DEBUG = true;

	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		if (!FMLForgePlugin.RUNTIME_DEOBF)
			return bytes;

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		String s = this.parseClass(classNode);
		if (s != null) {
			if (DEBUG) {
				ReikaJavaLibrary.pConsole(String.format("DRAGONAPI ASM: Redirecting parent of class %s from %s to %s due to missing dependencies.", classNode.name, s, classNode.superName));
			}
		}
		Iterator<InnerClassNode> classes = classNode.innerClasses.iterator();
		while(classes.hasNext()) {
			InnerClassNode method = classes.next();
			//parseClass(method);
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		classNode.check(classNode.version);
		return writer.toByteArray();
	}

	private String parseClass(ClassNode cn) {
		if (cn.visibleAnnotations == null) {
			return null;
		}
		for (AnnotationNode ann : cn.visibleAnnotations) {
			if (ann.desc.equals("LReika/DragonAPI/ASM/ClassReparenter$Reparent;")) {
				if (ann.values != null) {
					for (int x = 0; x < ann.values.size() - 1; x += 2) {
						Object key = ann.values.get(x);
						Object values = ann.values.get(x+1);
						if (key instanceof String && key.equals("value")) {
							if (values instanceof ArrayList) {
								String[] parts = (String[])((ArrayList)values).toArray(new String[((ArrayList)values).size()]);
								if (parts.length != 2)
									throw new InvalidReparentAnnotationException(cn, ann, "Wrong number of arguments!");
								return this.handleReparent(cn, parts) ? parts[0] : null;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private boolean handleReparent(ClassNode cn, String[] parts) {
		if (!ReikaASMHelper.checkForClass(parts[0])) {
			parts[1] = parts[1].replace('.', '/');
			cn.superName = parts[1];
			return true;
		}
		return false;
	}

	private static class InvalidReparentAnnotationException extends ASMException {

		private final AnnotationNode annotation;
		private final String message;

		public InvalidReparentAnnotationException(ClassNode cn, AnnotationNode ann, String msg) {
			super(cn);
			annotation = ann;
			message = msg;
		}

		@Override
		public final String getMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append(super.getMessage());
			sb.append("Annotation type "+annotation.desc+" is not valid reparent annotation: ");
			sb.append("!");
			sb.append(message);
			return sb.toString();
		}

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface Reparent {
		String[] value();
	}
}
