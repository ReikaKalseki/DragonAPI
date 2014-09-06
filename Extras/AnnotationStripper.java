package Reika.DragonAPI.Extras;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.Extras.APIStripper.Strippable;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;

/** Credit to KingLemming and Co. for this @Strippable annotation reader and ASM handler. */
class AnnotationStripper {

	static THashSet<String> strippables;
	static final String strippableDesc;
	static String side;

	static {
		strippableDesc = Type.getDescriptor(Strippable.class);
		strippables = new THashSet<String>(10);
	}

	static final ArrayList<String> workingPath = new ArrayList<String>();
	private static final String[] emptyList = {};
	static class AnnotationInfo {
		public String side;
		public String[] values = emptyList;
	}

	private static ClassNode world = null, worldServer = null;

	static byte[] parse(String name, String transformedName, byte[] bytes) {
		workingPath.add(transformedName);
		if (strippables.contains(name)) {
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			if (strip(cn)) {
				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
				cn.accept(cw);
				bytes = cw.toByteArray();
			}
		}
		workingPath.remove(workingPath.size() - 1);
		return bytes;
	}

	static synchronized void HACK(String name, byte[] bytes) {
		synchronized (workingPath) {
			workingPath.add(name);
			ClassReader cr = new ClassReader(bytes);
			ClassNode cn = new ClassNode();
			cr.accept(cn, 0);
			if (cn.innerClasses != null) {
				for (InnerClassNode node : cn.innerClasses) {
					if (!workingPath.contains(node.name)) {
						try {
							Class.forName(node.name, false, AnnotationStripper.class.getClassLoader());
						} catch (Throwable _) {
						}
					}
				}
			}
			workingPath.remove(workingPath.size() - 1);
		}
	}

	static boolean strip(ClassNode cn) {
		boolean altered = false;
		if (cn.visibleAnnotations != null) {
			for (AnnotationNode n : cn.visibleAnnotations) {
				AnnotationInfo node = parseAnnotation(n, strippableDesc);
				if (node != null) {
					String[] value = node.values;
					boolean wrongSide = side == node.side;
					for (int j = 0, l = value.length; j < l; ++j) {
						String clazz = value[j];
						String cz = clazz.replace('.', '/');
						if (cn.interfaces.contains(cz)) {
							boolean remove = true;
							try {
								if (!wrongSide && !workingPath.contains(clazz)) {
									Class.forName(clazz, false, AnnotationStripper.class.getClassLoader());
									remove = false;
								}
							} catch (Throwable _) {
							}
							if (remove) {
								cn.interfaces.remove(cz);
								altered = true;
							}
						}
					}
				}
			}
		}
		if (cn.methods != null) {
			Iterator<MethodNode> iter = cn.methods.iterator();
			while (iter.hasNext()) {
				MethodNode mn = iter.next();
				if (mn.visibleAnnotations != null) {
					for (AnnotationNode node : mn.visibleAnnotations) {
						if (checkRemove(parseAnnotation(node, strippableDesc), iter)) {
							altered = true;
							break;
						}
					}
				}
			}
		}
		if (cn.fields != null) {
			Iterator<FieldNode> iter = cn.fields.iterator();
			while (iter.hasNext()) {
				FieldNode fn = iter.next();
				if (fn.visibleAnnotations != null) {
					for (AnnotationNode node : fn.visibleAnnotations) {
						if (checkRemove(parseAnnotation(node, strippableDesc), iter)) {
							altered = true;
							break;
						}
					}
				}
			}
		}
		return altered;
	}

	static boolean checkRemove(AnnotationInfo node, Iterator<? extends Object> iter) {
		if (node != null) {
			boolean needsRemoved = node.side == side;
			if (!needsRemoved) {
				String[] value = node.values;
				for (int j = 0, l = value.length; j < l; ++j) {
					String clazz = value[j];
					if (clazz.startsWith("mod:")) {
						needsRemoved = !Loader.isModLoaded(clazz.substring(4));
					}
					else if (clazz.startsWith("api:")) {
						needsRemoved = !ModAPIManager.INSTANCE.hasAPI(clazz.substring(4));
					}
					else {
						try {
							if (!workingPath.contains(clazz)) {
								Class.forName(clazz, false, AnnotationStripper.class.getClassLoader());
							}
						} catch (Throwable _) {
							needsRemoved = true;
						}
					}
					if (needsRemoved) {
						break;
					}
				}
			}
			if (needsRemoved) {
				iter.remove();
				return true;
			}
		}
		return false;
	}
	// }
	static AnnotationInfo parseAnnotation(AnnotationNode node, String desc) {
		AnnotationInfo info = null;
		if (node.desc.equals(desc)) {
			info = new AnnotationInfo();
			if (node.values != null) {
				List<Object> values = node.values;
				for (int i = 0, e = values.size(); i < e;) {
					Object k = values.get(i++);
					Object v = values.get(i++);
					if ("value".equals(k)) {
						if (!(v instanceof List && ((List<?>) v).size() > 0 && ((List<?>) v).get(0) instanceof String)) {
							continue;
						}
						info.values = ((List<?>) v).toArray(emptyList);
					}
					else if ("side".equals(k) && v instanceof String[]) {
						String t = ((String[]) v)[1];
						if (t != null) {
							info.side = t.toUpperCase().intern();
						}
					}
				}
			}
		}
		return info;
	}

	static void scrapeData(ASMDataTable table) {
		side = FMLCommonHandler.instance().getSide().toString().toUpperCase().intern();
		for (ASMData data : table.getAll(Strippable.class.getName())) {
			String name = data.getClassName();
			strippables.add(name);
			strippables.add(name + "$class");
		}
	}
}