package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class SaveOpen extends Patcher {

	public SaveOpen() {
		super("net.minecraft.world.storage.SaveHandler", "ayq");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "(Ljava/io/File;Ljava/lang/String;Z)V");
		AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.RETURN);
		InsnList li = new InsnList();
		String s = ReikaASMHelper.convertClassName(cn, false);
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, s, (FMLForgePlugin.RUNTIME_DEOBF ? "field_75770_b" : "worldDirectory"), "Ljava/io/File;"));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, s, (FMLForgePlugin.RUNTIME_DEOBF ? "field_75769_e" : "initializationTime"), "J"));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "trackSaveHandleStart", "(Lnet/minecraft/world/storage/SaveHandler;Ljava/io/File;J)V", false));
		m.instructions.insertBefore(ain, li);
	}

	@Override
	public boolean isDisabledByDefault() {
		return true;
	}

}
