/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ASM.Patchers.Hooks.Event;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraftforge.classloading.FMLForgePlugin;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class MusicEvent extends Patcher {

	public MusicEvent() {
		super("net.minecraft.client.audio.MusicTicker", "btg");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73660_a", "update", "()V");

		MethodInsnNode min = ReikaASMHelper.getFirstMethodCallByName(cn, m, FMLForgePlugin.RUNTIME_DEOBF ? "func_147682_a" : "playSound");
		min.owner = "Reika/DragonAPI/Instantiable/Event/Client/PlayMusicEvent";
		min.name = "fire";
		min.desc = "(Lnet/minecraft/client/audio/SoundHandler;Lnet/minecraft/client/audio/ISound;Lnet/minecraft/client/audio/MusicTicker;)V";
		min.setOpcode(Opcodes.INVOKESTATIC);

		m.instructions.insertBefore(min, new VarInsnNode(Opcodes.ALOAD, 0));
	}
}
