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

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class MusicEvent extends Patcher {

	public MusicEvent() {
		super("net.minecraft.client.audio.MusicTicker", "btg");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_73660_a", "update", "()V");

		String handler = FMLForgePlugin.RUNTIME_DEOBF ? "func_147118_V" : "getSoundHandler"; //()Lnet/minecraft/client/audio/SoundHandler;
		String play = FMLForgePlugin.RUNTIME_DEOBF ? "func_147682_a" : "playSound"; //(Lnet/minecraft/client/audio/ISound;)V

		LabelNode L5 = new LabelNode();

		InsnList post = new InsnList();

		post.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lcpw/mods/fml/common/eventhandler/EventBus;"));
		post.add(new TypeInsnNode(Opcodes.NEW, "Reika/DragonAPI/Instantiable/Event/Client/PlayMusicEvent"));
		post.add(new InsnNode(Opcodes.DUP));
		post.add(new VarInsnNode(Opcodes.ALOAD, 0));
		post.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/audio/MusicTicker", "field_147678_c", "Lnet/minecraft/client/audio/ISound;"));
		post.add(new VarInsnNode(Opcodes.ALOAD, 0));
		post.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/audio/MusicTicker", "field_147676_d", "I"));
		post.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "Reika/DragonAPI/Instantiable/Event/Client/PlayMusicEvent", "<init>", "(Lnet/minecraft/client/audio/ISound;I)V", false));
		post.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cpw/mods/fml/common/eventhandler/EventBus", "post", "(Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
		post.add(new JumpInsnNode(Opcodes.IFNE, L5));

		AbstractInsnNode loc = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/client/audio/SoundHandler", play, "(Lnet/minecraft/client/audio/ISound;)V");

		m.instructions.insert(loc, L5);

		loc = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(loc), Opcodes.ALOAD, 0); //Get last ALOAD 0 before
		loc = ReikaASMHelper.getLastInsnBefore(m.instructions, m.instructions.indexOf(loc), Opcodes.ALOAD, 0); //Get next last ALOAD 0

		m.instructions.insertBefore(loc, post);
	}
}
