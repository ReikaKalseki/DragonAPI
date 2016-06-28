package reika.dragonapi.asm.patchers.fixes;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class PermutedBiomeReg extends Patcher {

	public PermutedBiomeReg() {
		super("net.minecraftforge.common.BiomeDictionary");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "registerVanillaBiomes", "()V");
		m.instructions.insertBefore(m.instructions.getLast(), new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/ASM/DragonAPIClassTransformer", "registerPermutedBiomesToDictionary", "()V", false));
	}
}
