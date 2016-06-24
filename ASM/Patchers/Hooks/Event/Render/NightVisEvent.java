package Reika.DragonAPI.ASM.Patchers.Hooks.Event.Render;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class NightVisEvent extends Patcher {

	public NightVisEvent() {
		super("net.minecraft.client.renderer.EntityRenderer", "blt");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82830_a", "getNightVisionBrightness", "(Lnet/minecraft/entity/player/EntityPlayer;F)F");
		m.instructions.clear();
		/*
		m.instructions.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/NightVisionBrightnessEvent"));
		m.instructions.add(new InsnNode(Opcodes.DUP));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.FLOAD, 2));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/NightVisionBrightnessEvent", "<init>", "(Lnet/minecraft/entity/player/EntityPlayer;F)V", false));
		m.instructions.add(new VarInsnNode(Opcodes.ASTORE, 3));
		m.instructions.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		m.instructions.add(new InsnNode(Opcodes.POP));
		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
		m.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, "Reika/DragonAPI/Instantiable/Event/Client/NightVisionBrightnessEvent", "brightness", "F"));
		m.instructions.add(new InsnNode(Opcodes.FRETURN));
		 */

		m.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		m.instructions.add(new VarInsnNode(Opcodes.FLOAD, 2));
		m.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/NightVisionBrightnessEvent", "fire", "(Lnet/minecraft/entity/player/EntityPlayer;F)F", false));
		m.instructions.add(new InsnNode(Opcodes.FRETURN));
	}
}
