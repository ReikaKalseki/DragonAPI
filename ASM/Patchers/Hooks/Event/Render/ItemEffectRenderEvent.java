package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class ItemEffectRenderEvent extends Patcher {

	public ItemEffectRenderEvent() {
		super("net.minecraft.client.renderer.entity.RenderItem", "bny");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "renderDroppedItem", "(Lnet/minecraft/entity/item/EntityItem;Lnet/minecraft/util/IIcon;IFFFFI)V");
		String name = /*FMLForgePlugin.RUNTIME_DEOBF ? "" : */"hasEffect";
		Object[] patt = {
				new VarInsnNode(Opcodes.ALOAD, 19),
				new VarInsnNode(Opcodes.ILOAD, 8),
				Opcodes.INVOKEVIRTUAL,
				Opcodes.IFEQ,
		};
		AbstractInsnNode ain = ReikaASMHelper.getPattern(m.instructions, patt);
		if (ain == null) {
			ReikaASMHelper.throwConflict(this.toString(), cn, m, "Could not find instruction pattern for ItemStack.hasEffect(int pass), Insns:\n"+ReikaASMHelper.clearString(m.instructions));
		}
		MethodInsnNode min = (MethodInsnNode)ain.getNext().getNext();//ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/item/ItemStack", name, "(I)Z");
		min.owner = "Reika/DragonAPI/Instantiable/Event/Client/ItemEffectRenderEvent";
		min.name = "fire";
		min.desc = "(Lnet/minecraft/item/ItemStack;I)Z";
		min.setOpcode(Opcodes.INVOKESTATIC);
	}

}
