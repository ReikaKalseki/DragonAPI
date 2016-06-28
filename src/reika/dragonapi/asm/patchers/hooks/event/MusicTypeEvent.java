package reika.dragonapi.asm.patchers.hooks.event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class MusicTypeEvent extends Patcher {

	public MusicTypeEvent() {
		super("net.minecraft.client.Minecraft", "bao");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_147109_W", "func_147109_W", "()Lnet/minecraft/client/audio/MusicTicker$MusicType;");
		m.instructions.clear();
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/MusicTypeEvent", "fire", "(Lnet/minecraft/client/Minecraft;)Lnet/minecraft/client/audio/MusicTicker$MusicType;", false));
		m.instructions.add(new InsnNode(Opcodes.ARETURN));
	}
}
