package Reika.DragonAPI.Instantiable;

import java.lang.reflect.Modifier;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

/** Used to add extra data to a TileEntity whose code you do not control, and have NBT hooks to make that data persistent if necessary.
 * ONLY ONE of these may be added per class!
 * */
public abstract class ExtraTileDataHandler {

	private static final String FIELD_NAME = "extraNBTDataASMHook";

	public abstract void writeNBT(TileEntity tile, NBTTagCompound NBT);

	public abstract void readNBT(TileEntity tile, NBTTagCompound NBT);

	public static final InsnList injectHandler(ClassNode cn, Class<? extends ExtraTileDataHandler> object, boolean construct, boolean addNBT) {
		String n2 = ReikaASMHelper.convertClassName(object, true);
		ReikaASMHelper.addField(cn, FIELD_NAME, n2, Modifier.PRIVATE, null);
		InsnList get = getLoadOfHandler(cn, object);
		if (construct) {
			String n = ReikaASMHelper.convertClassName(object, false);
			InsnList li = new InsnList();
			li.add(new VarInsnNode(Opcodes.ALOAD, 0));
			li.add(new TypeInsnNode(Opcodes.NEW, n));
			li.add(new InsnNode(Opcodes.DUP));
			li.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, n, "<init>", "()V", false));
			li.add(new FieldInsnNode(Opcodes.PUTFIELD, ReikaASMHelper.convertClassName(cn, false), FIELD_NAME, n2));
			li.add(new InsnNode(Opcodes.RETURN));

			try {
				MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "()V");
				li.remove(li.getLast());
				AbstractInsnNode loc = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.RETURN);
				m.instructions.insertBefore(loc, li);
			}
			catch (NoSuchASMMethodException e) {
				ReikaASMHelper.addMethod(cn, li, "<init>", "()V", Modifier.PUBLIC);
			}
		}
		if (addNBT) {
			injectNBTHandling(cn, object, get);
		}
		return get;
	}

	private static final void injectNBTHandling(ClassNode cn, Class<? extends ExtraTileDataHandler> object, InsnList get) {
		String n = ReikaASMHelper.convertClassName(object, false);
		String sig = "(Lnet/minecraft/nbt/NBTTagCompound;)V";
		String sig2 = ReikaASMHelper.addLeadingArgument("(Lnet/minecraft/nbt/NBTTagCompound;)V", "Lnet/minecraft/tileentity/TileEntity;");
		MethodNode write = ReikaASMHelper.getMethodByName(cn, "func_145841_b", "writeToNBT", sig);
		MethodNode read = ReikaASMHelper.getMethodByName(cn, "func_145839_a", "readFromNBT", sig);

		InsnList save = new InsnList();
		InsnList load = new InsnList();

		save.add(ReikaASMHelper.copyInsnList(get));
		save.add(new VarInsnNode(Opcodes.ALOAD, 0));
		save.add(new VarInsnNode(Opcodes.ALOAD, 1));
		save.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, n, "writeNBT", sig2, false));

		load.add(ReikaASMHelper.copyInsnList(get));
		load.add(new VarInsnNode(Opcodes.ALOAD, 0));
		load.add(new VarInsnNode(Opcodes.ALOAD, 1));
		load.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, n, "readNBT", sig2, false));

		AbstractInsnNode loc1 = ReikaASMHelper.getFirstInsnAfter(write.instructions, 0, Opcodes.ALOAD, 1);
		AbstractInsnNode loc2 = ReikaASMHelper.getFirstInsnAfter(read.instructions, 0, Opcodes.ALOAD, 1);

		write.instructions.insertBefore(loc1, save);
		read.instructions.insertBefore(loc2, load);
	}

	private static final InsnList getLoadOfHandler(ClassNode cn, Class<? extends ExtraTileDataHandler> object) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, cn.name, FIELD_NAME, ReikaASMHelper.convertClassName(object, true)));
		return li;
	}

}
