package Reika.DragonAPI.ASM.Patchers.Fixes;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class PotionEffectID1 extends Patcher {

	public PotionEffectID1() {
		super("net.minecraft.potion.PotionEffect", "rw");
	}

	@Override
	protected void apply(ClassNode cn) {
		// if (Loader.isModLoaded("Potion ID Helper"))
		// break;
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_82719_a", "writeCustomPotionEffectToNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/nbt/NBTTagCompound;");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.I2B) {
				MethodInsnNode nbt = (MethodInsnNode)ain.getNext(); // set to
				// NBT
				nbt.name = FMLForgePlugin.RUNTIME_DEOBF ? "func_74768_a" : "setInteger";
				nbt.desc = "(Ljava/lang/String;I)V";
				m.instructions.remove(ain); // delete the byte cast
				break;
			}
		}

		m = ReikaASMHelper.getMethodByName(cn, "func_82722_b", "readCustomPotionEffectFromNBT", "(Lnet/minecraft/nbt/NBTTagCompound;)Lnet/minecraft/potion/PotionEffect;");
		for (int i = 0; i < m.instructions.size(); i++) {
			AbstractInsnNode ain = m.instructions.get(i);
			if (ain.getOpcode() == Opcodes.LDC) {
				MethodInsnNode nbt = (MethodInsnNode)ain.getNext(); // get from
				// NBT
				nbt.name = FMLForgePlugin.RUNTIME_DEOBF ? "func_74762_e" : "getInteger";
				nbt.desc = "(Ljava/lang/String;)I";
				break;
			}
		}
	}

}
