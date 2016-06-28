package reika.dragonapi.asm.patchers.hooks.event;

import net.minecraftforge.classloading.FMLForgePlugin;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import reika.dragonapi.asm.patchers.Patcher;
import reika.dragonapi.libraries.java.ReikaASMHelper;

public class TileUpdate extends Patcher {

	public TileUpdate() {
		super("net.minecraft.world.World", "ahb");
	}

	@Override
	protected void apply(ClassNode cn) {
		MethodNode m = ReikaASMHelper.getMethodByName(cn, "func_72939_s", "updateEntities", "()V");
		String name = FMLForgePlugin.RUNTIME_DEOBF ? "func_145837_r" : "isInvalid";
		String name2 = FMLForgePlugin.RUNTIME_DEOBF ? "func_72899_e" : "blockExists";
		MethodInsnNode loc1 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/tileentity/TileEntity", name, "()Z");
		MethodInsnNode loc2 = ReikaASMHelper.getFirstMethodCall(cn, m, "net/minecraft/world/World", name2, "(III)Z");
		JumpInsnNode jump = (JumpInsnNode)loc2.getNext();
		jump.setOpcode(Opcodes.IFNE);
		ReikaASMHelper.deleteFrom(m.instructions, loc1, loc2);
		m.instructions.insertBefore(jump, new MethodInsnNode(Opcodes.INVOKESTATIC, "Reika/DragonAPI/Instantiable/Event/TileUpdateEvent", "fire", "(Lnet/minecraft/tileentity/TileEntity;)Z", false));
	}
}
