package reika.dragonapi.asm.patchers.hooks.event;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class HotbarKeyEvent extends Patcher {

	public HotbarKeyEvent() {
		super("net.minecraft.client.gui.inventory.GuiContainer", "bex");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_146983_a", "checkHotbarKeys", "(I)Z");
		String f = FMLForgePlugin.RUNTIME_DEOBF ? "field_147006_u" : "theSlot";
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", f, "Lnet/minecraft/inventory/Slot;"));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/HotbarKeyEvent", "fire", "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/inventory/Slot;II)Z", false));
		AbstractInsnNode jump = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.IF_ICMPNE);
		AbstractInsnNode end = jump.getPrevious();
		AbstractInsnNode start = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(end), Opcodes.ILOAD, 1);
		ReikaASMHelper.deleteFrom(m.instructions, start, end);
		m.instructions.insertBefore(jump, li);
		ReikaASMHelper.changeOpcode(jump, Opcodes.IFEQ);
	}
}
