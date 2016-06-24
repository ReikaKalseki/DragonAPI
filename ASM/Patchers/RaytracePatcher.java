package Reika.DragonAPI.ASM.Patchers;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodInstructionException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public abstract class RaytracePatcher extends Patcher {

	public RaytracePatcher(String deobf, String obf) {
		super(deobf, obf);
	}

	@Override
	protected final void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_70071_h_", "onUpdate", "()V");

		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/EntityAboutToRayTraceEvent", "fire", "(Lnet/minecraft/entity/Entity;)V", false));

		String func1 = FMLForgePlugin.RUNTIME_DEOBF ? "func_149668_a" : "getCollisionBoundingBoxFromPool";
		String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_72933_a" : "rayTraceBlocks";
		String func3 = FMLForgePlugin.RUNTIME_DEOBF ? "func_147447_a" : "func_147447_a";

		String world = FMLForgePlugin.RUNTIME_DEOBF ? "field_70170_p" : "worldObj";

		AbstractInsnNode min1 = null;
		AbstractInsnNode min2 = null;
		AbstractInsnNode min3 = null;

		try {
			min1 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/block/Block", func1, "(Lnet/minecraft/world/World;III)Lnet/minecraft/util/AxisAlignedBB;");
		}
		catch (NoSuchASMMethodInstructionException e) {

		}
		try {
			min2 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", func2, "(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;");
		}
		catch (NoSuchASMMethodInstructionException e) {

		}
		try {
			min3 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", func3, "(Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;ZZZ)Lnet/minecraft/util/MovingObjectPosition;");
		}
		catch (NoSuchASMMethodInstructionException e) {

		}

		AbstractInsnNode pre1 = min1 != null ? ReikaASMHelper.getLastNonZeroALOADBefore(m.instructions, m.instructions.indexOf(min1)) : null;
		AbstractInsnNode pre2 = min2 != null ? ReikaASMHelper.getLastFieldRefBefore(m.instructions, m.instructions.indexOf(min2), world).getPrevious() : null;
		AbstractInsnNode pre3 = min3 != null ? ReikaASMHelper.getLastFieldRefBefore(m.instructions, m.instructions.indexOf(min3), world).getPrevious() : null;

		if (pre1 != null) {
			m.instructions.insertBefore(pre1, ReikaASMHelper.copyInsnList(li));
		}
		if (pre2 != null) {
			m.instructions.insertBefore(pre2, ReikaASMHelper.copyInsnList(li));
		}
		if (pre3 != null) {
			m.instructions.insertBefore(pre3, ReikaASMHelper.copyInsnList(li));
		}

		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}

}
