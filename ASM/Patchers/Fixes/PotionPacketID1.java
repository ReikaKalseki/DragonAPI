package Reika.DragonAPI.ASM.Patchers.Fixes;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Exception.ASMException.NoSuchASMMethodException;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class PotionPacketID1 extends Patcher {

	public PotionPacketID1() {
		super("net.minecraft.network.play.server.S1DPacketEntityEffect", "in");
	}

	@Override
	protected void apply(ClassNode cn) {
		// if (Loader.isModLoaded("Potion ID Helper"))
		// break;
		FieldNode f = ReikaASMHelper.getFieldByName(cn, "field_149432_b", "field_149432_b");
		f.desc = "I";

		MethodNode m = ReikaASMHelper.getMethodByName(cn, "<init>", "(ILnet/minecraft/potion/PotionEffect;)V");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.I2B) {
				FieldInsnNode put = (FieldInsnNode)ain.getNext();
				put.desc = "I";
				m.instructions.remove(ain);
				break;
			}
		}

		m = ReikaASMHelper.getMethodByName(cn, "func_148837_a", "readPacketData", "(Lnet/minecraft/network/PacketBuffer;)V");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				String func = FMLForgePlugin.RUNTIME_DEOBF ? "readByte" : "readByte";
				String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "readInt" : "readInt";
				if (min.name.equals(func)) {
					min.name = func2;
					min.desc = "()I";
					FieldInsnNode put = (FieldInsnNode)ain.getNext();
					put.desc = "I";
					break;
				}
			}
		}

		m = ReikaASMHelper.getMethodByName(cn, "func_148840_b", "writePacketData", "(Lnet/minecraft/network/PacketBuffer;)V");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL) {
				MethodInsnNode min = (MethodInsnNode)ain;
				String func = FMLForgePlugin.RUNTIME_DEOBF ? "writeByte" : "writeByte";
				String func2 = FMLForgePlugin.RUNTIME_DEOBF ? "writeInt" : "writeInt";
				if (min.name.equals(func)) {
					min.name = func2;
					min.desc = "(I)Lio/netty/buffer/ByteBuf;";
					FieldInsnNode get = (FieldInsnNode)ain.getPrevious();
					get.desc = "I";
					break;
				}
			}
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			try {
				m = ReikaASMHelper.getMethodByName(cn, "func_149427_e", "func_149427_e", "()B");
			}
			catch (NoSuchASMMethodException e1) {
				try {
					ReikaASMHelper.getMethodByName(cn, "func_149427_e", "func_149427_e", "()I");
				}
				catch (NoSuchASMMethodException e2) {
					throw e1;
				}
			}
			m.desc = "()I"; // Change getID() return to int; does not need code
			// changes elsewhere, as it is passed into a
			// PotionEffect <init>.

			for (int i = 0; i < m.instructions.size(); i++) {
				AbstractInsnNode ain = m.instructions.get(i);
				if (ain.getOpcode() == Opcodes.GETFIELD) {
					FieldInsnNode fin = (FieldInsnNode)ain;
					fin.desc = "I";
					break;
				}
			}
		}
	}

}
