/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
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
import java.util.Iterator;
import java.util.List;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;


public class DependentMethodStripper implements IClassTransformer
{
	private static final boolean DEBUG = true;
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		if (bytes == null) {
			return null;
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Iterator<FieldNode> fields = classNode.fields.iterator();
		while(fields.hasNext()) {
			FieldNode field = fields.next();
			if (this.remove(field.visibleAnnotations)) {
				if (DEBUG) {
					ReikaJavaLibrary.pConsole(String.format("Removing Field: %s.%s", classNode.name, field.name));
				}
				fields.remove();
			}
		}
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while(methods.hasNext()) {
			MethodNode method = methods.next();
			if (this.remove(method.visibleAnnotations)) {
				if (DEBUG) {
					ReikaJavaLibrary.pConsole(String.format("Removing Method: %s.%s%s", classNode.name, method.name, method.desc));
				}
				methods.remove();
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);
		classNode.check(classNode.version);
		return writer.toByteArray();
	}

	private boolean remove(List<AnnotationNode> anns) {
		if (anns == null) {
			return false;
		}
		if (!FMLForgePlugin.RUNTIME_DEOBF) //prevents needing to always reload game in dev env
			return false;
		for (AnnotationNode ann : anns) {
			if (ann.desc.equals("LReika/DragonAPI/ASM/DependentMethodStripper$ModDependent;")) {
				if (ann.values != null) {
					for (int x = 0; x < ann.values.size() - 1; x += 2) {
						Object key = ann.values.get(x);
						Object values = ann.values.get(x+1);
						if (key instanceof String && key.equals("value")) {
							if (values instanceof String[]) {
								String[] value = (String[])values;
								ModList mod = ModList.valueOf(value[1]);
								//ReikaJavaLibrary.pConsole(mod+": "+Arrays.toString(value));
								if (!mod.isLoaded()) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.METHOD, ElementType.FIELD})
	public static @interface ModDependent {
		ModList value();
	}
}
