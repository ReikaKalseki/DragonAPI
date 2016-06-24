package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class SoundEvents extends Patcher {

	public SoundEvents() {
		super("net.minecraft.client.audio.SoundManager", "btj");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_148594_a", "getNormalizedVolume", "(Lnet/minecraft/client/audio/ISound;Lnet/minecraft/client/audio/SoundPoolEntry;Lnet/minecraft/client/audio/SoundCategory;)F");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/SoundVolumeEvent", "fire", "(Lnet/minecraft/client/audio/ISound;Lnet/minecraft/client/audio/SoundPoolEntry;Lnet/minecraft/client/audio/SoundCategory;)F", false));
		m.instructions.add(new InsnNode(Opcodes.FRETURN));

		m = ReikaASMHelper.getMethodByName(cn, "func_148606_a", "getNormalizedPitch", "(Lnet/minecraft/client/audio/ISound;Lnet/minecraft/client/audio/SoundPoolEntry;)F");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/SoundPitchEvent", "fire", "(Lnet/minecraft/client/audio/ISound;Lnet/minecraft/client/audio/SoundPoolEntry;)F", false));
		m.instructions.add(new InsnNode(Opcodes.FRETURN));
	}
}
