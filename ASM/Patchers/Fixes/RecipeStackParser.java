package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public abstract class RecipeStackParser extends Patcher {

	public RecipeStackParser(String s) {
		super(s);
	}

	public RecipeStackParser(String s1, String s2) {
		super(s1, s2);
	}

	@Override
	protected final void apply(ClassNode cn) {
		MethodNode m = this.getMethod(cn);
		AbstractInsnNode start = ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.NEW, "java/util/HashMap");
		AbstractInsnNode loc2 = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(start), Opcodes.ANEWARRAY, this.getArrayType());
		AbstractInsnNode end = loc2.getPrevious();
		while (!(end instanceof FrameNode))
			end = end.getPrevious();
		end = end.getPrevious();
		AbstractInsnNode ref = start.getPrevious();
		ReikaASMHelper.deleteFrom(cn, m.instructions, start, end);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ILOAD, this.getIndexVar()));
		li.add(new InsnNode(this.supportsOre() ? Opcodes.ICONST_1 : Opcodes.ICONST_0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 2));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "parseItemMappings", "(IZ[Ljava/lang/Object;)Ljava/util/HashMap;", false));
		li.add(new VarInsnNode(Opcodes.ASTORE, this.getMapVar()));
		m.instructions.insert(ref, li);
	}

	@Override
	public final boolean computeFrames() {
		return true;
	}

	protected abstract MethodNode getMethod(ClassNode cn);
	protected abstract String getArrayType();
	protected abstract boolean supportsOre();
	protected abstract int getMapVar();
	protected abstract int getIndexVar();

}
