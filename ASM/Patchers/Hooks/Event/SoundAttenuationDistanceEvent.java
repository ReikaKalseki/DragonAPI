package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;


public class SoundAttenuationDistanceEvent extends Patcher {

	public SoundAttenuationDistanceEvent() {
		super("net.minecraft.client.audio.SoundManager", "btj");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_148611_c", "playSound", "(Lnet/minecraft/client/audio/ISound;)V");
		//INVOKEINTERFACE net/minecraft/client/audio/ISound.getVolume()F
		MethodInsnNode min = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/client/audio/ISound", FMLForgePlugin.RUNTIME_DEOBF ? "func_147653_e" : "getVolume", "()F");
		LdcInsnNode ldc = (LdcInsnNode)ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(min), Opcodes.LDC, 16F);
		InsnList li = new InsnList();
		li.add(new VarInsnNode(Opcodes.ALOAD, 1));
		li.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/Client/SoundAttenuationDistanceEvent", "fire", "(FLnet/minecraft/client/audio/ISound;)F", false));
		m.instructions.insert(ldc, li);
	}

}