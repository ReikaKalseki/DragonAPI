package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

import cpw.mods.fml.relauncher.Side;


public class SettingsEvents extends Patcher {

	public SettingsEvents() {
		super("net.minecraft.client.settings.GameSettings", "bbj");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_74300_a", "loadOptions", "()V");
		this.addCall(cn, m, ReikaASMHelper.getFirstMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_74508_b" : "resetKeyBindingArrayAndHash"), "fireLoad");

		m = ReikaASMHelper.getMethodByName(cn, "func_74303_b", "saveOptions", "()V");
		//this.addCall(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_82879_c" : "sendSettingsToServer", "fireSave");
		this.addCall(cn, m, ReikaASMHelper.getFirstInsnAfter(m.instructions, 0, Opcodes.ALOAD, 1), "fireSave");
	}

	private void addCall(ClassNode cn, MethodNode m, AbstractInsnNode call, String hook) {
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 0));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/SettingsEvent", hook, "(Lnet/minecraft/client/settings/GameSettings;)V", false));
		m.instructions.insert(call, li);
	}

	@Override
	public boolean runsOnSide(Side s) {
		return s == Side.CLIENT;
	}

}
