package Reika.DragonAPI.ASM.Patchers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class CursorStackRender extends Patcher {

	public CursorStackRender() {
		super("net.minecraft.client.gui.inventory.GuiContainer", "bex");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73863_a", "drawScreen", "(IIF)V");
		String func = FMLForgePlugin.RUNTIME_DEOBF ? "func_146982_a" : "drawItemStack";
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, func);
		String ri = "Lnet/minecraft/client/renderer/entity/RenderItem;";
		String fr = "Lnet/minecraft/client/gui/FontRenderer;";
		String is = "Lnet/minecraft/item/ItemStack;";
		min.setOpcode(Opcodes.INVOKESTATIC);
		min.owner = "Reika/DragonAPI/ASM/ASMCallsClient";
		min.name = "renderCursorStack";
		min.desc = "(Lnet/minecraft/client/gui/inventory/GuiContainer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;"+ri+fr+is+")V";
		String ref = ReikaASMHelper.convertClassName(cn, false);
		InsnList li = new InsnList();
		li.add(new FieldInsnNode(Opcodes.GETSTATIC, ref, FMLForgePlugin.RUNTIME_DEOBF ? "field_146296_j" : "itemRender", ri));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, ref, FMLForgePlugin.RUNTIME_DEOBF ? "field_146289_q" : "fontRendererObj", fr));
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new FieldInsnNode(Opcodes.GETFIELD, ref, FMLForgePlugin.RUNTIME_DEOBF ? "field_147012_x" : "draggedStack", is));
		m.instructions.insertBefore(min, li);
	}

}
