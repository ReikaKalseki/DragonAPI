package Reika.DragonAPI.ASM.Patchers.Hooks.Event.World;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.BlockRenderBrightness;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class LightVisualBrightness extends BlockRenderBrightness {

	public LightVisualBrightness() {
		super("net.minecraft.world.World", "ahb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72801_o", "getLightBrightness", "(III)F");
		/*
		AbstractInsnNode ain = ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.FRETURN);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new VarInsnNode(Opcodes.ILOAD, 1));
		li.add(new VarInsnNode(Opcodes.ILOAD, 2));
		li.add(new VarInsnNode(Opcodes.ILOAD, 3));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/LightVisualBrightnessEvent", "fire", "(FLnet/minecraft/world/World;III)F", false));
		m.instructions.insertBefore(ain, li);
		 */
		MethodInsnNode min = (MethodInsnNode)ReikaASMHelper.getLastOpcode(m.instructions, Opcodes.INVOKEVIRTUAL);
		min.owner = "Reika/DragonAPI/Instantiable/Event/Client/LightVisualBrightnessEvent";
		min.name = "fire";
		min.desc = "(Lnet/minecraft/world/World;III)F";
		min.itf = false;
		min.setOpcode(Opcodes.INVOKESTATIC);
		m.instructions.remove(min.getNext()); //FALOAD

		this.patchBlockLight(cn);
	}

}
