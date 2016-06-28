package reika.dragonapi.asm.patchers.hooks.event.render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class ItemSizeTextEvent extends Patcher {

	public ItemSizeTextEvent() {
		super("net.minecraft.client.renderer.entity.RenderItem", "bny");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_94148_a", "renderItemOverlayIntoGUI", "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V");
		AbstractInsnNode ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.ASTORE);
		int var = ((VarInsnNode)ain).var;
		m.instructions.insert(ain, new VarInsnNode(Opcodes.ASTORE, var));
		m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/ItemSizeTextEvent", "fire", "(Lnet/minecraft/item/ItemStack;Ljava/lang/String;)Ljava/lang/String;", false));
		m.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, var));
		m.instructions.insert(ain, new VarInsnNode(Opcodes.ALOAD, 3));
		//ReikaJavaLibrary.pConsole(ReikaASMHelper.clearString(m.instructions));
	}
}
