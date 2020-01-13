package Reika.DragonAPI.ASM.Patchers.Fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import Reika.DragonAPI.ASM.Patchers.Patcher;
import Reika.DragonAPI.Libraries.Java.ReikaASMHelper;

public class ClearUnregOreWarning extends Patcher {

	public ClearUnregOreWarning() {
		super("net.minecraftforge.oredict.OreDictionary");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "getOreIDs", "(Lnet/minecraft/item/ItemStack;)[I");
		AbstractInsnNode ain = ReikaASMHelper.getFirstOpcode(m.instructions, Opcodes.LDC);
		ain = ReikaASMHelper.getFirstInsnAfter(m.instructions, m.instructions.indexOf(ain), Opcodes.ALOAD, 0);
		m.instructions.insert(ain, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/ASMCalls", "getUnregisteredOreStackIdentification", "(Lnet/minecraft/item/ItemStack;)Ljava/lang/Object;", false));
	}

}
